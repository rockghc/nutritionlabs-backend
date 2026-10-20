package com.nutritionlabs.business.reports;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Fills the compiled JasperReports templates (.jasper) stored in the classpath and exports them to PDF.
 *
 * <p>The templates are compiled with Jaspersoft Studio from the .jrxml files next to them. The resources
 * referenced by name in a template (images, subreports) are looked up in the folder of the template.
 */
@Service
public class ReportGenerator {

	private final ConcurrentMap<String, JasperReport> reports = new ConcurrentHashMap<>();

	/**
	 * @param reportFolder classpath folder of the template, ending with "/"
	 * @param reportName   file name of the compiled template
	 * @param parameters   report parameters
	 * @param datasource   beans of the main dataset
	 */
	public byte[] generatePdf(String reportFolder, String reportName, Map<String, Object> parameters, Collection<?> datasource) throws JRException {
		Map<String, Object> parametersReport = new HashMap<>(parameters);
		parametersReport.put(JRParameter.REPORT_CLASS_LOADER, new FolderResourcesClassLoader(reportFolder, getClass().getClassLoader()));
		JasperPrint jasperPrint = JasperFillManager.fillReport(getReport(reportFolder + reportName), parametersReport,
				new JRBeanCollectionDataSource(datasource));
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	private JasperReport getReport(String location) throws JRException {
		JasperReport report = reports.get(location);
		if (report == null) {
			try (InputStream inputStream = new ClassPathResource(location).getInputStream()) {
				report = (JasperReport) JRLoader.loadObject(inputStream);
			} catch (IOException e) {
				throw new JRException("Report template " + location + " not found", e);
			}
			reports.putIfAbsent(location, report);
		}
		return report;
	}

	/**
	 * Class loader that resolves the resources that are not found in the parent inside a folder of the classpath.
	 */
	private static final class FolderResourcesClassLoader extends ClassLoader {

		private final String folder;

		private FolderResourcesClassLoader(String folder, ClassLoader parent) {
			super(parent);
			this.folder = folder;
		}

		@Override
		protected URL findResource(String name) {
			return getParent().getResource(folder + name);
		}

		@Override
		protected Enumeration<URL> findResources(String name) throws IOException {
			URL resource = findResource(name);
			return resource == null ? Collections.emptyEnumeration() : Collections.enumeration(java.util.List.of(resource));
		}
	}
}
