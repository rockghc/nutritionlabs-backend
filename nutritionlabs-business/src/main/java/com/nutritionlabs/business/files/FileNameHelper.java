package com.nutritionlabs.business.files;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nutritionlabs.exceptions.FileNameException;

/**
 * Generates the unique names of the uploaded files.
 */
@Component
public class FileNameHelper {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("HHmmss_ddMMyyyy");

	public String generateUniqueNumber() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
	}

	public String generateFileName(String fileName) {
		int indexOfExtension = fileName.indexOf('.');
		String extensionName = indexOfExtension >= 0 ? fileName.substring(indexOfExtension) : "";
		return LocalDateTime.now().format(DATE_FORMAT) + "_" + generateUniqueNumber() + extensionName;
	}

	public String generateDisplayName(String originalFileName) {
		String originalCleanPath = StringUtils.cleanPath(originalFileName == null ? "" : originalFileName);
		if (originalCleanPath.contains("..")) {
			throw new FileNameException("Sorry! Filename contains invalid path sequence " + originalCleanPath);
		}
		return generateFileName(originalCleanPath);
	}
}
