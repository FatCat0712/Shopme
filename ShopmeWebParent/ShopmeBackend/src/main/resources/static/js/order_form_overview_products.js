let fieldProductCost;
let fieldSubtotal;
let fieldShippingCost;
let fieldTax;
let fieldTotal;
$(document).ready(function() {
    fieldProductCost = $("#productCost");
    fieldSubtotal = $("#subTotal");
    fieldShippingCost = $("#shippingCost");
    fieldTax = $("#tax");
    fieldTotal = $("#total");

    formatOrderAmounts();
    formatProductAmounts();

    $("#productList").on("change", ".quantity-input", function (e) {
       updateSubtotalWhenQuantityChanged($(this));
       updateOrderAmounts();
    });

    $("#productList").on("input", ".price-input", function (e) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".cost-input", function (e) {
        updateOrderAmounts();
    });

    $("#productList").on("change", ".ship-input", function (e) {
        updateOrderAmounts();
    });

});

function updateOrderAmounts() {
    let totalCost = 0.0;
    $(".cost-input").each(function(e) {
        let costInputField = $(this);
        let rowNumber = costInputField.attr("rowNumber");
        let quantityField = $("#quantity" + rowNumber).val();
        let productCost = getNumberValueRemoveThousandSeparator(costInputField);

        totalCost += parseInt(quantityField) * productCost;
    });

    setAndFormatNumberForField("productCost", totalCost);

    let orderSubTotal = 0.0;

    $(".subtotal-output").each(function(e) {
       let productSubtotal = getNumberValueRemoveThousandSeparator($(this));
       orderSubTotal += productSubtotal;
    });

    setAndFormatNumberForField("subTotal", orderSubTotal);

    let shippingCost = 0.0;

    $(".ship-input").each(function(e) {
        let productShip = getNumberValueRemoveThousandSeparator($(this));
        shippingCost += productShip;
    });

    setAndFormatNumberForField("shippingCost", shippingCost);

    let tax = getNumberValueRemoveThousandSeparator(fieldTax);
    let orderTotal = orderSubTotal + tax + shippingCost;

    setAndFormatNumberForField("total", orderTotal);

}

function setAndFormatNumberForField(fieldId, fieldValue) {
    let formattedValue = $.number(fieldValue, 2);
    $("#" + fieldId).val(formattedValue);
}

function getNumberValueRemoveThousandSeparator(fieldRef) {
    let fieldValue = fieldRef.val().replace(",","");
    return parseFloat(fieldValue);
}

function  updateSubtotalWhenPriceChanged(input) {
    let priceValue =getNumberValueRemoveThousandSeparator(input);
    let rowNumber = input.attr("rowNumber");

    let quantityField = $("#quantity" + rowNumber);
    let quantityValue = quantityField.val();
    let newSubtotal =  parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subTotal" + rowNumber, newSubtotal);
}

function updateSubtotalWhenQuantityChanged(input) {
    let quantityValue = input.val();
    let rowNumber = input.attr("rowNumber");
    let priceField = $("#price" + rowNumber);
    let priceValue = getNumberValueRemoveThousandSeparator(priceField);
    let newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subTotal" + rowNumber, newSubtotal);
}

function formatProductAmounts() {
    $(".cost-input").each(function(e) {
        formatNumberForField($(this))
    })

    $(".price-input").each(function(e) {
        formatNumberForField($(this))
    })

    $(".subtotal-output").each(function(e) {
        formatNumberForField($(this))
    })

    $(".ship-input").each(function(e) {
        formatNumberForField($(this))
    })
}

function formatOrderAmounts() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);
}

function formatNumberForField(fieldRef) {
    fieldRef.val($.number(fieldRef.val(), 2));
}

function processFormBeforeSubmit() {
    setCountryName();

    removeThousandSeparatorForField(fieldProductCost);
    removeThousandSeparatorForField(fieldSubtotal);
    removeThousandSeparatorForField(fieldShippingCost);
    removeThousandSeparatorForField(fieldTax);
    removeThousandSeparatorForField(fieldTotal);

    $(".cost-input").each(function() {
        removeThousandSeparatorForField($(this));
    });

    $(".price-input").each(function () {
        removeThousandSeparatorForField($(this));
    });

    $(".subtotal-output").each(function () {
       removeThousandSeparatorForField($(this));
    });

    $(".ship-input").each(function() {
        removeThousandSeparatorForField($(this));
    })

}

function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replace(",",""));
}

function setCountryName() {
    let selectedCountry = $("#country option:selected");
    let countryName = selectedCountry.text();
    $("#countryName").val(countryName);
}

