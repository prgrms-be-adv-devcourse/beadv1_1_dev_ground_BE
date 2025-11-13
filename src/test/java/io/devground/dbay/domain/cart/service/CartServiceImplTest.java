package io.devground.dbay.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductListResponse;
import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.domain.cart.cart.model.vo.GetItemsByCartResponse;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import io.devground.dbay.domain.cart.cart.service.CartServiceImpl;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import io.devground.dbay.domain.cart.cartItem.repository.CartItemRepository;
import io.devground.dbay.domain.cart.infra.client.ProductFeignClient;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private ProductFeignClient productFeignClient;

	@InjectMocks
	private CartServiceImpl cartService;

	@Test
	@DisplayName("성공_장바구니 생성")
	void createCart_success() {

		String userCode = UUID.randomUUID().toString();
		given(cartRepository.findByUserCode(userCode)).willReturn(Optional.empty());

		Cart cart = Cart.builder()
			.userCode(userCode)
			.build();
		given(cartRepository.save(any(Cart.class))).willReturn(cart);

		Cart result = cartService.createCart(userCode);

		assertThat(result.getUserCode()).isEqualTo(userCode);
		verify(cartRepository).findByCode(userCode);
		verify(cartRepository).save(any(Cart.class));
	}

	@Test
	@DisplayName("실패_장바구니 생성에서 코드 유효한 값")
	void createCart_throwException_whenInvalidCode() {
		String userCode = "invalid-code";

		assertThatThrownBy(() -> cartService.createCart(userCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});
	}

	@Test
	@DisplayName("실패_장바구니 이미 존재")
	void createCart_throwException_whenCartAlreadyExists() {
		String userCode = UUID.randomUUID().toString();
		Cart cart = new Cart(userCode);

		given(cartRepository.findByUserCode(userCode)).willReturn(Optional.of(cart));

		assertThatThrownBy(() -> cartService.createCart(userCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ALREADY_EXIST);
			});
	}

	@Test
	@DisplayName("성공_장바구니 상품 추가")
	void addItem_success() {
		String userCode = UUID.randomUUID().toString();
		String cartCode = UUID.randomUUID().toString();
		String productCode = UUID.randomUUID().toString();

		given(cartItemRepository.existsByCart_CodeAndProductCode(cartCode, productCode))
			.willReturn(false);

		Cart cart = Cart.builder()
			.userCode(userCode)
			.build();

		ReflectionTestUtils.setField(cart, "code", cartCode);

		given(cartRepository.findByCode(cartCode)).willReturn(Optional.of(cart));

		given(cartItemRepository.save(any(CartItem.class)))
			.willAnswer(i -> i.getArgument(0));

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		CartItem result = cartService.addItem(cartCode, request);

		assertThat(result.getCart().getCode()).isEqualTo(cartCode);
		assertThat(result.getProductCode()).isEqualTo(productCode);

		verify(cartRepository).findByCode(cartCode);
		verify(cartItemRepository).existsByCart_CodeAndProductCode(cartCode, productCode);
		verify(cartRepository).findByCode(cartCode);
		verify(cartItemRepository).save(any(CartItem.class));
	}

	@Test
	@DisplayName("실패_장바구니,상품코드 유효한 값")
	void addItem_throwException_whenInvalidCode() {
		String cartCode = "invalid-uuid";
		String productCode = "invalid-uuid";

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});
	}

	@Test
	@DisplayName("실패_장바구니 없음")
	void addItem_throwException_whenCartNotExists() {
		String cartCode = UUID.randomUUID().toString();
		String productCode = UUID.randomUUID().toString();

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		given(cartRepository.findByCode(cartCode)).willReturn(Optional.empty());

		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("실패_장바구니에 상품 이미 존재")
	void addItem_thrownException_whenCartItemAlreadyExists() {
		String userCode = UUID.randomUUID().toString();
		String cartCode = UUID.randomUUID().toString();
		String productCode = UUID.randomUUID().toString();

		Cart cart = Cart.builder().userCode(userCode).build();
		ReflectionTestUtils.setField(cart, "code", cartCode);

		given(cartItemRepository.existsByCart_CodeAndProductCode(cartCode, productCode)).willReturn(true);

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ITEM_ALREADY_EXIST);
			});
	}

	@Test
	@DisplayName("성공_장바구니 조회")
	void getItemsByCart_success() {
		String cartCode = UUID.randomUUID().toString();

		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();

		String sellerCode = UUID.randomUUID().toString();

		String productSaleCode1 = UUID.randomUUID().toString();
		String productSaleCode2 = UUID.randomUUID().toString();

		CartItem item1 = mock(CartItem.class);
		given(item1.getProductCode()).willReturn(productCode1);

		CartItem item2 = mock(CartItem.class);
		given(item2.getProductCode()).willReturn(productCode2);

		Cart cart = mock(Cart.class);
		given(cart.getCartItems()).willReturn(List.of(item1, item2));
		given(cartRepository.findByCode(cartCode)).willReturn(Optional.of(cart));

		CartProductListResponse p1 = new CartProductListResponse(productCode1, productSaleCode1, sellerCode, "아이폰 프로 17", 1500000L);
		CartProductListResponse p2 = new CartProductListResponse(productCode2, productSaleCode2, sellerCode, "맥북3 프로", 3500000L);
		given(productFeignClient.productListByCodes(List.of(productCode1, productCode2))).willReturn(List.of(p1, p2));

		GetItemsByCartResponse result = cartService.getItemsByCart(cartCode);

		assertThat(result.totalAmount()).isEqualTo(5000000L);
		assertThat(result.productLists()).containsExactlyInAnyOrder(p1, p2);
		assertThat(result.productLists().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("성공_장바구니 상품 중복 제거")
	void getItemsByCart_duplication_success() {
		String cartCode = UUID.randomUUID().toString();

		String productCode = UUID.randomUUID().toString();
		String productSaleCode = UUID.randomUUID().toString();

		String sellerCode = UUID.randomUUID().toString();

		CartItem item1 = mock(CartItem.class);
		given(item1.getProductCode()).willReturn(productCode);

		CartItem item2 = mock(CartItem.class);
		given(item2.getProductCode()).willReturn(productCode);

		Cart cart = mock(Cart.class);
		given(cart.getCartItems()).willReturn(List.of(item1, item2));
		given(cartRepository.findByCode(cartCode)).willReturn(Optional.of(cart));

		CartProductListResponse p1 = new CartProductListResponse(productCode, productSaleCode, sellerCode, "아이폰 프로 17", 1500000L);
		given(productFeignClient.productListByCodes(List.of(productCode))).willReturn(List.of(p1));

		GetItemsByCartResponse result = cartService.getItemsByCart(cartCode);

		assertThat(result.totalAmount()).isEqualTo(1500000L);
		assertThat(result.productLists()).containsExactlyInAnyOrder(p1);
		assertThat(result.productLists().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("성공_장바구니 상품 한개 조회")
	void getItemsByCart_only_one_success() {
		String cartCode = UUID.randomUUID().toString();

		String productCode = UUID.randomUUID().toString();

		String productSaleCode = UUID.randomUUID().toString();

		String sellerCode = UUID.randomUUID().toString();

		CartItem item1 = mock(CartItem.class);
		given(item1.getProductCode()).willReturn(productCode);

		Cart cart = mock(Cart.class);
		given(cart.getCartItems()).willReturn(List.of(item1));
		given(cartRepository.findByCode(cartCode)).willReturn(Optional.of(cart));

		CartProductListResponse p1 = new CartProductListResponse(productCode, productSaleCode, sellerCode, "아이폰 프로 17", 1500000L);
		given(productFeignClient.productListByCodes(List.of(productCode))).willReturn(List.of(p1));

		GetItemsByCartResponse result = cartService.getItemsByCart(cartCode);

		assertThat(result.totalAmount()).isEqualTo(1500000L);
		assertThat(result.productLists()).containsExactlyInAnyOrder(p1);
		assertThat(result.productLists().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("성공_빈 장바구니 조회")
	void getItemsByCart_empty_success() {
		String cartCode = UUID.randomUUID().toString();

		Cart cart = mock(Cart.class);

		given(cartRepository.findByCode(cartCode)).willReturn(Optional.of(cart));

		GetItemsByCartResponse result = cartService.getItemsByCart(cartCode);

		assertThat(result.totalAmount()).isEqualTo(0L);
		assertThat(result.productLists().size()).isEqualTo(0);

		verifyNoInteractions(productFeignClient);
	}

	@Test
	@DisplayName("실패_장바구니 조회에서 코드 유효한 값")
	void getItemsByCart_throwException_whenInvalidCode() {
		String cartCode = "invalid-code";

		assertThatThrownBy(() -> cartService.createCart(cartCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(cartRepository, productFeignClient);
	}

	@Test
	@DisplayName("실패_장바구니 조회에서 장바구니 없음")
	void getItemsByCart_throwException_whenCartNotExists() {
		String cartCode = UUID.randomUUID().toString();

		given(cartRepository.findByCode(cartCode)).willReturn(Optional.empty());

		assertThatThrownBy(() -> cartService.getItemsByCart(cartCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode().getMessage()).isEqualTo("장바구니를 찾을 수 없습니다.");
			});

		verifyNoInteractions(productFeignClient);
	}

	@Test
	@DisplayName("성공_장바구니 상품 삭제")
	void deleteItemsByCart_success() {
		String cartCode = UUID.randomUUID().toString();
		List<String> cartProductCodes = List.of("p1", "p2", "p3");

		given(cartRepository.findByCode(cartCode))
			.willReturn(Optional.of(mock(Cart.class)));
		given(cartItemRepository.deleteCartItemByProductCodes(cartCode, cartProductCodes))
			.willReturn(cartProductCodes.size());

		int result = cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(cartProductCodes));

		assertThat(result).isEqualTo(cartProductCodes.size());
		then(cartItemRepository).should().deleteCartItemByProductCodes(cartCode, cartProductCodes);
	}

	@Test
	@DisplayName("실패_장바구니 삭제에서 코드 유효한 값")
	void deleteItemsByCart_throwException_whenInvalidCode() {
		String cartCode = "invalid code";

		assertThatThrownBy(() -> cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(List.of())))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode().getMessage()).isEqualTo("잘못된 코드 형식입니다.");
			});

		verifyNoInteractions(cartRepository, cartItemRepository);
	}

	@Test
	@DisplayName("실패_장바구니 삭제 상품 목록 빈 값")
	void deleteItemsByCart_throwException_whenCartProductCodeEmpty() {
		String cartCode = UUID.randomUUID().toString();

		assertThatThrownBy(() -> cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(List.of())))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode().getMessage()).isEqualTo("삭제할 상품이 선택되지 않았습니다.");
			});

		verifyNoInteractions(cartRepository, cartItemRepository);
	}

	@Test
	@DisplayName("실패_장바구니 삭제에서 장바구니를 찾을 수 없음")
	void deleteItemsByCart_thrownException_whenCartNotExists() {
		String cartCode = UUID.randomUUID().toString();

		given(cartRepository.findByCode(cartCode)).willReturn(Optional.empty());

		assertThatThrownBy(
			() -> cartService.deleteItemsByCart(cartCode, new DeleteItemsByCartRequest(List.of("p1", "p2"))))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode().getMessage()).isEqualTo("장바구니를 찾을 수 없습니다.");
			});

		verifyNoInteractions(cartItemRepository);
	}

	@Test
	@DisplayName("실패_장바구니 삭제 결과 수 != 요청 수")
	void deleteItemsByCart_thrownException_failed() {
		String cartCode = UUID.randomUUID().toString();
		DeleteItemsByCartRequest request = new DeleteItemsByCartRequest(List.of("p1", "p2"));

		given(cartRepository.findByCode(cartCode))
			.willReturn(Optional.of(mock(Cart.class)));
		given(cartItemRepository.deleteCartItemByProductCodes(cartCode, request.cartProductCodes()))
			.willReturn(1);

		assertThatThrownBy(() -> cartService.deleteItemsByCart(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode().getMessage()).isEqualTo("장바구니 상품 삭제를 실패했습니다.");
			});
	}
}
