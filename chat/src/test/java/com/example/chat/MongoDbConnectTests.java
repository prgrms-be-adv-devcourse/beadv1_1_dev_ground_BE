//package com.example.chat;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.MongoCursor;
//import org.bson.Document;
//import org.junit.jupiter.api.Test;
//
//public class MongoDbConnectTests {
//
//    @Test
//    void mongo_db_quickstart_test(){
//        String uri = "mongodb+srv://han0103166_db_user:TSco46ZwRz1JULRd@chat.swwrm69.mongodb.net/?appName=chat";
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
//            MongoCollection<Document> rooms = database.getCollection("room");
//            MongoCollection<Document> chats = database.getCollection("chats");
//            try (MongoCursor<Document> cursor = collection.find("유저아이디, 코드").iterator()) {
//                // 내가 접속해 있는 방정보들
//                while (cursor.hasNext()) {
//                    System.out.println(cursor.next().toJson());
//
//                    MongoCursorsor<Document> cursor = collection.find("roomCode").top(10).orderBy(desc).iterator()
//                    {
//
//                    }
//                }
//            }
//        }
//    }
//
//}
