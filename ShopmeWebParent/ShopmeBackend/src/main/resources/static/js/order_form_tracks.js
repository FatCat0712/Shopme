$(document).ready(function () {
    $("#trackList").on("change", ".order-status", function () {
        let orderStatusList = $(this);
        let rowNumber = orderStatusList.attr("rowNumber");
        updateOrderTrackNotesByStatus(orderStatusList, rowNumber);
    })

    $("#link-add-track").on("click", function (e) {
        e.preventDefault();
        let htmlCode = generateTrackHtmlCode();
        $("#trackList").append(htmlCode);
    })

    $("#trackList").on("click", ".link-delete-track", function (e) {
        e.preventDefault();
        let link = $(this);
        let rowNumber = link.attr("rowNumber");

        if(isOnlyOneTrackExist()) {
            showWarningModal("Cannot remove track. The order must have at least one order track");
        }
        else {
            $("#rowTrack" + rowNumber).remove();

            $(".track-count").each(function (index, element) {
                element.innerHTML= index + 1;
            });

            $("#trackList .track-box").each(function (index, element) {
                $(element).attr("id", "rowTrack" + (index + 1));
            });

            $(".link-delete-track").each(function (index, element) {
                $(element).attr("rowNumber", index + 1);
            })
        }

    })

})

function isOnlyOneTrackExist() {
    let numTracks = $(".hiddenTrackId").length;
    return numTracks === 1;
}

function formatCurrentDateTime() {
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hour = date.getHours();
    let minute = date.getMinutes();
    let second = date.getSeconds();

    if(month < 10) month = "0" + month;
    if(day < 10) day = "0" + date;

    if(hour < 10) hour = "0" + hour;
    if(minute < 10) minute = "0" + minute;
    if(second < 10) second = "0" + second;

    return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
}

function  updateOrderTrackNotesByStatus(orderStatusList ,rowNumber) {
    $("#notes" + rowNumber).text(orderStatusList.val().split("|")[0]);
}

function generateTrackHtmlCode() {
    let currentTrackCount = $(".hiddenTrackId").length;
    let nextCount = currentTrackCount + 1;

    return htmlCode = `
          <div class="row border-bottom track-box" style="margin-bottom: 40px" id="rowTrack${nextCount}">
                    <input type="hidden"  name="trackId" class="hiddenTrackId" value="0">
                    <div class="col-sm-2">
                        <div class="track-count">${nextCount}</div>
                        <div><a class="fas fa-trash icon-dark link-delete-track" href="" rowNumber="${nextCount}"></a></div>
                          
                    </div>
                    <div class="col-sm-10">
                        <div class="form-group row">
                            <div class="col-sm-2">
                                <label class="col-form-label" for="time">Time:</label>
                            </div>
                            <div  class="col-sm-10">
                                <input type="datetime-local" name="trackDate" id="time" class="form-control" value="${formatCurrentDateTime()}"  style="max-width: 300px"/>
                            </div>

                        </div>
                        <div class="form-group row">
                            <div class="col-sm-2">
                                <label class="col-form-label" >Status:</label>
                            </div>
                            <div  class="col-sm-10">
                                <select class="form-control order-status" rowNumber="${nextCount}" style="max-width: 150px" name="trackStatus">
                                    <option value="Order was placed by the customer|NEW">NEW</option>
                                    <option value="Order was cancelled|CANCELLED">CANCELLED</option>
                                    <option value="Order is being processing|PROCESSING">PROCESSING</option>
                                    <option value="Products were packaged|PACKAGED">PACKAGED</option>
                                    <option value="Shipper picked the package|PICKED">PICKED</option>
                                    <option value="Shipper is delivering the package|SHIPPING">SHIPPING</option>
                                    <option value="Customer received products|DELIVERED">DELIVERED</option>
                                    <option value="Products were returned|RETURNED">RETURNED</option>
                                    <option value="Customer has paid the order|PAID">PAID</option>
                                    <option value="Customer has been refunded|REFUND">REFUND</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div  class="col-sm-2">
                                <label class="col-form-label">Notes:</label>
                            </div>
                            <div  class="col-sm-10">
                                <textarea id="notes${nextCount}" class="form-control" style="max-width: 300px" name="trackNotes"></textarea>
                            </div>
                        </div>
                </div>
            </div>
           `;
}
