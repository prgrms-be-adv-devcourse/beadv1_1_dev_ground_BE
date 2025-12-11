package io.devground.product.product.application.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ChatAiTest {

	@Autowired
	ChatClient.Builder chatClientBuilder;

	@Test
	@DisplayName("Spring AI 작동 테스트")
	void test_open_AI_response() throws Exception {
		ChatClient chatClient = chatClientBuilder.build();

		String reply = chatClient.prompt()
			.system("you are a soccer manager")
			.user("너의 직업은 뭐야?")
			.call()
			.content();

		System.out.println("답변: " + reply);
		assertThat(reply).isNotBlank();
	}
}
