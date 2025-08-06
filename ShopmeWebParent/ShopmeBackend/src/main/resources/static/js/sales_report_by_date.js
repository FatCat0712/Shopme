// Sales report by Date
let data;
let chartOptions;
let totalGrossSales = 0;
let totalNetSales = 0;
let totalOrders = 0;
let startDateField ;
let endDateField;
let MILLISECONDS_A_DAY =  24 * 60 * 60 * 1000;

$(document).ready(function () {
    startDateField = document.getElementById("startDate");
    endDateField = document.getElementById('endDate');
    $(".button-sales-by-date").on("click", function (e) {
        let divCustomDateRange = $("#divCustomDateRange");
        totalOrders = 0;
        $(".button-sales-by-date").each(function() {
            $(this).removeClass('btn-primary').addClass('btn-light');
        })

        $(this).removeClass('btn-light').addClass('btn-primary');

        let period = $(this).attr('period');

        if(period) {
            loadSalesReportByDate(period);
            divCustomDateRange.addClass('d-none');
        }
        else {
           divCustomDateRange.removeClass('d-none');
        }
    });

    initCustomDateRange();
    $("#buttonViewReportByDateRange").on('click', function(e) {
        validateDateRange();
    })
});

function validateDateRange() {
    let days = calculateDays();
    startDateField.setCustomValidity('');
    if(days >= 7 && days <= 30) {
        loadSalesReportByDate('custom')
    }
    else {
        startDateField.setCustomValidity('Dates must be in the range of 7..30 days');
        startDateField.reportValidity();
    }
}

function calculateDays() {
    let startDate = startDateField.valueAsDate;
    let endDate = endDateField.valueAsDate;
    return (endDate - startDate) / MILLISECONDS_A_DAY;
}

function initCustomDateRange() {
    let toDate = new Date();
    endDateField.valueAsDate= toDate;

    let fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    startDateField.valueAsDate = fromDate;

}

function loadSalesReportByDate(period) {
    let requestURL = contextPath + "reports/sales_by_date/";
    if(period === "custom") {
        let startDate = $("#startDate").val();
        let endDate = $("#endDate").val();
        requestURL += startDate + "/" + endDate;
    }else {
        requestURL += period;

    }
    $.get(requestURL, function(responseJSON) {
        prepareChartData(responseJSON);
        customizeChart(period);
        drawChart(period);
    })

}

function prepareChartData(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Date');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');
    data.addColumn('number', 'Orders');

    $.each(responseJSON, function(index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales, reportItem.ordersCount]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalOrders += parseInt(reportItem.ordersCount);
    })
}

function customizeChart(period) {
    chartOptions = {
        title: getCharTitle(period),
        height: 360,
        legend: 'top',
        series: {
            0: {targetAxisIndex: 0},
            1: {targetAxisIndex: 0},
            2: {targetAxisIndex: 1}
        },
        vAxes: {
            0: {title: 'Sales Amount', format: 'currency'},
            1: {title: 'Number of Orders'}
        }
    }

    let formatter = new google.visualization.NumberFormat({
        prefix: prefixCurrencySymbol,
        suffix: suffixCurrencySymbol,
        decimal: decimalPointType,
        groupingSymbol: thousandPointType,
        fractionDigits: decimalDigits
    });

    formatter.format(data, 1);
    formatter.format(data,2);

}

function drawChart(period) {
        let salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
        salesChart.draw(data, chartOptions);
        $("#textTotalGrossSales").text(formatCurrency(totalGrossSales));
        $("#textTotalNetSales").text(formatCurrency(totalNetSales));

        let denominator = getDenominator(period)

        $("#textAvgGrossSales").text(formatCurrency(totalGrossSales / denominator));
        $("#textAvgNetSales").text(formatCurrency(totalNetSales/ denominator));
        $("#textTotalOrders").text(totalOrders);
}

function formatCurrency(amount) {
    let formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandPointType);
    return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

function getCharTitle(period) {
    if(period === 'last_7_days') return "Sales in Last 7 Days";
    if(period === 'last_28_days') return "Sales in Last 28 Days";
    if(period === 'last_6_months') return "Sales in Last 6 Months";
    if(period === 'last_year') return "Sales in Last Year";
    if(period === 'custom') return "Custom Date Range"
    return "";
}

function getDenominator(period) {
    if(period === 'last_7_days') return 7;
    if(period === 'last_28_days') return 28;
    if(period === 'last_6_months') return 6;
    if(period === 'last_year') return 12;
    if(period === 'custom') calculateDays();
    return 7;
}

