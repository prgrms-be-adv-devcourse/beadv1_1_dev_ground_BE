package io.devground.chat.service;

import io.devground.chat.client.ProductClient;
import io.devground.chat.enums.ChatRoomStatus;
import io.devground.chat.model.dto.response.ChatRoomSummary;
import io.devground.chat.model.dto.response.ProductDetailResponse;
import io.devground.chat.model.entity.ChatMessages;
import io.devground.chat.model.entity.ChatRoom;
import io.devground.chat.repository.MessageRepository;
import io.devground.chat.repository.RoomRepository;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final ProductClient productClient;

    private String resolveProductTitle(String productCode, String userCode) {
        try {
            BaseResponse<ProductDetailResponse> response = productClient.getProductDetail(productCode, userCode);
            if (response != null) {
                response.throwIfNotSuccess();
            }
            ProductDetailResponse detail = response != null ? response.data() : null;
            if (detail != null && detail.title() != null && !detail.title().isBlank()) {
                return detail.title();
            }
        } catch (Exception e) {
            log.warn("상품 제목 조회 실패: productCode={}, userCode={}", productCode, userCode, e);
        }
        return productCode;
    }

    public ChatRoom getOrCreateRoom(String productCode, String sellerCode, String buyerCode) {
        return roomRepository
                .findByProductCodeAndSellerCodeAndBuyerCode(productCode, sellerCode, buyerCode)
                .orElseGet(() -> roomRepository.save(
                        ChatRoom.builder()
                                .productCode(productCode)
                                .sellerCode(sellerCode)
                                .buyerCode(buyerCode)
                                .status(ChatRoomStatus.OPEN)
                                .build()
                ));

    }

    public ChatRoom getRoom(String chatId) {
        return roomRepository.findById(chatId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }


    //OPEN상태인 채팅방 조회
    public List<ChatRoomSummary> listOpenRoomsForUser(String userCode) {
        List<ChatRoom> rooms = roomRepository.findByStatusAndSellerCodeOrStatusAndBuyerCode(
                ChatRoomStatus.OPEN, userCode, ChatRoomStatus.OPEN, userCode
        );

        return rooms.stream().map(room -> {
            ChatMessages last = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(room.getId());
            long unread = messageRepository.countByChatIdAndSenderCodeNotAndIsReadFalse(room.getId(), userCode);
            String productTitle = resolveProductTitle(room.getProductCode(), userCode);
            return ChatRoomSummary.builder()
                    .id(room.getId())
                    .productCode(room.getProductCode())
                    .productTitle(productTitle)
                    .sellerCode(room.getSellerCode())
                    .buyerCode(room.getBuyerCode())
                    .status(room.getStatus())
                    .lastMessage(last != null ? last.getMessage() : null)
                    .lastMessageAt(last != null ? last.getCreatedAt() : null)
                    .unreadCount(unread)
                    .build();
        }).collect(Collectors.toList());
    }

    public ChatRoom leaveRoom(String chatId, String userCode) {
        ChatRoom room = getRoom(chatId);
        boolean isParticipant = userCode.equals(room.getSellerCode()) || userCode.equals(room.getBuyerCode());
        if (!isParticipant) {
            throw new ServiceException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        room.close();
        return roomRepository.save(room);
    }
}
