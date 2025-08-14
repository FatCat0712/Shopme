function submitQuestionCreateForm() {
    let productId = $("#productId").val();
    let questionContent = $("#questionContent").val();
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
       showModalDialog("Post Question", response.message);
    }).fail(function(error) {
        showWarningModal(error.responseJSON.message);
    })
}
