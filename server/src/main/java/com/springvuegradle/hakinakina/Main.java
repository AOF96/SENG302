package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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
	CommandLineRunner init(UserRepository userRepository,
						   PassportCountryRepository countryRepository,
						   EmailRepository emailRepository,
						   SessionRepository sessionRepository,
						   ActivityTypeRepository activityTypeRepository,
						   ActivityRepository activityRepository,
						   SearchRepository searchRepository,
						   UserActivityRoleRepository userActivityRoleRepository,
						   AchievementRepository achievementRepository,
						   LocationRepository locationRepository,
						   HomeFeedRepository homeFeedRepository
	) {
		return args -> {
			URL url = new URL("https://restcountries.eu/rest/v2/all");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			JSONParser parser = new JSONParser();
			JSONArray data = (JSONArray) parser.parse(rd.readLine());

			for (Object country : data) {
				JSONObject countryJson = (JSONObject) country;
				String name = countryJson.get("name").toString();
				String code = countryJson.get("alpha2Code").toString();
				countryRepository.save(new PassportCountry(code, name));
			}

			BufferedReader bufReader = new BufferedReader(new FileReader("./src/main/resources/activityTypes.txt"));
			ArrayList<String> listOfActivityTypes = new ArrayList<>();
			String activity = bufReader.readLine();
			while (activity != null) {
				activity = activity.substring(1,activity.length()-1);
				listOfActivityTypes.add(activity);
				activity = bufReader.readLine();
			}
			for (String activityType : listOfActivityTypes) {
				activityTypeRepository.save(new ActivityType(activityType));
			}
		};
	}


	// Fix the CORS errors
	@Bean
	public FilterRegistrationBean simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		// *** URL below needs to match the Vue client URL and port ***
		config.setAllowedOrigins(new ArrayList(Arrays.asList("http://127.0.0.1:9500", "http://127.0.0.1:9499",
				"http://localhost:9000", "http://localhost:9500", "https://csse-s302g3.canterbury.ac.nz/test",
				"https://csse-s302g3.canterbury.ac.nz/prod", "https://csse-s302g3.canterbury.ac.nz")));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

}