package io.devground.dbay.order.infrastructure.adapter.in.scheduler;

import io.devground.dbay.order.application.service.OrderApplication;
import io.devground.dbay.order.domain.vo.Progress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

	private final OrderApplication orderApplication;

	@Scheduled(cron = "0 0 3 * * *")
	public void runAutoDeliveryUpdate() {
		Progress result = orderApplication.autoUpdateOrderStatus();
		log.info("배송 대상 주문: {}, 배송 변경된 주문: {}, 배송 완료 대상 주문: {}, 배송 완료 변경된 주문: {}",
				result.paidOrder(),
				result.paidToDelivery(),
				result.deliveryOrder(),
				result.deliveryToDelivered()
		);
	}
}
