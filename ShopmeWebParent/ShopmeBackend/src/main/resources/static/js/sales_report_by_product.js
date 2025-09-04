let tableOptions;
let salesDataTable;

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
        drawChartForSalesReportByProduct(responseJSON);
        setSalesAmount(period, '_product', "Total products")
    })
}

function customizeChartForSalesReportByProduct() {
    tableOptions = {
        perPage: 5,
       searchable: true,
       sortable: true,
       labels: {
            placeholder: "Search...",
            noRows: "No data available",
            info: "Showing {start} to {end} of {rows} entries"
       }
    }
}

function drawChartForSalesReportByProduct(responseJSON) {
        const table = $('#chart_sales_by_product');
         if (!table.length) {
             // Table doesn't exist on the page, skip DataTable initialization
             return;
         }

         totalGrossSales = 0;
         totalNetSales = 0;
         totalItems = 0;

         // Destroy existing DataTable if it exists
         if (salesDataTable) {
             salesDataTable.destroy();
         }

//         Clear existing rows
         const tbody = table.find("tbody");
           tbody.empty();


//         Append new  rows

            if(!responseJSON || responseJSON.length === 0) {
                  tbody.append(`
                           <tr>
                               <td class="datatable-empty" colspan="4">No data available</td>
                               <td></td>
                               <td></td>
                               <td></td>
                           </tr>
                   `);
                   setSalesAmount('last_7_days', '_product', "Total products")

            }
            else {
                    $.each(responseJSON, function(index, reportItem) {
                         totalGrossSales += parseFloat(reportItem.grossSales || 0);
                        totalNetSales += parseFloat(reportItem.netSales || 0);
                        totalItems += parseInt(reportItem.productsCount || 0);

                      tbody.append(`
                                <tr>
                                    <td>${reportItem.identifier || ''}</td>
                                    <td>${reportItem.productsCount || 0}</td>
                                    <td>${reportItem.grossSales || 0}</td>
                                    <td>${reportItem.netSales || 0}</td>
                                </tr>
                      `);


                });
            }




        customizeChartForSalesReportByProduct();

       salesDataTable = new simpleDatatables.DataTable(table[0], tableOptions);
}




