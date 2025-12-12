package com.example.chat.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private String productCode;
    private String sellerCode;
    private String buyerCode;
}
