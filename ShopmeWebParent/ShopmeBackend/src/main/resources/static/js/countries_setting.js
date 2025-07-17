let buttonLoad;
let dropDownCountries;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;


$(document).ready(function() {
   buttonLoad = $("#buttonLoadCountries");
   dropDownCountries = $("#dropDownCountries");
   buttonAddCountry = $("#buttonAddCountry");
   buttonUpdateCountry = $("#buttonUpdateCountry");
   buttonDeleteCountry = $("#buttonDeleteCountry");
   labelCountryName = $("#labelCountryName");
   fieldCountryName = $("#fieldCountryName");
   fieldCountryCode = $("#fieldCountryCode");

   buttonLoad.on("click" , function() {
      loadCountries();
   });

   dropDownCountries.on("change", function () {
       changeFormStateToSelectedCountry();
   })

    buttonAddCountry.on("click", function () {
        if(buttonAddCountry.val() === "Add") {
            addCountry();
        }
        else {
            changeFormStateToNew();
        }

    })

    buttonUpdateCountry.on("click", function () {
        updateCountry();
    })

    buttonDeleteCountry.click(function () {
        deleteCountry();
    })

});

function deleteCountry() {
    let optionValue = dropDownCountries.val();
    let countryId =optionValue.split("-")[0];
    let url  = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
    }).done(function () {
        $("#dropDownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNew();
        showToastMessage("The country has been deleted");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });
}


function updateCountry() {
    if(!validateFormCountry()) return;
    let url = contextPath + "countries/save";

    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let countryId = dropDownCountries.val().split('-')[0];

    let jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        let updatedValue =  $("#dropDownCountries option:selected");
        updatedValue.val(countryId + "-" + countryCode);
        updatedValue.text(countryName);
        showToastMessage("The new country has been updated");
        changeFormStateToNew();
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });
}

function validateFormCountry() {
    let formCountry = document.getElementById("formCountry");
    if(!formCountry.checkValidity()) {
        formCountry.reportValidity();
        return false;
    }
    return true;
}

function addCountry() {
    if(!validateFormCountry()) return;


    let url = contextPath + "countries/save";

    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();

    let jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
             xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });
}

function  selectNewlyAddedCountry(countryId, countryCode, countryName) {
    let optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountries);
    $("#dropDownCountries option[value='" +optionValue + "']").prop("selected", true);
    fieldCountryName.val('').focus();
    fieldCountryCode.val('');

}

function changeFormStateToNew() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country Name: ");

    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryName.val('').focus();
    fieldCountryCode.val('');

}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country: ");

    let selectedCountryName = $("#dropDownCountries option:selected").text();

    fieldCountryName.val(selectedCountryName);

    let optionValue = dropDownCountries.val();

    let countryCode = optionValue.split("-")[1];

    fieldCountryCode.val(countryCode);



}

function loadCountries() {
    $.get(contextPath +  "countries/list", function (responseJSON) {
         dropDownCountries.empty();

         $.each(responseJSON, function(index, country) {
            $("<option>").val(country.id + "-" + country.code).text(country.name).appendTo(dropDownCountries);
         });

    }).done(function () {
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });

}

function showToastMessage(message) {
        $("#toastMessage").text(message);
        $(".toast").toast('show');
}

