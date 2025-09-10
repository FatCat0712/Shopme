$(document).ready(function () {
   $(document).on("click", function(e) {
        const addToCartButton = $(e.target);
        if(addToCartButton.hasClass("addToCart")) {
           const productId = addToCartButton.attr('productId');
           addToCart(productId);
        }
   });
})

function addToCart(productId) {
    let quantity = $("#quantity" + productId).val();
    let url = contextPath + "cart/add/" + productId  + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        }
    }).done(function (response) {
        const updatedQuantity = response.quantity;
        const message = response.message;
        $("#sumCart").text(updatedQuantity);
        refreshMiniCart();
        showModalDialog('Shopping Cart', message);
    }).fail(function () {
        showErrorModal("Error while adding product to shopping cart");
    })
}

function refreshMiniCart() {
    $.ajax({
        url: "/cart/update_cart_preview",
        type: 'GET'
    }).done(function(fragment) {
        $('#dropdownCartLink').empty().append(fragment);
    })
}