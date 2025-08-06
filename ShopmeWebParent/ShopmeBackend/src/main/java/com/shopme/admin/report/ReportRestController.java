package com.shopme.admin.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ReportRestController {
    private final MasterOrderReportService masterOrderReportService;
    private final OrderDetailReportService orderDetailReportService;

    @Autowired
    public ReportRestController(MasterOrderReportService masterOrderReportService, OrderDetailReportService orderDetailReportService) {
        this.masterOrderReportService = masterOrderReportService;
        this.orderDetailReportService = orderDetailReportService;
    }

    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable(name = "period") String  period) {
        return switch (period){
            case "last_28_days" -> masterOrderReportService.getReportDataLast28Days(ReportType.DAY);
            case "last_6_months" -> masterOrderReportService.getReportDataLast6Months(ReportType.MONTH);
            case "last_year" -> masterOrderReportService.getReportDataLastYear(ReportType.MONTH);
            default -> masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
         };
    }

    @GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);
        return masterOrderReportService.getReportDataByDateRange(startTime, endTime, ReportType.DAY);
    }

    @GetMapping("/reports/{groupBy}/{period}")
    public List<ReportItem> getReportDataByCategoryOrProduct(
            @PathVariable(name = "groupBy") String groupBy,
            @PathVariable(name = "period") String period)
    {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
        return switch (period){
            case "last_28_days" -> orderDetailReportService.getReportDataLast28Days(reportType);
            case "last_6_months" -> orderDetailReportService.getReportDataLast6Months(reportType);
            case "last_year" -> orderDetailReportService.getReportDataLastYear(reportType);
            default -> orderDetailReportService.getReportDataLast7Days(reportType);
        };
    }

    @GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByCategoryOrProductDateRange(
            @PathVariable(name = "groupBy") String groupBy,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate
    ) throws ParseException {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);
        return masterOrderReportService.getReportDataByDateRange(startTime, endTime, reportType);
    }




}
