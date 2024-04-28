package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	//设置环境变量 GOOGLE_APPLICATION_CREDENTIALS="F:\IDEA\doc\rice-comp-539-spring-2022-fcffacde4281.json"
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("GOOGLE_APPLICATION_CREDENTIALS:::"+System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
	}

}
