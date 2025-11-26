package io.devground.deposit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
<<<<<<< HEAD
import org.springframework.test.context.ActiveProfiles;

=======
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
>>>>>>> cfbedd3 ([REFACTOR] Deposit MSA모듈 분리)
@SpringBootTest
class DepositApplicationTests {

	@Test
	void contextLoads() {
	}

}
