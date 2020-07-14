package com.adcb.kfk.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

//import org.springframework.boot.web.support.SpringBootServletInitializer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.adcb")
@SpringBootApplication
@EnableScheduling
//@EnableWebMvc
public class CBPortalReconStream /*extends SpringBootServletInitializer*/{
	public static final void main(String args[]){
		SpringApplication.run(CBPortalReconStream.class, args);
	}

/*	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CBPortalReconStream.class);
	}*/
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}
}