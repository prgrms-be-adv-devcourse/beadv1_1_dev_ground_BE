package io.devground.core.model.vo;

import io.devground.core.model.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// common
	CODE_INVALID(400, "잘못된 코드 형식입니다."),
	METHOD_NOT_ALLOWED(400, "잘못된 Http Method 입니다."),

	// user/auth
	UNAUTHORIZED(401, "로그인이 필요합니다."),
	USER_NOT_FOUNT(404, "사용자를 찾을 수 없습니다."),
	CODE_EXPIRED(400, "인증 코드가 만료되었습니다."),
	WRONG_VERIFICATION_CODE(400, "인증번호가 올바르지 않습니다."),
	NOT_VERIFICATION_EMAIL(400, "이메일이 인증되지 않았습니다."),
	INVALID_TOKEN(400, "잘못된 토큰입니다."),
	INVALID_PASSWORD(400, "잘못된 비밀번호입니다."),
	EMPTY_REFRESH_TOKEN(400, "refresh 토큰이 없습니다."),
	EXPIRED_REFRESH_TOKEN(400, "refresh 토큰이 만료되었습니다."),

	// cart
	CART_ITEM_DELETE_NOT_SELECTED(400, "삭제할 상품이 선택되지 않았습니다."),
	CART_ITEM_ALREADY_EXIST(409, "장바구니에 이미 담긴 상품입니다."),
	CART_ALREADY_EXIST(409, "장바구니가 이미 존재합니다."),
	CART_NOT_FOUND(404, "장바구니를 찾을 수 없습니다."),
	DELETE_CART_ITEM_FAILED(400, "장바구니 상품 삭제를 실패했습니다."),

	// order
	ORDER_ITEM_NOT_SELECTED(400, "상품이 선택되지 않았습니다."),
	ORDER_ITEM_ALREADY_SOLD(400, "주문 목록에 이미 판매된 상품이 있습니다."),
	ADDRESS_NOT_FOUND(404, "해당 주소를 찾을 수 없습니다."),
	ORDER_NOT_FOUND(404, "해당 주문을 찾을 수 없습니다."),
	ORDER_ALREADY_CANCELLED(400, "이미 취소된 주문입니다."),
	ORDER_ALREADY_DELIVERED(400, "이미 배송완료된 주문입니다."),
	ORDER_CANCELLED_NOT_ALLOWED_WHEN_DELIVERED(400, "배송완료된 상품에 대해선 주문 취소를 할 수 없습니다."),
	ORDER_CANCELLED_NOT_ALLOWED_WHEN_CONFIRMED(400, "구매확정된 상품에 대해선 주문 취소를 할 수 없습니다."),
	ORDER_CONFIRM_NOT_ALLOWED(400, "배송완료 전에는 구매확정을 할 수 없습니다."),
	ORDER_ALREADY_CONFIRMED(400, "이미 구매확정된 주문입니다."),

	// product
	ONLY_ON_SALE_PRODUCT_CHANGEABLE(400, "판매 중인 상품만 판매 완료 처리할 수 있습니다."),
	SOLD_PRODUCT_CANNOT_PURCHASE(400, "이미 판매된 상품입니다."),
	SOLD_PRODUCT_CANNOT_UPDATE(400, "이미 판매된 상품 내용은 변경할 수 없습니다."),
	PRODUCT_MUST_WITH_LEAF_CATEGORY(400, "상품은 반드시 최종 카테고리를 가져야 합니다."),
	PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다."),

	// category
	CANNOT_EXCEED_MAX_DEPTH(400, "카테고리는 최대 뎁스를 초과할 수 없습니다."),
	MISMATCH_ON_DEPTH(400, "해당 카테고리의 하위에 등록할 수 없습니다."),
	CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다."),

	// image
	IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다."),

	// s3
	INVALID_FILE_EXTENSION(400, "지원하지 않는 확장자입니다."),
	S3_UPLOAD_FAILED(500, "S3 업로드를 실패하였습니다."),
	S3_PRESIGNED_URL_GENERATION_FAILED(500, "PresignedURL 생성에 실패하였습니다."),
	S3_OBJECT_DELETE_FAILED(500, "S3 오브젝트 제거에 실패하였습니다."),
	S3_OBJECT_GET_FAILED(500, "S3 오브젝트 호출에 실패하였습니다."),

	// deposit
	DEPOSIT_ALREADY_EXISTS(409, "이미 예금 계정이 존재합니다."),
	DEPOSIT_NOT_FOUND(404, "예치금 계정을 찾을 수 없습니다."),
	DEPOSIT_HISTORY_NOT_FOUND(404, "거래 내역을 찾을 수 없습니다."),
	AMOUNT_MUST_BE_POSITIVE(400, "금액은 0보다 커야 합니다."),
	INSUFFICIENT_BALANCE(400, "잔액이 부족합니다."),

	// saga
	SAGA_NOT_FOUND(404, "사가 정보를 찾을 수 없습니다.");

	private final int httpStatus;
	private final String message;

	public ServiceException throwServiceException() {
		throw new ServiceException(this);
	}

	public ServiceException throwServiceException(Throwable cause) {
		throw new ServiceException(this, cause);
	}
}
