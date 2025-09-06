
$(document).ready(function() {
      $("#askQuestion").on("click", function(e) {
        $("#questionForm").removeClass("d-none");
      })
})



function submitQuestionCreateForm() {
    let productId = $("#productId").val();
    questionContent = $("#questionContent").val();
    sendQuestionCreateRequest(productId, questionContent);
    return false;
}

function sendQuestionCreateRequest(productId, questionContent) {
    let requestURL = contextPath + "questions/save";
    let requestBody = {questionContent: questionContent, productId: productId};
    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(requestBody),
        contentType: 'application/json'
    }).done(function(response) {
        console.log(response);
        if(response.successful) {
            showModalDialog("Post Question", response.message);
            $("#questionContent").val('');
        }
    }).fail(function(error) {
        showWarningModal(error.responseJSON.message);
    })
}
