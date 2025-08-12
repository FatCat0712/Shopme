"use strict"

$(document).ready(function() {
    $(".link-vote-review").on("click", function(e) {
        e.preventDefault();
        voteReview($(this));
    });
})

function voteReview(currentLink) {
    let requestURL = currentLink.attr("href");
    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend : function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(voteResult) {
        if(voteResult.successful) {
            $("#modalDialog").on("hide.bs.modal", function() {
                 updateVoteCountAndIcons(currentLink, voteResult);
            })
        }
        showModalDialog("Vote Review", voteResult.message);
    }).fail(function() {
        showErrorModal("Error voting review.");
    })
}

function updateVoteCountAndIcons(currentLink, voteResult) {
    let reviewId = currentLink.attr('reviewId');
    let voteUpLink = $("#linkVoteUp-" + reviewId);
    let voteDownLink = $("#linkVoteDown-" + reviewId);
    let voteCount = $("#voteCount-" + reviewId);

    voteCount.text(voteResult.voteCount + " Votes");
    let message = voteResult.message.toLowerCase();
     if(message.includes("unvoted up")) {
         unhighlightVoteUpIcon(voteUpLink);
     }
   else if(message.includes("unvoted down")) {
         unhighlightVoteDownIcon(voteDownLink);
     }
    else if(message.includes("voted up")) {
        highlightVoteUpIcon(voteUpLink, voteDownLink);
    }
    else if(message.includes("voted down")) {
        highlightVoteDownIcon(voteDownLink, voteUpLink);
    }

}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
    voteUpLink.removeClass("far").addClass("fas");
    voteUpLink.attr("title", "Undo vote up this review");
    voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
    voteDownLink.removeClass("far").addClass("fas");
    voteDownLink.attr("title", "Undo vote down this review");
    voteUpLink.removeClass("fas").addClass("far");
}

function  unhighlightVoteDownIcon(voteDownLink) {
     voteDownLink.attr("title", "Vote down this review");
     voteDownLink.removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink) {
    voteUpLink.attr("title", "Vote up this review");
    voteUpLink.removeClass("fas").addClass("far");
}



