let dropDownAllCountries;
let dropDownStates;
let labelStateName;
let fieldStateName;

let buttonLoadAllCountries;
let buttonAddState;
let buttonUpdateState;
let buttonDeleteState;

$(document).ready(function() {
    buttonLoadAllCountries= $("#buttonLoadAllCountries");
    buttonAddState = $("#buttonAddState");
    buttonUpdateState = $("#buttonUpdateState");
    buttonDeleteState = $("#buttonDeleteState");

    dropDownAllCountries = $("#dropDownAllCountries");
    dropDownStates = $("#dropDownStates");

    labelStateName = $("#labelStateName");
    fieldStateName = $("#fieldStateName");

    buttonLoadAllCountries.on("click", function () {
        loadAllCountries();
    })

    dropDownAllCountries.on("change", function() {
        fieldStateName.val('');
        loadStateByCountry();
    })

    dropDownStates.on("change", function () {
        changeFormStateToSelectedState();
    })

    buttonAddState.on("click", function () {
        if(buttonAddState.val() === "Add") {
            addState();
        }
        else {
            changeFormStateToNewState();
        }
    })

    buttonUpdateState.on("click", function () {
        updateState();
    })

    buttonDeleteState.on("click", function () {
        deleteState();
    })
})

function changeFormStateToNewState() {
    buttonAddState.val('Add');
    fieldStateName.val('').focus();

    labelStateName.text("State Name: ");

    buttonUpdateState.prop("disabled", true);
    buttonDeleteState.prop("disabled", true);
}

function validateFormState() {
    let formState = document.getElementById("formState");
    if(!formState.checkValidity()) {
        formState.reportValidity();
        return false;
    }
    return true;
}

function addState() {
    if(!validateFormState()) return;
    let url = contextPath + "states/save";
    let countryId = dropDownAllCountries.val().split('-')[0];
    let stateName = fieldStateName.val();

    let jsonData = {name: stateName, country: {id: countryId}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (stateId) {
        selectNewlyAddedState(stateId, stateName);
        loadStateByCountry("updateState");
        showToastMessage("The new state has been added");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });
}

function updateState() {
    if(!validateFormState()) return;
    let url = contextPath + "states/save";
    let countryId = dropDownAllCountries.val().split('-')[0];
    let stateId = dropDownStates.val().split('-')[0];
    let stateName = fieldStateName.val();

    let jsonData = {id: stateId, name: stateName, country: {id: countryId}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (stateId) {
        let updatedOption =  $("#dropDownStates option:selected");
        updatedOption.val(stateId + "-" + stateName);
        updatedOption.text(stateName );
        showToastMessage("The state has been updated");
        changeFormStateToNewState();
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });
}

function deleteState() {
    let optionValue = dropDownStates.val();
    let stateId = optionValue.split('-')[0];
    let url = contextPath + "states/delete/" + stateId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
    }).done(function () {
        $("#dropDownStates option[value='" + optionValue + "']").remove();
        changeFormStateToNewState();
        showToastMessage("The state has been deleted");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server");
    });
}

function  selectNewlyAddedState(stateId, stateName) {
    let optionValue = stateId + "-" + stateName;
    $("<option>").val(optionValue).text(stateName).appendTo(dropDownStates);
    $("#dropDownStates option[value='" +optionValue + "']").prop("selected", true);
    fieldStateName.val('').focus();
}


function changeFormStateToSelectedState() {
    buttonAddState.prop("value", "New");
    buttonUpdateState.prop("disabled", false);
    buttonDeleteState.prop("disabled", false);

    labelStateName.text("Selected State: ");

    let selectedStateName = $("#dropDownStates option:selected").text();

    fieldStateName.val(selectedStateName);
}

function loadStateByCountry() {
    let countryId = dropDownAllCountries.val().split("-")[0];
    let url = contextPath + "states/list_by_country/" + countryId;

    $.get(url, function (response) {
        dropDownStates.empty();
        $.each(response, function (index, state) {
            let optionValue = state.id + "-" + state.name;
            $("<option>").val(optionValue).text(state.name).appendTo(dropDownStates);
        });
    }).done(function () {
        showToastMessage("All states have been loaded");

    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server");
    });

}

function loadAllCountries() {
    let url = contextPath + "countries/list";
    dropDownAllCountries.empty();
    $.get(url, function (response) {
        $.each(response, function (index,  country) {
            let optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownAllCountries);
        });
    }).done(function () {
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server");
    });
}