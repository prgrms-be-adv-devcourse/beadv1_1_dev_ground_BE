package io.devground.dbay.common.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.devground.dbay.domain.payment.model.dto.request.ChargePaymentRequest;
import io.devground.dbay.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view")
public class ViewController {

	private final PaymentService paymentService;

	@GetMapping("/payments/checkout")
	public String viewCheckoutPage(
		@RequestHeader("X-CODE") String userCode,
		Model model
	) {
		ChargePaymentRequest description = new ChargePaymentRequest(userCode, 10000L);
		model.addAttribute("description", description);
		return "payment/checkout";
	}
}
