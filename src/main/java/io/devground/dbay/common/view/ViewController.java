package io.devground.dbay.common.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.ChargePaymentRequest;
import io.devground.dbay.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view")
public class ViewController {

	//private final PaymentService paymentService;

	@GetMapping("/payments/checkout/{userCode}")
	public String viewCheckoutPage(
		@PathVariable String userCode,
		Model model
	) {
		//ChargePaymentRequest description = new ChargePaymentRequest(userCode, 10000L);
		model.addAttribute("userCode", userCode);
		model.addAttribute("amount", 10000L);
		return "payment/checkout";
	}


	@GetMapping("/payments/success")
	public String viewSuccessPage() {
		return "payment/success";
	}

	@GetMapping("/payments/fail")
	public String viewFailurePage() {
		return "payments/fail";
	}
}
