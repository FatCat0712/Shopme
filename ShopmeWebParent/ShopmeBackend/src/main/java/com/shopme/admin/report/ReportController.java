package com.shopme.admin.report;

import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ReportController {
    private final SettingService settingService;

    @Autowired
    public ReportController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request) {
        loadCurrencySettings(request);
        return "reports/reports";
    }

    private void loadCurrencySettings(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for(Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }




}
