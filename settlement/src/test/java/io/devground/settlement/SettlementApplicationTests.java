package io.devground.settlement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.devground.dbay.SettlementApplication;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = SettlementApplication.class)
class SettlementApplicationTests {

	@Test
	void contextLoads() {
	}

}
