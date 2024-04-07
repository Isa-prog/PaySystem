package kg.devcats.internlabs.admin.controller;

import kg.devcats.internlabs.admin.dto.response.PaymentDTO;
import kg.devcats.internlabs.admin.dto.response.RollbackResponseAdmin;
import kg.devcats.internlabs.admin.excel.ExcelHandler;
import kg.devcats.internlabs.admin.service.PaymentService;
import kg.devcats.internlabs.core.dto.response.PaymentResult;
import kg.devcats.internlabs.core.dto.response.PutRollbackResult;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kg.devcats.internlabs.admin.dto.Status.*;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/payments")
@Validated
public class PaymentController {
    private final PaymentService paymentService;
    private final MessageSource messageSource;
    private final ExcelHandler excelHandler;


    public PaymentController(PaymentService paymentService, MessageSource messageSource, ExcelHandler excelHandler) {
        this.paymentService = paymentService;
        this.messageSource = messageSource;
        this.excelHandler = excelHandler;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('payment.read')")
    public ResponseEntity<Page<PaymentDTO>> getPayments(
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size,
                                                    @RequestParam(defaultValue = "createdAt") String sortField,
                                                    @RequestParam(defaultValue = "desc") String sortOrder,
                                                    @RequestParam(required = false) String requisite,
                                                    @RequestParam(required = false) BigDecimal minAmount,
                                                    @RequestParam(required = false) BigDecimal maxAmount,
                                                    @RequestParam(required = false) String serviceName,
                                                    @RequestParam(required = false) String clientFullName,
                                                    @RequestParam(required = false, defaultValue = "2000-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
                                                    @RequestParam(required = false, defaultValue = "2000-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDatePaid,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDatePaid,
                                                    @RequestParam(required = false) String paymentStatus) {

        Page<PaymentDTO> paymentsPage;

        if ( (requisite != null && !requisite.isEmpty()) || minAmount != null || maxAmount != null || (serviceName != null && !serviceName.isEmpty()) ||
                (clientFullName != null && !clientFullName.isEmpty()) || startDate != null || (paymentStatus != null && !paymentStatus.isEmpty()))
        {
            paymentsPage = paymentService.findByFilters(requisite, minAmount, maxAmount, serviceName,
                    clientFullName, startDate, endDate, startDatePaid, endDatePaid, paymentStatus,
                    PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortField));
        } else {
            paymentsPage = paymentService.getPayments(page, size, sortField, sortOrder);
        }
        if (paymentsPage == null || paymentsPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(paymentsPage, HttpStatus.OK);
    }
    @GetMapping("/download")
    @PreAuthorize("hasAuthority('payment.read')")
    public ResponseEntity<String> downloadPayments(
            HttpServletResponse response,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "amount") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String requisite,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String clientFullName,
            @RequestParam(required = false, defaultValue = "2000-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "2000-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDatePaid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDatePaid,
            @RequestParam(required = false) String paymentStatus) {

        List<PaymentDTO> payments;

        if ( (requisite != null && !requisite.isEmpty()) || minAmount != null || maxAmount != null || (serviceName != null && !serviceName.isEmpty()) ||
                (clientFullName != null && !clientFullName.isEmpty()) || startDate != null || (paymentStatus != null && !paymentStatus.isEmpty()))
        {
            payments = paymentService.findByFilters(requisite, minAmount, maxAmount, serviceName,
                    clientFullName, startDate, endDate, startDatePaid, endDatePaid, paymentStatus,
                    PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortField)).getContent();
            try {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=payments.xlsx");

                excelHandler.writeToExcel(response.getOutputStream(), payments);
                response.flushBuffer();
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            payments = paymentService.getPayments(page, size, sortField, sortOrder).getContent();
            try {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=payments.xlsx");

                excelHandler.writeToExcel(response.getOutputStream(), payments);
                response.flushBuffer();
            } catch (IOException e) {
                return ResponseEntity.ok("Cannot be exported");
            }
        }
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Excel file");
        }
        return ResponseEntity.ok("Excel file created successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('payment.read')")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable("id") Long id) {
        Optional<PaymentDTO> paymentData = paymentService.getPaymentById(id);
        if (paymentData.isPresent()) {
            return new ResponseEntity<>(paymentData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/rollback")
    @PreAuthorize("hasAuthority('payment.cancel')")
    public ResponseEntity<RollbackResponseAdmin> rollbackPaymentById(@PathVariable("id") Long id, boolean isLimited) {
        PutRollbackResult response = paymentService.putRollbackById(id, isLimited);
        if (response.getStatus().equals(PutRollbackResult.Status.SUCCESS)) {
            return new ResponseEntity<>(new RollbackResponseAdmin(ROLLBACK_COMPLETED,
                    messageSource.getMessage(ROLLBACK_COMPLETED.getMessage(), null, null),
                    response.getPaymentStatus()), HttpStatus.OK);
        }
        return switch(response.getDetailError()){
            case ROLLBACK_AMOUNT_LIMIT -> new ResponseEntity<>(new RollbackResponseAdmin(ROLLBACK_AMOUNT_LIMIT,
                    response.getDetailMessageError()), HttpStatus.OK);
            case PAYMENT_STATUS_NOT_SUCCESS -> new ResponseEntity<>(new RollbackResponseAdmin(PAYMENT_STATUS_NOT_SUCCESS,
                    response.getDetailMessageError()), HttpStatus.OK);
            case PAYMENT_NOT_FOUND -> new ResponseEntity<>(new RollbackResponseAdmin(PAYMENT_NOT_FOUND,
                    response.getDetailMessageError()),HttpStatus.OK);
            default -> new ResponseEntity<>(new RollbackResponseAdmin(OTHER,
                    response.getDetailMessageError()), HttpStatus.OK);
        };
    }
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('payment.create')")
    public ResponseEntity<PaymentResult> completePaymentById(@PathVariable("id") Long id) {
        PaymentResult result = paymentService.putCompleteById(id);

        if(result.getStatus().equals(PaymentResult.Status.SUCCESS)){
            return new ResponseEntity<>(new PaymentResult(PaymentResult.Status.SUCCESS, result.getPaymentId(),result.getPaymentStatus()), HttpStatus.OK);
        }

        return switch (result.getDetailError()){
            case PAYMENT_STATUS_NOT_IN_PROGRESS -> new ResponseEntity<>(new PaymentResult(PaymentResult.DetailError.PAYMENT_STATUS_NOT_IN_PROGRESS,
                    result.getDetailMessageError()), HttpStatus.OK);

            case PAYMENT_NOT_FOUND -> new ResponseEntity<>(new PaymentResult(PaymentResult.DetailError.PAYMENT_NOT_FOUND,
                    result.getDetailMessageError()), HttpStatus.OK);

            case NOT_ENOUGH_MONEY -> new ResponseEntity<>(new PaymentResult(PaymentResult.DetailError.NOT_ENOUGH_MONEY,
                    result.getDetailMessageError()), HttpStatus.OK);

            default -> new ResponseEntity<>(new PaymentResult(PaymentResult.DetailError.OTHER,
                    result.getDetailMessageError()), HttpStatus.OK);
        };
    }

}
