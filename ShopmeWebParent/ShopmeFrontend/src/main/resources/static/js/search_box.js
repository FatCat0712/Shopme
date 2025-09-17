$(document).ready(function() {
     const searchWrapper = document.querySelector('.search-wrapper');
      const searchBtn = document.querySelector('#searchButton');
      const previewSearch =  $(".previewSearchResult")
       let debounceTimer;


      searchBtn.addEventListener('click', (e) => {
          e.stopPropagation();
        searchWrapper.classList.toggle('active');
        if (searchWrapper.classList.contains('active')) {
          searchWrapper.querySelector('.search-input').focus();
        }
     });



     $(".search-input").on("input", function(e) {
          clearTimeout(debounceTimer);
            const searchTerm = $(this).val().trim();
             if(searchTerm.length > 0) {
                previewSearch.removeClass("d-none").addClass("d-block w-100");

                $(".spinnerSearch").addClass("show");
                 debounceTimer = setTimeout(() => {
                         sendRequest(searchTerm);
                }, 500);
             }
             else {
                 $(".spinnerSearch").removeClass("show");
                previewSearch.find("li").not(".spinnerSearch").remove();
                previewSearch.removeClass("d-block w-100").addClass('d-none');
             }

     });

     function sendRequest(searchTerm) {
           const data = {keyword: searchTerm};
           previewSearch.find("li").not(".spinnerSearch").remove();

           $.get("/search_preview", data, function(response) {
                $(".spinnerSearch").removeClass("show");
                previewSearch.append(response);
           })

     }

      document.addEventListener('click', (e) => {
           //Hide the menus if visible
           if(!searchWrapper.contains(e.target)) {
                searchWrapper.classList.remove('active');
               $(".search-input").val('');
               previewSearch.find("li").not(".spinnerSearch").remove();
               previewSearch.removeClass("d-block w-100");
           }
     });

      document.addEventListener('keydown', (e) => {
                //Hide the menus if visible
                if(e.key == 'Escape'){
                     searchWrapper.classList.remove('active');
                     $(".search-input").val('');
                     previewSearch.find("li").not(".spinnerSearch").remove();
                     previewSearch.removeClass("d-block w-100");
                }
          });
})