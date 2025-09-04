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
        customizeChartForSalesReportByCategory(period);
        drawChartForSalesReportByCategory();
        setSalesAmount(period, '_category', "Total products")
    })
}

 function generateRandomColors(numColors) {
        const backgroundColors = [];
        const borderColors =[];

       for(let i = 0; i < numColors; i++) {
            const r = Math.floor(Math.random() * 255);
            const g = Math.floor(Math.random() * 255);
            const b= Math.floor(Math.random() * 255);

            backgroundColors.push(`rgba(${r}, ${g}, ${b}, 0.6)`);
            borderColors.push(`rgba(${r}, ${g}, ${b}, 1)`);
       }

       return {backgroundColors, borderColors};
    }



function prepareChartDataForSalesReportByCategory(responseJSON) {
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

        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    })

    const percentage = grossSales.map(value => ((value/totalGrossSales) * 100).toFixed(2));

    const colors = generateRandomColors(labels.length);


    chartData = {
        labels : labels,
         datasets: [{
                label: 'Gross Sales',
                data: percentage,
               backgroundColor: colors.backgroundColors,
               borderColor: colors.borderColors,
               borderWidth: 1
       }]
    }
}



function customizeChartForSalesReportByCategory(period) {
    chartOptions = {
        type: 'pie',
        data: chartData,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            title: {
                display: true,
                text: getChartTitle(period),
                fontSize: 18
            },
            legend: {
                position: 'right'
            }
        }
    }
}

function drawChartForSalesReportByCategory() {
          let ctx = document.getElementById('chart_sales_by_category').getContext('2d');
         if (window.salesChartInstance) {
               window.salesChartInstance.destroy();
           }
          window.salesChartInstance = new Chart(ctx, chartOptions);
}




