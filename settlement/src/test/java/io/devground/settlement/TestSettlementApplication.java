package io.devground.settlement;

import org.springframework.boot.SpringApplication;

import io.devground.dbay.SettlementApplication;

public class TestSettlementApplication {

	public static void main(String[] args) {
		SpringApplication.from(SettlementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
