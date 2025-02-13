package com.nursery.controller;

import com.nursery.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @GetMapping("/checkout")
    public RedirectView checkout(@RequestParam double amount) throws StripeException {
        String checkoutUrl = paymentService.createCheckoutSession(amount);
        return new RedirectView(checkoutUrl);
    }

    @GetMapping("/payment/success")
    public String success() {
        return "Payment successful! Your order has been placed.";
    }

    @GetMapping("/payment/cancel")
    public String cancel() {
        return "Payment was canceled. Please try again.";
    }
}
