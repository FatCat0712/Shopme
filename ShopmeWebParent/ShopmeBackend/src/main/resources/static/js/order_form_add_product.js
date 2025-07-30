$(document).ready(function () {
    $("#products").on("click", "#linkAddProduct", function (e) {
        e.preventDefault();
        let link = $(this);
        let url = link.attr("href");
        $("#addProductModal").on("shown.bs.modal", function () {
           $(this).find("iframe").attr("src", url);
        }).modal();
    })
});

function isProductAlreadyAdded(newProductId) {
    let productExist = false;
    $(".hiddenProductId").each(function(e) {
        let productId = $(this).val();
        if(productId === newProductId) {
            productExist = true;
        }
    });
    return productExist;
}

function addProduct(productId, productName) {
    $("#addProductModal").modal("hide");
    getShippingCost(productId);
}

function getShippingCost(productId) {
    let selectedCountry = $("#country option:selected");
    let countryId = selectedCountry.val();
    let state = $("#state").val();
    if(state.length === 0) {
        state = $("#city").val();
    }

    let requestURL = contextPath + "get_shipping_cost";
    let params = {productId: productId, countryId: countryId, state: state};

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: params
    }).done(function (shippingCost) {
       console.log("Shipping cost = " + shippingCost)
        getProductInfo(productId, shippingCost);

    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
        let shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    }).always(function() {
        $("#addProductModal").modal("hide");
    });
}

function getProductInfo(productId, shippingCost) {
    let requestURL = contextPath + "products/get/" + productId;
    $.get(requestURL, function(product) {
        let productName = product.name;
        let mainImagePath = contextPath.substring(0,contextPath.length - 1) + product.imagePath;
        let productCost = $.number(product.cost, 2);
        let productPrice = $.number(product.price,2);
        let htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost)
        $("#productList").append(htmlCode);
    }).fail(function (err) {
        showWarningModal(err.reponseJSON.message);
    })
}

function generateProductCode(productId,productName, mainImagePath, productCost, productPrice, shippingCost) {
    let nextCount = $(".hiddenProductId").length + 1;
    let quantityId = "quantity" + nextCount;
    let priceId = "price" + nextCount;
    let subTotalId = "subTotal" + nextCount;
    let htmlCode = `
                                <div class="border rounded p-2">
                                 <input type="hidden" name="productId" value="${productId}" class="hiddenProductId" />
                                    <div class="row">
                                        <div class="col-1">
                                            <div>${nextCount}</div>
                                        </div>
                                        <div class="col-3">
                                            <img src="${mainImagePath}" class="img-fluid" alt=""/>
                                        </div>
                                    </div>
                                    <div class="row m-2">
                                        <b>${productName}</b>
                                    </div>
                                    <div class="row m-2">
                                        <table>
                                            <tr>
                                                <td>Product Cost:</td>
                                                <td>
                                                    <input type="text" required class="form-control cost-input m-1" value="${productCost}" style="max-width: 140px" rowNumber="${nextCount}"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>Quantity:</td>
                                                <td>
                                                    <input type="number" step="1" min="1" max="5" required
                                                           class="form-control m-1 quantity-input"
                                                           id="${quantityId}"
                                                          value="1" style="max-width: 140px"
                                                           rowNumber="${nextCount}"
                                                    />
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>Unit Price:</td>
                                                <td>
                                                    <input type="text"
                                                           class="form-control price-input m-1"
                                                            value="${productPrice}" style="max-width: 140px"
                                                           id="${priceId}"
                                                            rowNumber="${nextCount}"
                                                           required />
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>Subtotal:</td>
                                                <td>
                                                    <input type="text"  required class="form-control subtotal-output m-1" value="${productPrice}" style="max-width: 140px" id="${subTotalId}" readonly/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>Shipping Cost:</td>
                                                <td>
                                                    <input type="text"  required class="form-control ship-input m-1" value="${shippingCost}" style="max-width: 140px"/>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                                <div class="row">&nbsp;</div>
    `
    return htmlCode;
}

