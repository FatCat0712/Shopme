package com.shopme.admin.report;

import com.shopme.admin.order.OrderDetailRepository;
import com.shopme.common.entity.order.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderDetailReportService extends AbstractReportService{
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailReportService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
        List<OrderDetail> listOrderDetails = null;
        if(reportType.equals(ReportType.CATEGORY)) {
            listOrderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startDate, endDate);
        }

       List<ReportItem> listReportItems = new ArrayList<>();
        if(listOrderDetails != null) {
            for(OrderDetail od : listOrderDetails) {
                String identifier;
                identifier = od.getProduct().getCategory().getName();
                ReportItem reportItem = new ReportItem(identifier);
                float grossSales = od.getSubtotal() + od.getShippingCost();
                float netSales = od.getSubtotal() - od.getProductCost();

                int index = listReportItems.indexOf(reportItem);
                if(index >= 0) {
                    reportItem = listReportItems.get(index);
                    reportItem.addGrossSales(grossSales);
                    reportItem.addNetSales(netSales);
                    reportItem.increaseProductCount(od.getQuantity());
                }
                else {
                    listReportItems.add(new ReportItem(identifier, grossSales, netSales, od.getQuantity()));
                }
            }
        }

        return listReportItems;
    }

    private void printReportData(List<ReportItem> listReportItems) {
        for(ReportItem item : listReportItems) {
            System.out.printf("%-20s %10.2f %10.2f %d\n",
                    item.getIdentifier(),
                    item.getGrossSales(),
                    item.getNetSales(),
                    item.getProductsCount()
            );
        }
    }


    private void printRawData(List<OrderDetail> listOrderDetails) {
        for(OrderDetail detail: listOrderDetails) {
            System.out.printf("%d %-20s %10.2f %10.2f %10.2f \n", detail.getQuantity(), detail.getProduct().getCategory().getName(), detail.getSubtotal(), detail.getProductCost(), detail.getShippingCost());
        }
    }
}
