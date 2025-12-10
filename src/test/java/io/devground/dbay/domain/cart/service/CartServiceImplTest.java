//package io.devground.dbay.domain.cart.service;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import io.devground.core.model.exception.ServiceException;
//import io.devground.core.model.vo.DeleteStatus;
//import io.devground.core.model.vo.ErrorCode;
//import io.devground.core.model.web.BaseResponse;
//import io.devground.dbay.domain.cart.cart.model.entity.Cart;
//import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
//import io.devground.dbay.domain.cart.cart.model.vo.CartProductsRequest;
//import io.devground.dbay.domain.cart.cart.model.vo.CartProductsResponse;
//import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
//import io.devground.dbay.domain.cart.cart.model.vo.GetItemsByCartResponse;
//import io.devground.dbay.domain.cart.cart.model.vo.ProductDetailResponse;
//import io.devground.dbay.domain.cart.cart.repository.CartRepository;
//import io.devground.dbay.domain.cart.cart.service.CartServiceImpl;
//import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
//import io.devground.dbay.domain.cart.cartItem.repository.CartItemRepository;
//import io.devground.dbay.domain.cart.infra.client.ProductFeignClient;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@ExtendWith(MockitoExtension.class)
//class CartServiceImplTest {
//
//	@Mock
//	private CartRepository cartRepository;
//
//	@Mock
//	private CartItemRepository cartItemRepository;
//
//	@Mock
//	private ProductFeignClient productFeignClient;
//
//	private BaseResponse<ProductDetailResponse> onSaleResponse;
//	private BaseResponse<ProductDetailResponse> soldResponse;
//
//	@InjectMocks
//	private CartServiceImpl cartService;
//
//	@BeforeEach
//	void setUp() {
//		ProductDetailResponse onSaleDetail = new ProductDetailResponse(
//			null,
//			null,
//			null,
//			null,
//			null,
//			null,
//			0L,
//			"ON_SALE",
//			null
//		);
//
//		ProductDetailResponse soldDetail = new ProductDetailResponse(
//			null,
//			null,
//			null,
//			null,
//			null,
//			null,
//			0L,
//			"SOLD",
//			null
//		);
//
//		onSaleResponse = BaseResponse.success(200, onSaleDetail);
//		soldResponse = BaseResponse.success(200, soldDetail);
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 생성")
//	void createCart_success() {
//
//		String userCode = UUID.randomUUID().toString();
//		given(cartRepository.findByUserCode(userCode)).willReturn(Optional.empty());
//
//		Cart cart = Cart.builder()
//			.userCode(userCode)
//			.build();
//		given(cartRepository.save(any(Cart.class))).willReturn(cart);
//
//		Cart result = cartService.createCart(userCode);
//
//		assertThat(result.getUserCode()).isEqualTo(userCode);
//		verify(cartRepository).findByUserCode(userCode);
//		verify(cartRepository).save(any(Cart.class));
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 생성에서 코드 유효한 값")
//	void createCart_throwException_whenInvalidCode() {
//		String userCode = "invalid-code";
//
//		assertThatThrownBy(() -> cartService.createCart(userCode))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
//			});
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 생성에서 이미 존재")
//	void createCart_throwException_whenCartAlreadyExists() {
//		String userCode = UUID.randomUUID().toString();
//		Cart cart = new Cart(userCode);
//
//		given(cartRepository.findByUserCode(userCode)).willReturn(Optional.of(cart));
//
//		assertThatThrownBy(() -> cartService.createCart(userCode))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ALREADY_EXIST);
//			});
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 상품 추가")
//	void addItem_success() {
//		String userCode = UUID.randomUUID().toString();
//		String productCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder()
//			.userCode(userCode)
//			.build();
//
//		given(cartItemRepository.existsByCartAndProductCode(cart, productCode))
//			.willReturn(false);
//
//		ReflectionTestUtils.setField(cart, "code", cart.getCode());
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		given(cartItemRepository.save(any(CartItem.class)))
//			.willAnswer(i -> i.getArgument(0));
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		given(productFeignClient.getProductDetail(productCode))
//			.willReturn(onSaleResponse);
//
//		CartItem result = cartService.addItem(cart.getCode(), request);
//
//		assertThat(result.getCart().getCode()).isEqualTo(cart.getCode());
//		assertThat(result.getProductCode()).isEqualTo(productCode);
//
//		verify(cartRepository).findByUserCode(cart.getCode());
//		verify(cartItemRepository).existsByCartAndProductCode(cart, productCode);
//		verify(cartRepository).findByUserCode(cart.getCode());
//		verify(cartItemRepository).save(any(CartItem.class));
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 상품 추가에서 장바구니,상품코드 유효한 값")
//	void addItem_throwException_whenInvalidCode() {
//		String cartCode = "invalid-uuid";
//		String productCode = "invalid-uuid";
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
//			});
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 상품 추가에서 장바구니 없음")
//	void addItem_throwException_whenCartNotExists() {
//		String cartCode = UUID.randomUUID().toString();
//		String productCode = UUID.randomUUID().toString();
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		given(cartRepository.findByUserCode(cartCode)).willReturn(Optional.empty());
//
//		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
//			});
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 상품 추가에서 상품 이미 존재")
//	void addItem_thrownException_whenCartItemAlreadyExists() {
//		String userCode = UUID.randomUUID().toString();
//		String productCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder().userCode(userCode).build();
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		given(cartItemRepository.existsByCartAndProductCode(cart, productCode)).willReturn(true);
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		assertThatThrownBy(() -> cartService.addItem(cart.getCode(), request))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ITEM_ALREADY_EXIST);
//			});
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 상품 추가에서 상품 이미 판매됨")
//	void addItem_thrownException_whenCartItemAlreadySold() {
//		String userCode = UUID.randomUUID().toString();
//		String productCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder().userCode(userCode).build();
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		given(cartItemRepository.existsByCartAndProductCode(cart, productCode)).willReturn(false);
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		given(productFeignClient.getProductDetail(productCode))
//			.willReturn(soldResponse);
//
//		assertThatThrownBy(() -> cartService.addItem(cart.getCode(), request))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.SOLD_PRODUCT_CANNOT_PURCHASE);
//			});
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 조회")
//	void getItemsByCart_success() {
//		String userCode = UUID.randomUUID().toString();
//
//		String productCode1 = UUID.randomUUID().toString();
//		String productCode2 = UUID.randomUUID().toString();
//
//		String sellerCode = UUID.randomUUID().toString();
//
//		String productSaleCode1 = UUID.randomUUID().toString();
//		String productSaleCode2 = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder().userCode(userCode).build();
//
//		CartItem item1 = CartItem.builder().cart(cart).productCode(productCode1).build();
//		CartItem item2 = CartItem.builder().cart(cart).productCode(productCode2).build();
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode1);
//		AddCartItemRequest request2 = new AddCartItemRequest(productCode2);
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		given(productFeignClient.getProductDetail(productCode1))
//			.willReturn(onSaleResponse);
//
//		cartService.addItem(cart.getCode(), request);
//
//		given(productFeignClient.getProductDetail(productCode2))
//			.willReturn(onSaleResponse);
//
//		cartService.addItem(cart.getCode(), request2);
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//		given(cartItemRepository.findByCart(cart)).willReturn(List.of(item1, item2));
//
//		CartProductsResponse p1 = new CartProductsResponse(productCode1, productSaleCode1, sellerCode,
//			"아이폰 프로 17", 1500000L);
//		CartProductsResponse p2 = new CartProductsResponse(productCode2, productSaleCode2, sellerCode, "맥북3 프로",
//			3500000L);
//
//		BaseResponse<List<CartProductsResponse>> response = BaseResponse.success(200, List.of(p1, p2));
//
//		given(productFeignClient
//			.getCartProducts(new CartProductsRequest(List.of(productCode1, productCode2))))
//			.willReturn(response);
//
//		GetItemsByCartResponse result = cartService.getItemsByCart(cart.getCode());
//
//		assertThat(result.totalAmount()).isEqualTo(5000000L);
//		assertThat(result.productLists()).containsExactlyInAnyOrder(p1, p2);
//		assertThat(result.productLists().size()).isEqualTo(2);
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 상품 중복 제거")
//	void getItemsByCart_duplication_success() {
//		String userCode = UUID.randomUUID().toString();
//
//		String productCode = UUID.randomUUID().toString();
//		String productSaleCode = UUID.randomUUID().toString();
//
//		String sellerCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder().userCode(userCode).build();
//
//		CartItem item1 = CartItem.builder().cart(cart).productCode(productCode).build();
//		CartItem item2 = CartItem.builder().cart(cart).productCode(productCode).build();
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//		given(cartItemRepository.existsByCartAndProductCode(cart, productCode)).willReturn(false);
//
//		given(productFeignClient.getProductDetail(productCode))
//			.willReturn(onSaleResponse);
//
//		cartService.addItem(cart.getCode(), request);
//
//		given(productFeignClient.getProductDetail(productCode))
//			.willReturn(onSaleResponse);
//
//		cartService.addItem(cart.getCode(), request);
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		given(cartItemRepository.findByCart(cart)).willReturn(List.of(item1, item2));
//
//		CartProductsResponse p1 = new CartProductsResponse(productCode, productSaleCode, sellerCode, "아이폰 프로 17",
//			1500000L);
//
//		BaseResponse<List<CartProductsResponse>> response =
//			BaseResponse.success(200, List.of(p1));
//
//		given(productFeignClient
//			.getCartProducts(new CartProductsRequest(List.of(productCode))))
//			.willReturn(response);
//
//		GetItemsByCartResponse result = cartService.getItemsByCart(cart.getCode());
//
//		assertThat(result.totalAmount()).isEqualTo(1500000L);
//		assertThat(result.productLists()).containsExactlyInAnyOrder(p1);
//		assertThat(result.productLists().size()).isEqualTo(1);
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 상품 한개 조회")
//	void getItemsByCart_only_one_success() {
//		String userCode = UUID.randomUUID().toString();
//
//		String productCode = UUID.randomUUID().toString();
//
//		String productSaleCode = UUID.randomUUID().toString();
//
//		String sellerCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder()
//			.userCode(userCode)
//			.build();
//
//		CartItem item = CartItem.builder()
//			.cart(cart)
//			.productCode(productCode)
//			.build();
//
//		AddCartItemRequest request = new AddCartItemRequest(productCode);
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		given(productFeignClient.getProductDetail(productCode))
//			.willReturn(onSaleResponse);
//
//		cartService.addItem(cart.getCode(), request);
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//		given(cartItemRepository.findByCart(cart)).willReturn(List.of(item));
//
//		CartProductsResponse p1 = new CartProductsResponse(productCode, productSaleCode, sellerCode, "아이폰 프로 17",
//			1500000L);
//
//		BaseResponse<List<CartProductsResponse>> response =
//			BaseResponse.success(200, List.of(p1));
//
//		given(productFeignClient
//			.getCartProducts(new CartProductsRequest(List.of(productCode))))
//			.willReturn(response);
//
//		GetItemsByCartResponse result = cartService.getItemsByCart(cart.getCode());
//
//		assertThat(result.totalAmount()).isEqualTo(1500000L);
//		assertThat(result.productLists()).containsExactlyInAnyOrder(p1);
//		assertThat(result.productLists().size()).isEqualTo(1);
//	}
//
//	@Test
//	@DisplayName("성공_빈 장바구니 조회")
//	void getItemsByCart_empty_success() {
//		String userCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder().userCode(userCode).build();
//
//		given(cartRepository.findByUserCode(cart.getCode())).willReturn(Optional.of(cart));
//
//		GetItemsByCartResponse result = cartService.getItemsByCart(cart.getCode());
//
//		assertThat(result.totalAmount()).isEqualTo(0L);
//		assertThat(result.productLists().size()).isEqualTo(0);
//
//		verifyNoInteractions(productFeignClient);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 조회에서 코드 유효한 값")
//	void getItemsByCart_throwException_whenInvalidCode() {
//		String cartCode = "invalid-code";
//
//		assertThatThrownBy(() -> cartService.createCart(cartCode))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
//			});
//
//		verifyNoInteractions(cartRepository, productFeignClient);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 조회에서 장바구니 없음")
//	void getItemsByCart_throwException_whenCartNotExists() {
//		String cartCode = UUID.randomUUID().toString();
//
//		given(cartRepository.findByUserCode(cartCode)).willReturn(Optional.empty());
//
//		assertThatThrownBy(() -> cartService.getItemsByCart(cartCode))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
//			});
//
//		verifyNoInteractions(productFeignClient);
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 상품 삭제")
//	void deleteItemsByCart_success() {
//		String userCode = UUID.randomUUID().toString();
//		List<String> cartProductCodes = List.of("p1", "p2", "p3");
//
//		Cart cart = Cart.builder()
//			.userCode(userCode).build();
//
//		given(cartRepository.findByUserCode(cart.getCode()))
//			.willReturn(Optional.of(cart));
//		given(cartItemRepository.deleteCartItemByProductCodes(cart, cartProductCodes))
//			.willReturn(cartProductCodes.size());
//
//		int result = cartService.deleteItemsByCart(cart.getCode(), new DeleteItemsByCartRequest(cartProductCodes));
//
//		assertThat(result).isEqualTo(cartProductCodes.size());
//		then(cartItemRepository).should().deleteCartItemByProductCodes(cart, cartProductCodes);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 삭제에서 코드 유효한 값")
//	void deleteItemsByCart_throwException_whenInvalidCode() {
//		String cartCode = "invalid code";
//
//		assertThatThrownBy(() -> cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(List.of())))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
//			});
//
//		verifyNoInteractions(cartRepository, cartItemRepository);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 삭제 상품 목록 빈 값")
//	void deleteItemsByCart_throwException_whenCartProductCodeEmpty() {
//		String cartCode = UUID.randomUUID().toString();
//
//		assertThatThrownBy(() -> cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(List.of())))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ITEM_DELETE_NOT_SELECTED);
//			});
//
//		verifyNoInteractions(cartRepository, cartItemRepository);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 삭제에서 장바구니를 찾을 수 없음")
//	void deleteItemsByCart_thrownException_whenCartNotExists() {
//		String cartCode = UUID.randomUUID().toString();
//
//		given(cartRepository.findByUserCode(cartCode)).willReturn(Optional.empty());
//
//		assertThatThrownBy(
//			() -> cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(List.of("p1", "p2"))))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
//			});
//
//		verifyNoInteractions(cartItemRepository);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 상품 삭제 결과 수 != 요청 수")
//	void deleteItemsByCart_thrownException_failed() {
//		String userCode = UUID.randomUUID().toString();
//		DeleteItemsByCartRequest request = new DeleteItemsByCartRequest(List.of("p1", "p2"));
//
//		Cart cart = Cart.builder().userCode(userCode).build();
//
//		given(cartRepository.findByUserCode(cart.getCode()))
//			.willReturn(Optional.of(cart));
//		given(cartItemRepository.deleteCartItemByProductCodes(cart, request.cartProductCodes()))
//			.willReturn(1);
//
//		assertThatThrownBy(() -> cartService.deleteItemsByCart(cart.getCode(), request))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.DELETE_CART_ITEM_FAILED);
//			});
//	}
//
//	@Test
//	@DisplayName("성공_장바구니 삭제")
//	void deleteCart_success() {
//		String userCode = UUID.randomUUID().toString();
//
//		Cart cart = Cart.builder()
//			.userCode(userCode)
//			.build();
//
//		given(cartRepository.findByUserCode(userCode)).willReturn(Optional.of(cart));
//
//		Cart deletedCart = cartService.deleteCart(userCode);
//
//		assertThat(deletedCart).isNotNull();
//		assertThat(deletedCart.getUserCode()).isEqualTo(userCode);
//		assertThat(deletedCart.getDeleteStatus()).isEqualTo(DeleteStatus.Y);
//
//		verify(cartRepository).findByUserCode(userCode);
//		verify(cartItemRepository).deleteCartItemByCartCode(cart);
//		verify(cartItemRepository, never()).deleteAll(anyList());
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 삭제 유저 코드 유효한 값x")
//	void deleteCart_throwException_whenInvalidCode() {
//		String userCode = "Invalid code";
//
//		assertThatThrownBy(() -> cartService.createCart(userCode))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
//			});
//
//		verifyNoInteractions(cartRepository, cartItemRepository);
//	}
//
//	@Test
//	@DisplayName("실패_장바구니 삭제에서 장바구니가 존재하지 않음")
//	void deleteCart_thrownException_whenCartNotExists() {
//		String userCode = UUID.randomUUID().toString();
//
//		given(cartRepository.findByUserCode(userCode)).willReturn(Optional.empty());
//
//		assertThatThrownBy(() -> cartService.deleteCart(userCode))
//			.isInstanceOf(ServiceException.class)
//			.satisfies(ex -> {
//				ServiceException se = (ServiceException) ex;
//				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
//			});
//
//		verifyNoInteractions(cartItemRepository);
//	}
//}
