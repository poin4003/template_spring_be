package com.template.app.features.sims.service.schema.command;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class SimExcelImportCmd {

    @ExcelProperty(value = "phone_number")
    private String simPhoneNumber;

    @ExcelProperty(value = "selling_price")
    private Integer simSellingPrice;

    @ExcelProperty(value = "dealer_price")
    private Integer simDealerPrice;

    @ExcelProperty(value = "import_price")
    private Integer simImportPrice;
    
    @ExcelProperty(value = "sim_status_string")
    private String simStatusString;
}
