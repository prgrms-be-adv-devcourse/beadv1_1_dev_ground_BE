package com.example.chat.repository;

import com.example.chat.model.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<ChatRoom, Long> {
    // 동일 상품, 동일 셀러, 동일 바이어 조합으로 방 찾기
    Optional<ChatRoom> findByProductCodeAndSellerCodeAndBuyerCode(
            Long productCode, Long sellerCode, Long buyerCode
    );
}
