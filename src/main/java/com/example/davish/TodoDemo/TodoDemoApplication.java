package com.example.davish.TodoDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class TodoDemoApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	private static final Logger log = LoggerFactory.getLogger(TodoDemoApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TodoDemoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(TodoDemoApplication.class, args);
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToLocalDateTimeConverter());
	}
	
	@Bean
	public Converter<String, LocalDateTime> stringToLocalDateTimeConverter() {
		return new StringToLocalDateTimeConverter();
	}
	
	private static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		@Override
		public LocalDateTime convert(String source) {
			if (source == null || source.trim().isEmpty()) {
				return null;
			}
			try {
				return LocalDateTime.parse(source.trim(), FORMATTER);
			} catch (Exception e) {
				log.warn("Failed to parse date: {}", source);
				return null;
			}
		}
	}
}
