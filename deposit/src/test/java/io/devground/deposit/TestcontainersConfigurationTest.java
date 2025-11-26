package io.devground.deposit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TestcontainersConfigurationTest {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PostgreSQLContainer<?> postgresContainer;

	@Test
	void testPostgresContainerIsRunning() {
		assertNotNull(postgresContainer);
		assertTrue(postgresContainer.isRunning());
	}

	@Test
	void testDataSourceConnection() throws Exception {
		assertNotNull(dataSource);
		try (Connection connection = dataSource.getConnection()) {
			assertTrue(connection.isValid(1));
		}
	}
}