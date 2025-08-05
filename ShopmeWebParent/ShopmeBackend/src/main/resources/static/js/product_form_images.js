let extraImagesCount = 0;
$(document).ready(function() {
    $("input[name='extraImage']").each(function(index) {
          extraImagesCount++;
        $(this).change(function() {
            if(!checkFileSize(this)) {
                return;
            }
             showExtraImageThumbnail(this, index);
        })
    })

    $("a[name='linkRemoveExtraImage']").each(function(index) {
        $(this).click(function (e) {
            e.preventDefault();
            removeExtraImage(index);
        })
    })
});

function showExtraImageThumbnail(fileInput, index) {
         let file = fileInput.files[0];

         let fileName = file.name;
         let imageNameHiddenField = $("#imageName" + index);
         if(imageNameHiddenField.length) {
             imageNameHiddenField.val(fileName);
         }


        let reader = new FileReader();
        reader.onload = function(e) {
            $("#extraThumbnail" + index).attr("src", e.target.result);
        };

        reader.readAsDataURL(file);

        if(index >= extraImagesCount - 1 ){
            addNextExtraImageSection(index);
        }

}

function addNextExtraImageSection(index) {
    let htmlExtraImage = `
            <div class="col border m-3 p-2 extraImageBox" id="divExtraImage${index}">
                <div id="extraImageHeader${index}">
                    <label>Extra Image #${index + 1} :</label>
                </div>
                <div class="m-2">
                    <img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid" src="${defaultImageThumbnail}"/>
                </div>
                <div>
                    <input type="file" name="extraImage" accept="image/png, image/jpeg" onchange="showExtraImageThumbnail(this, ${index})">
                </div>
            </div>
    `;

    let htmlLinkRemove = `
            <a class="btn fas fa-times-circle fa-2x icon-dark float-right" title="Remove this image" href="javascript:removeExtraImage(${index})"></a>
    `;

    $("#divProductImages").append(htmlExtraImage);

    $("#extraImageHeader" + (index)).append(htmlLinkRemove);

     extraImagesCount++;


}

function removeExtraImage(index) {
        $("#divExtraImage" + index).remove();
        extraImagesCount = 0;
        $(".extraImageLabel").each(function (index, element) {
            let currentText = $(element).text();
            let indexOfNum = currentText.indexOf('#');
            $(element).text(currentText.substring(0, indexOfNum + 1) + (index + 1) + currentText.substring(indexOfNum + 2));
            extraImagesCount++;
        })

       $(".extraImageBox").each(function (index, element) {
           $(element).attr('id', 'divExtraImage' + index);
       })
}



