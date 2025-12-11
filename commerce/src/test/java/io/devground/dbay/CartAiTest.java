package io.devground.dbay;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = "spring.ai.openai.api-key=${OPEN_AI_API_KEY}")
public class CartAiTest {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    void pingOpenAi() {
        ChatClient chatClient = chatClientBuilder.build();

        String reply = chatClient.prompt()
                .system("You are a helpful assistant")
                .user("안녕! 한 줄로 답해줘.")
                .call()
                .content();

        System.out.println("대답: " + reply);
        assertThat(reply).isNotBlank();
    }
}
