let dropDownCountry;
let dataListStates;
let fieldState;

$(document).ready(function () {
    dropDownCountry = $("#country");
    dataListStates = $("#listStates");
    fieldState = $("#state");

    dropDownCountry.on("change", function () {
        loadStatesByCountry();
        $("#state").val("").focus();
    })
    loadStatesByCountry();
})

function loadStatesByCountry() {
    let selectedCountry = $("#country option:selected");
    let selectedCountryId = selectedCountry.val();

    let url = contextPath + "states/list_by_country/" + selectedCountryId;

    $.get(url, function (response) {
        dataListStates.empty();
        $.each(response, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dataListStates);
        })
    });

}