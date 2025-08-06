// Sales report by Category
$(document).ready(function () {
    setupButtonEventHandler("_category", loadSalesReportByDateForCategory);
});

function loadSalesReportByDateForCategory(period) {
    let requestURL = contextPath + "reports/category/";
    if(period === "custom") {
        let startDate = $("#startDate_category").val();
        let endDate = $("#endDate_category").val();
        requestURL += startDate + "/" + endDate;
    }else {
        requestURL += period;
    }
    $.get(requestURL, function(responseJSON) {
        prepareChartDataForSalesReportByCategory(responseJSON);
        customizeChartForSalesReportByCategory();
        formatChartData(data,1 ,2);
        drawChartForSalesReportByCategory(period);
        setSalesAmount(period, '_category', "Total products")
    })
}
function prepareChartDataForSalesReportByCategory(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Category');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function(index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    })
}

function customizeChartForSalesReportByCategory() {
    chartOptions = {
        height: 360,
        legend: 'right'
    }
}

function drawChartForSalesReportByCategory() {
        let salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
        salesChart.draw(data, chartOptions);
}




