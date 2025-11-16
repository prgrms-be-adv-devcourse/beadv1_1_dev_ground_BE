package io.devground.dbay.domain.cart;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductListResponse;
import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.domain.cart.cart.model.vo.GetItemsByCartResponse;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import io.devground.dbay.domain.cart.cart.service.CartService;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import io.devground.dbay.domain.cart.cartItem.repository.CartItemRepository;
import io.devground.dbay.domain.cart.infra.client.ProductFeignClient;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"S3_BUCKET_NAME=test-bucket",
	"S3_ACCESS_KEY=test-access-key",
	"S3_SECRET_KEY=test-secret-key",
	"S3_REGION=ap-northeast-2"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CartIntegrationTest {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private EntityManager entityManager;

	@MockBean
	private ProductFeignClient productFeignClient;

	@Test
	@DisplayName("장바구니 생성 성공")
	void createCart_success() {
		String userCode = UUID.randomUUID().toString();

		Cart cart = cartService.createCart(userCode);

		assertThat(cart.getUserCode()).isEqualTo(userCode);

		Cart saved = cartRepository.findByUserCode(userCode).orElse(null);

		assertThat(saved).isNotNull();
	}

	@Test
	@DisplayName("장바구니 상품 추가 성공")
	void addItem_success() {
		String userCode = UUID.randomUUID().toString();

		String productCode = UUID.randomUUID().toString();

		Cart cart = cartService.createCart(userCode);

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		CartItem cartItem = cartService.addItem(cart.getCode(), request);

		assertThat(cartItem.getCart().getId()).isEqualTo(cart.getId());
		assertThat(cartItem.getProductCode()).isEqualTo(productCode);
	}

	@Test
	@DisplayName("장바구니 상품 가져오기 성공")
	void getItemsByCart_success() {
		String userCode = UUID.randomUUID().toString();

		String productSaleCode1 = UUID.randomUUID().toString();
		String productSaleCode2 = UUID.randomUUID().toString();

		String sellerCode1 = UUID.randomUUID().toString();
		String sellerCode2 = UUID.randomUUID().toString();

		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();

		Cart cart = cartService.createCart(userCode);

		AddCartItemRequest request1 = new AddCartItemRequest(productCode1);
		AddCartItemRequest request2 = new AddCartItemRequest(productCode2);

		cartService.addItem(cart.getCode(), request1);
		cartService.addItem(cart.getCode(), request2);

		Cart savedCart = cartRepository.findByCode(cart.getCode()).orElseThrow();

		List<CartItem> cartItems = cartItemRepository.findByCart(savedCart);

		assertThat(cartItems.size()).isEqualTo(2);

		List<String> productCodes = List.of(productCode1, productCode2);

		CartProductListResponse p1 = new CartProductListResponse(
			productCode1, productSaleCode1, sellerCode1, "상품1", 1000L
		);
		CartProductListResponse p2 = new CartProductListResponse(
			productCode2, productSaleCode2, sellerCode2, "상품2", 2000L
		);

		List<CartProductListResponse> cartProductListResponses = List.of(p1, p2);

		given(productFeignClient.productListByCodes(productCodes))
			.willReturn(cartProductListResponses);

		GetItemsByCartResponse result = cartService.getItemsByCart(savedCart.getCode());

		verify(productFeignClient, times(1)).productListByCodes(anyList());

		assertThat(result.totalAmount()).isEqualTo(3000L);
		assertThat(result.productLists())
			.extracting(CartProductListResponse::productCode)
			.containsExactlyInAnyOrder(productCode1, productCode2);
	}

	@Test
	@DisplayName("장바구니 상품 삭제 성공")
	void deleteItemByCart_success() {
		String userCode = UUID.randomUUID().toString();

		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();

		Cart cart = cartService.createCart(userCode);

		AddCartItemRequest request1 = new AddCartItemRequest(productCode1);
		AddCartItemRequest request2 = new AddCartItemRequest(productCode2);

		cartService.addItem(cart.getCode(), request1);
		cartService.addItem(cart.getCode(), request2);

		DeleteItemsByCartRequest request3 = new DeleteItemsByCartRequest(List.of(productCode1, productCode2));

		int result = cartService.deleteItemsByCart(cart.getCode(), request3);

		entityManager.flush();
		entityManager.clear();

		List<CartItem> cartItems = cartItemRepository.findByCart(cart);
		log.info("cartItem: {}", cartItems);

		assertThat(result).isEqualTo(2);
		assertThat(cartItems)
			.hasSize(0);
	}

	@Test
	@DisplayName("장바구니 삭제 성공")
	void deleteCart_success() {
		String userCode = UUID.randomUUID().toString();
		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();

		Cart cart = cartService.createCart(userCode);

		AddCartItemRequest request = new AddCartItemRequest(productCode1);
		AddCartItemRequest request2 = new AddCartItemRequest(productCode2);

		cartService.addItem(cart.getCode(), request);
		cartService.addItem(cart.getCode(), request2);

		Cart deleted = cartService.deleteCart(userCode);

		entityManager.flush();
		entityManager.clear();

		assertThat(deleted.getUserCode()).isEqualTo(userCode);
		assertThat(deleted.getDeleteStatus()).isEqualTo(DeleteStatus.Y);

		List<CartItem> items = cartItemRepository.findByCart(deleted);

		assertThat(items).allMatch(i -> i.getDeleteStatus() == DeleteStatus.Y);
	}
}
