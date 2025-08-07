// Sales report by Product
$(document).ready(function () {
    setupButtonEventHandler("_product", loadSalesReportByDateForProduct);
});

function loadSalesReportByDateForProduct(period) {
    let requestURL = contextPath + "reports/product/";
    if(period === "custom") {
        let startDate = $("#startDate_product").val();
        let endDate = $("#endDate_product").val();
        requestURL += startDate + "/" + endDate;
    }else {
        requestURL += period;
    }
    $.get(requestURL, function(responseJSON) {
        prepareChartDataForSalesReportByProduct(responseJSON);
        customizeChartForSalesReportByProduct();
        formatChartData(data, 2 ,3);
        drawChartForSalesReportByProduct(period);
        setSalesAmount(period, '_product', "Total products")
    })
}
function prepareChartDataForSalesReportByProduct(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Product');
    data.addColumn('number', 'Quantity');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function(index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.productCounts, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    })
}

function customizeChartForSalesReportByProduct() {
    chartOptions = {
        height: 360,
        width: '80%',
        page: 'enable',
        sortColumn: 2,
        sortAscending: false,

    }
}

function drawChartForSalesReportByProduct() {
        let salesChart = new google.visualization.Table(document.getElementById('chart_sales_by_product'));
        salesChart.draw(data, chartOptions);
}




