dropdownBrands = $("#brand");
dropdownCategories = $("#category");
$(document).ready(function() {

     $("#shortDescription").richText();
     $("#fullDescription").richText();


    dropdownBrands.change(function() {
            dropdownCategories.empty();
            getCategories();
    })


    getCategoriesForNewForm();

});

function getCategoriesForNewForm() {
    catIdField = $("#categoryId").val();
    editMode = false;

    if(catIdField.length) {
        editMode = true;
    }

    if(!editMode) {
        getCategories();
    }
}

function getCategories() {
 brandId = dropdownBrands.val();
  url = brandModuleURL + "/" + brandId + "/categories";

  csrfValue = $("input[name='_csrf']").val();

  params = {brandId: brandId, _csrf: csrfValue};

  $.get(url, params, function(responseJson) {
         $.each(responseJson, function(index, category) {
             $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
         });
  })
}

function checkUniqueName(form) {
    productId = $("#id").val();
    productName = $("#name").val();

    csrfValue = $("input[name='_csrf']").val();

    params = {id: productId, name: productName, _csrf: csrfValue};

    url = urlCheckName;

    $.post(url, params, function(response) {
         if(response == "OK") {
             form.submit();
         }
         else if(response == "Duplicate") {
             showWarningModal("There is another product having the name " + productName);
         }
         else {
             showErrorModal("Unknown response from server");
         }
    }).fail(function() {
       showErrorModal("Could not connect the server");
    });

    return false;
}

