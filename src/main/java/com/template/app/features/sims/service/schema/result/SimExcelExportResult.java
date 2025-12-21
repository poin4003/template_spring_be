package com.template.app.features.sims.service.schema.result;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

@Data
public class SimExcelExportResult {

    @ExcelProperty("ID Sim")
    private String simId; 

    @ExcelProperty("Phone number")
    private String simPhoneNumber;
    
    @ExcelProperty("Status")
    private String simStatus;

    @ExcelProperty("Selling price (VND)")
    private Integer simSellingPrice;
    
    @ExcelProperty("Dealer price (VND)")
    private Integer simDealerPrice;
    
    @ExcelProperty("Import price (VND)")
    private Integer simImportPrice;

    @ExcelProperty("Created at")
    @DateTimeFormat("dd-MM-yyyy HH:mm:ss")
    private String createdAt;

    @ExcelProperty("Updated at")
    @DateTimeFormat("dd-MM-yyyy HH:mm:ss")
    private String updatedAt;
}
