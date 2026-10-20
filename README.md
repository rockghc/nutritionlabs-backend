# nutritionlabs-backend

Single Spring Boot application that replaces the NutritionLabs microservices
(`freedom-user-service`, `nutritionlabs-catalog-service`, `nutritionlabs-food-service`,
`nutritionlabs-recipe-service`, `nutritionlabs-file-service`, `nutritionlabs-report-service`
and `nutritionlabs-custom-commons`). Eureka, Zuul, Spring Boot Admin and Kafka are not needed anymore.

| Component      | Version                                   |
|----------------|-------------------------------------------|
| Java           | 25 (`E:\Tools\jdk-25.0.2`)                |
| Spring Boot    | 4.1.1                                     |
| Keycloak       | existing server, OAuth2 resource server   |
| PostgreSQL     | `NutritionLabsBD`, schema `nutritionlabs` |
| JasperReports  | 6.21.5 (templates made with Jaspersoft Studio 6.20.5) |

## Modules

Same layers as `freedom-user-service`:

| Module                      | Content                                                    |
|-----------------------------|------------------------------------------------------------|
| `nutritionlabs-domain`      | Entities, DTOs and MapStruct mappers                       |
| `nutritionlabs-data-access` | Spring Data repositories and native queries                |
| `nutritionlabs-business`    | Business services, report generation, report templates    |
| `nutritionlabs-service`     | REST controllers, error handling                           |
| `nutritionlabs-boot`        | Application class, security and configuration             |

The REST paths keep the prefixes of the former gateway (`/resourceUser`, `/resourceCatalog`, `/resourceFood`,
`/resourceRecipe`, `/resourceFile`, `/resourceReport`) and the application listens on port 8083, the port of
the gateway, so the Angular application works without changes.

## Database

Scripts of the `database` folder, run once as `postgres`:

```powershell
$env:PGPASSWORD = 'Elvish'
$psql = 'C:\Program Files\PostgreSQL\17\bin\psql.exe'
& $psql -h localhost -U postgres -d postgres -f database\01_create_database.sql
& $psql -h localhost -U postgres -d NutritionLabsBD -f database\02_create_schema.sql
.\database\03_migrate_data.ps1
& $psql -h localhost -U postgres -d NutritionLabsBD -f database\04_after_data_migration.sql
```

Tables renamed because their names collided after the merge:

| Former table                          | New table                      |
|---------------------------------------|--------------------------------|
| `NLabsUserBD.nlabs_tags`              | `nlabs_tags_user`              |
| `NLabsRecipesBD.nlabs_tags`           | `nlabs_tags_recipe`            |
| `NLabsFoodsBD.nlabs_common_mesure`    | `nlabs_food_common_measures`   |
| `NLabsRecipesBD.nlabs_common_measure` | `nlabs_recipe_common_measures` |

Hibernate validates the schema at startup (`ddl-auto: validate`); it never changes it.

## Keycloak

The API validates the access tokens with the keys of the realm `freedom-realm-keycloak`
(`KEYCLOAK_URL`, default `http://localhost:8080`, and `KEYCLOAK_REALM`). Every endpoint requires the realm or
client role `user` (`nutritionlabs.security.required-role`). The username is the `preferred_username` claim.

## Run

```powershell
$env:JAVA_HOME = 'E:\Tools\jdk-25.0.2'
mvn clean package
java -jar nutritionlabs-boot\target\nutritionlabs-boot-0.0.1-SNAPSHOT.jar
```

Database credentials: `DB_USERNAME` / `DB_PASSWORD` (default `postgres` / `Elvish`).

## Tests

```powershell
mvn test                                                 # unit tests
mvn verify -Dgroups=integration -Dsurefire.excludedGroups=   # API tests against NutritionLabsBD, rolled back
```

## Reports

The application fills the compiled templates (`.jasper`) of
`nutritionlabs-business/src/main/resources/reports/recipe`. After changing a `.jrxml` in Jaspersoft Studio,
compile it there and replace the `.jasper` file.
