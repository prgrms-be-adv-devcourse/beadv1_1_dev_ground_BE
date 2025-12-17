package io.devground.chat.service;

import io.devground.chat.client.ProductClient;
import io.devground.chat.enums.ChatRoomStatus;
import io.devground.chat.model.dto.request.CartProductsRequest;
import io.devground.chat.model.dto.response.CartProductsResponse;
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

import java.util.ArrayList;
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
        List<String> productCodes = rooms.stream().map(ChatRoom::getProductCode).toList();
        BaseResponse<List<CartProductsResponse>> cartProducts = productClient.getCartProducts(new CartProductsRequest(productCodes));

        List<ChatRoomSummary> list = new ArrayList<>();
        for (ChatRoom chatRoom : rooms) {
            ChatMessages last = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(chatRoom.getId());
            long unread = messageRepository.countByChatIdAndSenderCodeNotAndIsReadFalse(chatRoom.getId(), userCode);
            //String productTitle = resolveProductTitle(chatRoom.getProductCode(), userCode);
            ChatRoomSummary apply = ChatRoomSummary.builder()
                    .id(chatRoom.getId())
                    .productCode(chatRoom.getProductCode())
                    //.productTitle(productTitle)
                    .sellerCode(chatRoom.getSellerCode())
                    .buyerCode(chatRoom.getBuyerCode())
                    .status(chatRoom.getStatus())
                    .lastMessage(last != null ? last.getMessage() : null)
                    .lastMessageAt(last != null ? last.getCreatedAt() : null)
                    .unreadCount(unread)
                    .build();
            list.add(apply);
        }
        return list;
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
