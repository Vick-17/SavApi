package com.springTemplate.springTemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SpringTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTemplateApplication.class, args);
	}

}
