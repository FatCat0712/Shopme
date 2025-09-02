function clearFilter() {
       window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName) {
    let entityId = link.attr("entityId");
      $("#yesButton").attr("href", link.attr("href"));
     $("#confirmText").text("Are you sure you want to delete this " + entityName + " ID " + entityId + "?" );
     let myModal = new bootstrap.Modal(document.getElementById("confirmModal"));
     myModal.show();

}

function handleDetailLink(linkClass, modalId) {
    $(linkClass).on("click" ,function (e) {
            e.preventDefault();
            let linkDetailURL = $(this).attr("href");
            let modalElement = $(modalId);
            $(modalId).find(".modal-content").load(linkDetailURL, function() {
                let modal = new bootstrap.Modal(modalElement[0]);
                modal.show();
            });
    })
}
