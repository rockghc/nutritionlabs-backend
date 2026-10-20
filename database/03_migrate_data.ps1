<#
.SYNOPSIS
  Copies the data of the former microservice databases into the merged "nutritionlabs" schema.

.DESCRIPTION
  For every source database the script runs pg_dump (data only), points every COPY statement to the
  "nutritionlabs" schema (renaming the tables that collided after the merge) and loads it into
  NutritionLabsBD in a single transaction. Foreign key triggers are disabled while loading, so the
  table order of the dumps does not matter. The source databases are only read.

  Run it after 01_create_database.sql and 02_create_schema.sql, against an empty schema, and then run
  04_after_data_migration.sql.

.EXAMPLE
  $env:PGPASSWORD = 'Elvish'
  .\03_migrate_data.ps1
#>
param(
    [string]$PgBin = 'C:\Program Files\PostgreSQL\17\bin',
    [string]$PgHost = 'localhost',
    [int]$PgPort = 5432,
    [string]$PgUser = 'postgres',
    [string]$TargetDatabase = 'NutritionLabsBD',
    [string]$TargetSchema = 'nutritionlabs'
)

$ErrorActionPreference = 'Stop'

# Source database -> table renames needed in the merged schema.
$sources = [ordered]@{
    'NLabsCatalogsBD' = @{}
    'NLabsFoodsBD'    = @{ 'nlabs_common_mesure' = 'nlabs_food_common_measures' }
    'NLabsRecipesBD'  = @{ 'nlabs_tags' = 'nlabs_tags_recipe'; 'nlabs_common_measure' = 'nlabs_recipe_common_measures' }
    'NLabsUserBD'     = @{ 'nlabs_tags' = 'nlabs_tags_user' }
    'NLabsFileBD'     = @{}
}

$pgDump = Join-Path $PgBin 'pg_dump.exe'
$psql = Join-Path $PgBin 'psql.exe'
$workDir = Join-Path ([IO.Path]::GetTempPath()) 'nutritionlabs-migration'
New-Item -ItemType Directory -Force -Path $workDir | Out-Null
$utf8 = New-Object System.Text.UTF8Encoding($false)
$copyPattern = '^COPY public\.(?<table>[A-Za-z0-9_]+) '

foreach ($database in $sources.Keys) {
    $renames = $sources[$database]
    $dumpFile = Join-Path $workDir "$database.data.sql"
    $loadFile = Join-Path $workDir "$database.load.sql"

    Write-Host "Dumping $database ..."
    & $pgDump -h $PgHost -p $PgPort -U $PgUser -d $database --data-only --no-owner --no-privileges --schema=public -f $dumpFile
    if ($LASTEXITCODE -ne 0) { throw "pg_dump failed for $database" }

    $reader = New-Object System.IO.StreamReader($dumpFile, $utf8)
    $writer = New-Object System.IO.StreamWriter($loadFile, $false, $utf8)
    try {
        $writer.WriteLine('SET session_replication_role = replica;')
        while ($null -ne ($line = $reader.ReadLine())) {
            if ($line.StartsWith('SELECT pg_catalog.setval(')) {
                # Identity sequences are reset by 04_after_data_migration.sql.
                continue
            }
            $match = [regex]::Match($line, $copyPattern)
            if ($match.Success) {
                $table = $match.Groups['table'].Value
                $target = if ($renames.ContainsKey($table)) { $renames[$table] } else { $table }
                $line = "COPY $TargetSchema.$target " + $line.Substring($match.Length)
            }
            $writer.WriteLine($line)
        }
    }
    finally {
        $reader.Close()
        $writer.Close()
    }

    Write-Host "Loading $database into $TargetDatabase.$TargetSchema ..."
    & $psql -h $PgHost -p $PgPort -U $PgUser -d $TargetDatabase -v ON_ERROR_STOP=1 --single-transaction -q -f $loadFile
    if ($LASTEXITCODE -ne 0) { throw "Loading data of $database failed" }
}

Remove-Item -Recurse -Force $workDir
Write-Host 'Data migrated. Now run 04_after_data_migration.sql.'
