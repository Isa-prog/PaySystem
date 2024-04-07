package kg.devcats.internlabs.admin.controller;

import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.admin.excel.ExcelHandler;
import kg.devcats.internlabs.admin.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/accounts")
@Validated
public class AccountController {
    private final AccountService accountService;
    private final ExcelHandler excelHandler;
    public AccountController(AccountService accountService, ExcelHandler excelHandler) {
        this.accountService = accountService;
        this.excelHandler = excelHandler;
    }
    @GetMapping
    @PreAuthorize("hasAuthority('account.read')")
    public ResponseEntity<Page<AccountDTO>> getAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "requisite") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String requisite,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) Boolean isBlocked
    ) {
        Page<AccountDTO> accountsPage = accountService.getAccounts(page, size, sortField, sortOrder, requisite, minBalance, maxBalance, fullName, isBlocked);
        if (accountsPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(accountsPage, HttpStatus.OK);
    }
    @GetMapping("/{requisite}")
    @PreAuthorize("hasAuthority('account.read')")
    public ResponseEntity<AccountDTO> getAccountByRequisite(@PathVariable("requisite") String requisite) {
        AccountDTO accountData = accountService.getAccountByRequisite(requisite);

        if (accountData != null) {
            return new ResponseEntity<>(accountData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping
    @PreAuthorize("hasAuthority('account.create')")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        AccountDTO createdAccount = accountService.createAccount(accountDTO);

        if (createdAccount != null) {
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/{requisite}")
    @PreAuthorize("hasAuthority('account.create')")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable("requisite") String requisite, @RequestBody AccountDTO accountDTO) {
        AccountDTO accountData = accountService.updateAccount(requisite, accountDTO);
        if (accountData != null) {
            return new ResponseEntity<>(accountData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{requisite}")
    @PreAuthorize("hasAuthority('account.delete')")
    public ResponseEntity<HttpStatus> deleteAccount(@PathVariable("requisite") String requisite) {
        accountService.delete(requisite);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('account.create')")
    public ResponseEntity<List<AccountDTO>> importAccounts(@RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            try {
                InputStream inputStream = file.getInputStream();
                List<AccountDTO> accountsList = excelHandler.readFromExcel(inputStream);
                List<AccountDTO> existAccount = excelHandler.createAccounts(accountsList);
                return new ResponseEntity<>(existAccount, HttpStatus.OK);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process Excel file", e);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
