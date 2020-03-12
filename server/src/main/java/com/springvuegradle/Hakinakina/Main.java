package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.entity.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * Main class for application
 */
@SpringBootApplication
public class Main {

	/**
	 * Launches Spring application
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PassportCountryRepository countryRepository, EmailRepository emailRepository) {
		return args -> {
			String[] countryCodes = Locale.getISOCountries();
			for (String countryCode : countryCodes) {
				Locale obj = new Locale("", countryCode);
				countryRepository.save(new PassportCountry(obj.getCountry(), obj.getDisplayCountry()));
			}

			// For debugging purposes
			User u = new User("You", "Me", "triplej@msn.com", null, null, 4, "password");
			u.addEmail(new Email("triplej@google.com"));
		    u.addEmail(new Email("triplej@xtra.co.nz"));
			userRepository.save(u);
		};
	}

	// Fix the CORS errors
	@Bean
	public FilterRegistrationBean simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		// *** URL below needs to match the Vue client URL and port ***
		config.setAllowedOrigins(new ArrayList(Arrays.asList("http://localhost:9000", "http://localhost:9500", "https://csse-s302g0.canterbury.ac.nz/test", "https://csse-s302g0.canterbury.ac.nz/prod")));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

}