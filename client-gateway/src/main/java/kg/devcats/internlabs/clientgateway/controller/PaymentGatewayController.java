package kg.devcats.internlabs.clientgateway.controller;

import kg.devcats.internlabs.clientgateway.dto.request.CreatePaymentRequest;
import kg.devcats.internlabs.clientgateway.dto.response.*;
import kg.devcats.internlabs.clientgateway.service.PaymentGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/client")
@Validated
public class PaymentGatewayController {

    private final PaymentGatewayService paymentGatewayService;

    public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @GetMapping("/requisite")
    public ResponseEntity<RequisiteResponse> getRequisite(Authentication authentication,
                                                          HttpServletRequest httpServletRequest,
                                                          @RequestParam @NotNull String requisite,
                                                          @RequestParam @NotNull Long serviceId) {
        String clientLogin = authentication.getName();
        String ipAddress = httpServletRequest.getRemoteAddr();
        System.out.println("ipAddress - " + ipAddress);

        RequisiteResponse response = paymentGatewayService.getRequisite(requisite, clientLogin, serviceId, ipAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/payments/byDate")
    public ResponseEntity<PaymentFromToResponse> getPaymentsFromToDate(Authentication authentication,
                                                              HttpServletRequest httpServletRequest,
                                                              @RequestParam @NotNull String fromDate,
                                                              @RequestParam @NotNull String toDate) {
        String clientLogin = authentication.getName();
        String ipAddress = httpServletRequest.getRemoteAddr();

        System.out.println("ipAddress - " + ipAddress);

        PaymentFromToResponse response = paymentGatewayService.getPaymentsFromTo(fromDate, toDate, clientLogin, ipAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/payment/{id}/status")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(Authentication authentication,
                                                                  HttpServletRequest httpServletRequest,
                                                                  @PathVariable @NotNull Long id) {
        String clientLogin = authentication.getName();
        String ipAddress = httpServletRequest.getRemoteAddr();
        System.out.println("ipAddress - " + ipAddress);

        PaymentStatusResponse response = paymentGatewayService.getPaymentStatus(id, clientLogin, ipAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payment/make")
    public ResponseEntity<PaymentResponse> makePayment(Authentication authentication,
                                                       HttpServletRequest httpServletRequest,
                                                       @RequestBody CreatePaymentRequest request) {
        String clientLogin = authentication.getName();
        String ipAddress = httpServletRequest.getRemoteAddr();
        System.out.println("ipAddress - " + ipAddress);

        PaymentResponse paymentResponse = paymentGatewayService.makePayment(clientLogin,
                request.requisite(), request.amount(), request.serviceId(), request.externalId(), ipAddress);

        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @PutMapping("/payment/{id}/rollback")
    public ResponseEntity<RollbackResponse> rollbackPayment(Authentication authentication,
                                                            HttpServletRequest httpServletRequest,
                                                            @PathVariable @NotNull Long id, boolean isLimited) {
        String clientLogin = authentication.getName();
        String ipAddress = httpServletRequest.getRemoteAddr();
        System.out.println("ipAddress - " + ipAddress);

        RollbackResponse response = paymentGatewayService.rollbackPayment(id, true,
                clientLogin, ipAddress);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
