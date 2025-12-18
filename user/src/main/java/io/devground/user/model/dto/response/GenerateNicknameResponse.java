package io.devground.user.model.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class GenerateNicknameResponse {
	private List<String> words;  // API 응답: {"words": ["멋진", "호랑이"]}
}
