// Sales report by Date
let chartData;
let chartOptions;
let totalGrossSales = 0;
let totalNetSales = 0;
let totalItems = 0;

$(document).ready(function () {
    setupButtonEventHandler("_date", loadSalesReportByDate);
});

function loadSalesReportByDate(period) {
    let requestURL = contextPath + "reports/sales_by_date/";
    if(period === "custom") {
        let startDate = $("#startDate_date").val();
        let endDate = $("#endDate_date").val();
        requestURL += startDate + "/" + endDate;
    }else {
        requestURL += period;
    }
    $.get(requestURL, function(responseJSON) {
        prepareChartDataForSalesReportByDate(responseJSON);
        customizeChartForSalesReportByDate(period);
        drawChartForSalesReportByDate(period);
        setSalesAmount(period, '_date', "Total Orders")
    })
}
function prepareChartDataForSalesReportByDate(responseJSON) {
    const labels = [];
    const grossSales = [];
    const netSales = [];
    const orders = [];

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function(index, reportItem) {
        labels.push(reportItem.identifier);
        grossSales.push(parseFloat(reportItem.grossSales));
        netSales.push(parseFloat(reportItem.netSales));
        orders.push(parseInt(reportItem.ordersCount))

        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
       totalItems += parseInt(reportItem.ordersCount);
    });

    chartData = {
           labels: labels,
           datasets: [
                {
                    label: 'Gross Sales',
                    data: grossSales,
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    yAxisID: 'y' // first axis
                },
                {
                    label: 'Net Sales',
                    data: netSales,
                    backgroundColor: 'rgba(75, 192, 192, 0.6)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    yAxisID: 'y'  // first axis
                },
                {
                    label: 'Orders',
                    data: orders,
                    type: 'line',
                    borderColor: 'rgba(255, 99, 132, 1)',
                   backgroundColor: 'rgba(255, 99, 132, 0.3)',
                   yAxisID: 'y1'
                }
           ]
    }


}

function customizeChartForSalesReportByDate(period) {
    chartOptions = {
        type: 'bar',
        data: {
            labels: chartData.labels,
            datasets: chartData.datasets
        },
        options: {
             responsive: true,
             maintainAspectRatio: false,
            title: {
                display: true,
                text: getChartTitle(period),
                fontSize: 18
            },
            legend: {
                position: 'top'
            },
             scales: {
                 yAxes: [
                         {
                            id: 'y',
                            type: 'linear',
                            position: 'left',
                            scaleLabel: {
                                display: true,
                                labelString: 'Sales Amount'
                            },
                            ticks: {
                                callback: function(value) {
                                    return '$' + value;
                                }
                            }
                         },
                         {
                            id: 'y1',
                            type: 'linear',
                            position: 'right',
                            scaleLabel: {
                                  display: true,
                                  labelString: 'Number of Orders'
                            },
                            gridLines: {
                                 drawOnChartArea: false
                            }
                         }
                ],
                 xAxes: [
                     {
                          scaleLabel: {
                            display: true,
                            labelString: 'Period'
                          }
                     }
                 ]
           }
        }
    }
}

function drawChartForSalesReportByDate() {
        let ctx = document.getElementById('chart_sales_by_date').getContext('2d');
         if (window.salesChartInstance) {
                window.salesChartInstance.destroy();
        }
       window.salesChartInstance = new Chart(ctx, chartOptions);
}




