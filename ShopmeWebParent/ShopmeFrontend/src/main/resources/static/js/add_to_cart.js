$(document).ready(function () {
    $("#buttonAddToCart").on("click", function(e) {
        e.preventDefault();
         console.log('add');
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
        const updatedQuantity = response.split(' ')[0];
        $("#sumCart").text(updatedQuantity);
        showModalDialog('Shopping Cart',response);
    }).fail(function () {
        showErrorModal("Error while adding product to shopping cart");
    })

}