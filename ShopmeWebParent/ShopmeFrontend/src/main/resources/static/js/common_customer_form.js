let dropDownCounty;
let dataListStates;
let fieldState;

$(document).ready(function () {
    dropDownCounty = $("#country");
    dataListStates = $("#listStates");
    fieldState = $("#state");

    dropDownCounty.on("change", function () {
        loadStatesForCountry();
        fieldState.val('').focus();
    })
});

function loadStatesForCountry() {
    let selectedCountry = $("#country option:selected");
    let selectedCountryId = selectedCountry.val();


    let url = contextPath + "settings/list_states_by_country/" + selectedCountryId;

    $.get(url, function (response) {
        dataListStates.empty();
        $.each(response, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dataListStates);
        })
    });
}


function checkPasswordMatch(confirmPassword) {
    if(confirmPassword.value !== $("#password").val()) {
        console.log(confirmPassword.value);
        confirmPassword.setCustomValidity("Passwords do not match!");
    }
    else {
        confirmPassword.setCustomValidity("");
    }
}


function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}