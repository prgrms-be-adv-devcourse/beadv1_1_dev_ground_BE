package io.devground.deposit;

import org.springframework.boot.SpringApplication;

public class TestDepositApplication {

	public static void main(String[] args) {
		SpringApplication.from(DepositApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
