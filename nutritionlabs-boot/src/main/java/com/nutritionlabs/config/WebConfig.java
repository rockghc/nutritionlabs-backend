package com.nutritionlabs.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class WebConfig {

	/**
	 * The front end calls many URLs with a trailing slash ("/resourceUser/user/"). Spring no longer matches them
	 * with the mappings without it, so the slash is removed before the request reaches the security filters.
	 */
	@Bean
	public FilterRegistrationBean<UrlHandlerFilter> trailingSlashFilter() {
		FilterRegistrationBean<UrlHandlerFilter> registration = new FilterRegistrationBean<>(
				UrlHandlerFilter.trailingSlashHandler("/**").wrapRequest().build());
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}
}
