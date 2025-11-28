package com.example.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class StompChatController {

    @MessageMapping("/chats") //클라이언트에서 요청을 보내면 chats로 이동함 (/pub/chats) -> 쓸 때 pub은 생략되고 뒤에 url만 씀
    @SendTo("/sub/chats") //에게 전달
    public String handleMessage(@Payload String message) {
        log.info("{} recived ",message);

        return message;
    }
}
