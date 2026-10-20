package com.nutritionlabs.properties;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Reads the messages of messages.properties.
 */
@Component
@PropertySource("classpath:messages.properties")
public class PropertiesReader {

	@Autowired
	private Environment environment;

	public String getProperty(String propertyName, String... args) {
		String property = environment.getProperty(propertyName);
		if (property == null || args == null || args.length == 0) {
			return property;
		}
		return MessageFormat.format(property, (Object[]) args);
	}
}
