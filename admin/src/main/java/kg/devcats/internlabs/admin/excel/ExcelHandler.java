package kg.devcats.internlabs.admin.excel;

import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.admin.service.AccountService;
import org.apache.poi.ss.usermodel.*;
import kg.devcats.internlabs.admin.dto.response.PaymentDTO;
import kg.devcats.internlabs.admin.service.PaymentService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Component
public class ExcelHandler {
    private final PaymentService paymentService;
    private final AccountService accountService;
    public ExcelHandler(PaymentService paymentService, AccountService accountService) {
        this.paymentService = paymentService;
        this.accountService = accountService;
    }

    public void writeToExcel(OutputStream outputStream, List<PaymentDTO> payments) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        createHeaderRow(sheet);
        fillDataRow(sheet, payments);

        workbook.write(outputStream);
        workbook.close();
    }

    private void fillDataRow(Sheet sheet, List<PaymentDTO> payments) {
        int rowNum = 1;
        for (PaymentDTO paymentDTO : payments) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(paymentDTO.account().requisite());
            dataRow.createCell(1).setCellValue(paymentDTO.account().fullName());
            dataRow.createCell(2).setCellValue(String.valueOf(paymentDTO.amount()));
            dataRow.createCell(3).setCellValue(paymentDTO.createdAt());
            dataRow.createCell(4).setCellValue(paymentDTO.paidAt());
            dataRow.createCell(5).setCellValue(String.valueOf(paymentDTO.paymentStatus()));
            dataRow.createCell(6).setCellValue(String.valueOf(paymentDTO.services()));
        }
    }

    private Row createHeaderRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Реквизит");
        row.createCell(1).setCellValue("Имя");
        row.createCell(2).setCellValue("Сумма");
        row.createCell(3).setCellValue("Дата создания");
        row.createCell(4).setCellValue("Дата проведения");
        row.createCell(5).setCellValue("Статус");
        row.createCell(6).setCellValue("Сервис");
        return row;
    }

    public List<AccountDTO> readFromExcel(InputStream inputStream) throws IOException {
        List<AccountDTO> accountList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                AccountDTO accountDTO = AccountDTO.builder()
                        .requisite(getStringValue(currentRow.getCell(0)))
                        .balance(new BigDecimal("0.00"))
                        .fullName(getStringValue(currentRow.getCell(1)))
                        .isBlocked(false)
                        .deletedAt(null)
                        .build();
                accountList.add(accountDTO);
            }
        }
        return accountList;
    }

    public List<AccountDTO> createAccounts(List<AccountDTO> accountsList) {
        List <AccountDTO> existAccounts = new ArrayList<>();
        AccountDTO createdAccount;

        for (int i = 0; i < accountsList.size(); i++) {
            createdAccount = accountService.createAccount(accountsList.get(i));
            if (createdAccount == null) {
                existAccounts.add(accountsList.get(i));
            }
        }

        return existAccounts;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return "";
        }
    }

//    public void writeToExcel(List<PaymentDTO> payments) throws IOException {
//        UUID uuid = UUID.randomUUID();
//        String filePath = "D:\\intern labs\\payments" + uuid + ".xlsx"; ////////
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Sheet1");
//        Row headerRow = createHeaderRow(sheet);
//        fillDataRow(sheet, payments);
//
//        FileOutputStream fileOut = new FileOutputStream(filePath);
//        workbook.write(fileOut);
//        fileOut.close();
//        workbook.close();
//    }
}

