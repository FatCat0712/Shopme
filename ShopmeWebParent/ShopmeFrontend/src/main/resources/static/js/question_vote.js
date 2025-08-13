"use strict"

$(document).ready(function() {
    $(".link-vote-question").on("click", function(e) {
        e.preventDefault();
        voteQuestion($(this));
    })
});

function voteQuestion(currentLink) {
    let requestURL = currentLink.attr("href");
    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function(voteResult) {
        console.log(voteResult);
        if(voteResult.successful) {
            $("#modalDialog").on("hide.bs.modal", function() {
                updateQuestionVoteCountAndIcons(currentLink, voteResult);
            })
        }
        showModalDialog("Vote Question", voteResult.message);
    }).fail(function(e) {
        showErrorDialog("Error voting question");
    });
}

function updateQuestionVoteCountAndIcons(link, voteResult) {
    let questionId = link.attr("questionId");
    let linkVoteQuestionUp = $("#linkVoteQuestionUp-" + questionId);
    let linkVoteQuestionDown = $("#linkVoteQuestionDown-" + questionId);
    let questionVoteCount = $("#questionVoteCount-" + questionId);
    let message = voteResult.message;
    questionVoteCount.text(voteResult.voteCount + " Votes");

    if(message.includes("unvoted up")) {
        unhighlightLinkVoteQuestionUp(linkVoteQuestionUp);
    }
    else if(message.includes("unvoted down")) {
       unhighlightLinkVoteQuestionDown(linkVoteQuestionDown);
    }
    else if(message.includes("voted up")) {
        highlightLinkVoteQuestionUp(linkVoteQuestionUp, linkVoteQuestionDown);
    }
     else if(message.includes("voted down")) {
        highlightLinkVoteQuestionDown(linkVoteQuestionDown, linkVoteQuestionUp);
    }
}

function unhighlightLinkVoteQuestionUp(linkVoteQuestionUp) {
     linkVoteQuestionUp.removeClass("icon-blue").addClass("icon-dark");
}

function unhighlightLinkVoteQuestionDown(linkVoteQuestionDown) {
    linkVoteQuestionDown.removeClass("icon-blue").addClass("icon-dark");
}

function highlightLinkVoteQuestionUp(linkVoteQuestionUp, linkVoteQuestionDown) {
    linkVoteQuestionUp.removeClass("icon-dark").addClass("icon-blue");
    linkVoteQuestionDown.removeClass("icon-blue").addClass("icon-dark");
}

function highlightLinkVoteQuestionDown(linkVoteQuestionDown, linkVoteQuestionUp) {
       linkVoteQuestionDown.removeClass("icon-dark").addClass("icon-blue");
      linkVoteQuestionUp.removeClass("icon-blue").addClass("icon-dark");
}