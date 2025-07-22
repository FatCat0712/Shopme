let dropDownCountries;
let dropDownStates;
$(document).ready(function () {
    dropDownCountries = $("#dropDownCountries");
    dropDownStates = $("#dropDownStates");

    dropDownCountries.on("change", function () {
        loadStatesByCountry(dropDownCountries.val());
    })
})

function loadStatesByCountry(country) {
    let countryId = country.split("-")[0];
    console.log(countryId);
    let url = contextPath + "states/list_by_country/" + countryId;

    $.get(url, function (response) {
        $.each(response, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dropDownStates);
        });
    }).fail(function () {
        console.log('Could not load states');
    })
}