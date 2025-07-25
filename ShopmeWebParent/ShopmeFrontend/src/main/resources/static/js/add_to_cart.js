$(document).ready(function () {
    $("#buttonAddToCart").on("click", function(e) {
        e.preventDefault();
        addToCart();
    })
})

function addToCart() {
    let quantity = $("#quantity" + productId).val();

    let url = contextPath + "cart/add/" + productId  + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        }
    }).done(function (response) {
        showModalDialog('Shopping Cart',response);
    }).fail(function () {
        showErrorModal("Error while adding product to shopping cart");
    })

}