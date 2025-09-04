$(document).ready(function() {
    $("#linkAddProduct").on("click", function(e) {
          e.preventDefault();
          let url = $(this).attr('href');
          $("#addProductModal").on("shown.bs.modal", function() {
                $(this).find("iframe").attr("src", url);
          });
          $("#addProductModal").modal('show');
    });

    $("#productList").on("click", ".link-move-left--product", function(e) {
        e.preventDefault();
        let item = $(this).closest(".product-box");
        let prev = item.prev();
        if(prev.length) {
            prev.before(item);
        }
    })

    $("#productList").on("click", ".link-move-right--product", function(e) {
        e.preventDefault();
        let item = $(this).closest(".product-box");
        let next = item.next();
        if(next.length) {
            next.after(item);
        }
    });

    $("#productList").on("click", ".link-remove--product", function(e) {
        e.preventDefault();
        removeProduct($(this));
    });

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

function getProductInfo(productId) {
    let requestURL = contextPath + "products/get/" + productId;
    $.get(requestURL, function(product) {
        let productName = product.shortName;
        let mainImagePath = product.imagePath;
        let htmlCode = generateProductHtmlCode(productId, productName, mainImagePath);
        $("#productList").append(htmlCode);
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
    }).always(function() {
      $ ("#addProductModal").modal("hide");
    });
}

function addProduct(productId, productName) {
    $("#addProductModal").modal("hide");
    getProductInfo(productId);
}


function generateProductHtmlCode(productId,productName, mainImagePath) {
    let nextCount = $(".hiddenProductId").length + 1;
    let itemId = "col" + nextCount;
    let htmlCode = `
    <div id="${itemId}" class="col-sm-3 text-center product-box">
             <input type="hidden" name="sectionProducts" value="${productId}" class="hiddenProductId" />
             <div class="d-flex justify-content-between mb-2">
                  <a class="fas fa-chevron-left icon-dark link-move-left--product text-decoration-none" href=""></a>
                  <a class="fas fa-trash icon-dark link-remove--product text-decoration-none" href="" colNumber="${nextCount}"></a>
                  <a class="fas fa-chevron-right icon-dark link-move-right--product text-decoration-none" href=""></a>
             </div>
             <div>
                 <img src="${mainImagePath}" alt="${productName}" width="100px"/>
             </div>
             <div>
                 <b>${productName}</b>
             </div>
    </div>
    `
    return htmlCode;
}

function removeProduct(link) {
    let colNumber = link.attr("colNumber");
    $("#col" + colNumber).remove();

    $("#productList .product-box").each(function (index, element) {
        $(element).attr("id", "col" + (index + 1));
    })

    $(".link-remove--product").each(function(index, element) {
         $(element).attr("colNumber", index + 1);
    });
}
