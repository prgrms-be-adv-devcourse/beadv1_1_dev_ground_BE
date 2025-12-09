package io.devground.payment.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.devground.core.util.CodeUtil;
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
		String orderCode = CodeUtil.generateUUID();
		model.addAttribute("userCode", userCode);
		model.addAttribute("orderCode", orderCode);
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
