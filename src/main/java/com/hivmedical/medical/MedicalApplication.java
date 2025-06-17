package com.hivmedical.medical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalApplication.class, args);
		System.out.println("Medical Application Started");
	}
}
