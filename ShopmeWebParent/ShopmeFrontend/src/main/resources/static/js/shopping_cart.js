$(document).ready(function () {
    $(".link-minus").on("click", function(e) {
        e.preventDefault();
        decreaseQuantity($(this));
    })

    $(".link-plus").on("click", function (e) {
        e.preventDefault();
        increaseQuantity($(this));
    })

    $(".link-remove").on("click", function (e) {
        e.preventDefault();
        removeProduct($(this));

    })
});

function decreaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) - 1;
    if(newQuantity > 0 && newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    }
    else {
        showWarningModal('Minium quantity is 1');
    }
}

function increaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) + 1;
    if(newQuantity > 0 && newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    }
    else {
        showWarningModal('Maximum quantity is 5');
    }
}

function updateQuantity(productId, quantity) {
    let url = contextPath + "cart/update/" + productId  + "/" + quantity;
    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        }
    }).done(function (updatedSubtotal) {
         updateSubtotal(updatedSubtotal, productId);
         updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product quantity.");
    })
}

function updateSubtotal(updatedSubtotal, productId) {
        let formattedSubtotal = $.number(updatedSubtotal, 2);
        $("#subtotal" + productId).text(formattedSubtotal);
}

function updateTotal() {
     let total = 0.0;
     let productCount = 0;
     $(".subtotal").each(function(index, element) {
         productCount++;
         total += parseFloat(element.innerHTML.replaceAll(",",""));
     })

    if(productCount < 1) {
        showEmptyShoppingCart();
    }
    else {
        let formattedTotal = $.number(total, 2);
        $("#total").text(formattedTotal);
    }


}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
        let url = link.attr("href");
        $.ajax({
            type: "DELETE",
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            }
        }).done(function (response){
            let rowNumber = link.attr("rowNumber");
           removeProductHTML(rowNumber);
            updateTotal();
            updateCountNumbers();
           showModalDialog("Shopping Cart", response);
        }).fail(function (response) {
            showErrorModal(response);
        });
}

function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
    $(".divCount").each(function(index, element){
         element.innerHTML = "" + (index + 1);
    });
}


