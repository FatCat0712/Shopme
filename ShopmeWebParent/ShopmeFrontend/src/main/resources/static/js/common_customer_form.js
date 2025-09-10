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

     $("#fileImage").change(function() {
            if(!checkFileSize(this)) {
                return;
           }
            showImageThumbnail(this, '#thumbnail');
    });
});


function showImageThumbnail(fileInput, id) {
        let file = fileInput.files[0];
        let reader = new FileReader();
        reader.onload = function(e) {
            $(id).attr("src", e.target.result);
        };
        reader.readAsDataURL(file);
}


function checkFileSize(fileInput) {
        fileSize = fileInput.files[0].size;
       if(fileSize > MAX_FILE_SIZE) {
            fileInput.setCustomValidity("You must choose an image less than " + (MAX_FILE_SIZE/1024) + "KB !");
            fileInput.reportValidity();
            return false;
        }
        else {
            fileInput.setCustomValidity("");
            return true;
        }
}


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