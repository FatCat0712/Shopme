package com.shopme.admin.report;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService {
    private final OrderRepository repo;
    private DateFormat dateFormatter;

    @Autowired
    public MasterOrderReportService(OrderRepository repo) {
        this.repo = repo;
    }

    public List<ReportItem> getReportDataLast7Days() {
        return getReportDataLastXDays(7);
    }

    public List<ReportItem> getReportDataLast28Days() {
        return getReportDataLastXDays(28);
    }

    private List<ReportItem> getReportDataLastXDays(int days) {
        Date endtime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = calendar.getTime();
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endtime);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        return getReportDataByDateRange(startTime, endtime, "days");
    }

    public List<ReportItem> getReportDataLast6Months() {
        return getReportDataLastXMonths(6);
    }

    public List<ReportItem> getReportDataLastYear() {
        return getReportDataLastXMonths(12);
    }

    public List<ReportItem> getReportDataLastXMonths(int months) {
        Date endtime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -(months - 1));
        Date startTime = calendar.getTime();
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endtime);
        dateFormatter = new SimpleDateFormat("yyyy-MM");

        return getReportDataByDateRange(startTime, endtime, "months");
    }

    public List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRange(startTime, endTime, "days");
    }

    private List<ReportItem> getReportDataByDateRange(Date startTime,  Date endTime, String period) {
        List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
        printRawData(listOrders);
        List<ReportItem> listReportItems = createReportData(startTime, endTime, period);

        System.out.println();

        calculateSalesReportData(listOrders, listReportItems);

        printReportData(listReportItems);

        return listReportItems;
    }

    private List<ReportItem> createReportData(Date startTime, Date endTime, String period) {
        List<ReportItem> listReportItems = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String dateString = dateFormatter.format(currentDate);
        listReportItems.add(new ReportItem(dateString));

        do{
            if(period.equals("days")) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            }
            else if(period.equals("months")) {
                startDate.add(Calendar.MONTH, 1);
            }

            currentDate = startDate.getTime();
            dateString = dateFormatter.format(currentDate);
            listReportItems.add(new ReportItem(dateString));
        }while(startDate.before(endDate));

        return listReportItems;
    }



    private void calculateSalesReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
        for(Order order : listOrders) {
            String orderDateString = dateFormatter.format(order.getOrderTime());
            ReportItem reportItem = new ReportItem(orderDateString);
            int itemIndex = listReportItems.indexOf(reportItem);
            if(itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);
                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubTotal() - order.getProductCost());
                reportItem.increaseOrderCount();
            }
        }
    }

    private void printRawData(List<Order> listOrders) {
        listOrders.forEach(order -> {
            System.out.printf("%-3d | %s | %10.2f | %10.2f \n", order.getId(), order.getOrderTime(), order.getTotal() ,order.getProductCost());
        });
    }

    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(item -> {
            System.out.printf("%s %10.2f %10.2f %d\n", item.getIdentifier(), item.getGrossSales(), item.getNetSales(), item.getOrdersCount());
        });
    }







}
