package io.devground.dbay.order.infra;

import io.devground.dbay.order.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

	private final OrderService orderService;

	@Scheduled(cron = "0 0 3 * * *")
	public void runAutoDeliveryUpdate() {
		int result = orderService.autoUpdateOrderStatus();
		log.info("자동 배송 상태 갱신: {}건 처리 완료", result);
	}
}
