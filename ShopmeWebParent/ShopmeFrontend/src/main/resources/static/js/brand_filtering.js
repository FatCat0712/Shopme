$(document).ready(function() {
      $("#btnFilter").click(function(e) {
                         e.preventDefault();
                        let categoryArr = [];
             //          target filter
                        $("#categoryFilter .form-check-input:checked").each(function() {
                          categoryArr.push($(this).val());
                        })

                        $("#categorySelect option:selected").each(function() {
                            categoryArr.push($(this).val());
                        })

                         const currentUrl = new URL(window.location.href);
                        const searchParams = currentUrl.searchParams;
                       searchParams.delete('category');


                       let categoryAlias;
       //            Add or update query parameter
                         if(categoryArr.length > 0) {
                            categoryAlias = categoryArr.join(',');
                         }


             //            Update the URL and reload the page
                           if(categoryAlias != null || categoryAlias != undefined) {
                                window.location.href = '/brands/page/1?category=' + categoryAlias;
                           }
                           else {
                                 window.location.href = '/brands';
                           }

                  });


    const params =  new URLSearchParams(window.location.search);

    if(params.has('category')) {
           const categories = params.get('category').split(',');
           categories.forEach(category => {
                $(`#categoryFilter .form-check-input[value="${category}"]`).prop('checked',true);
                $(`#categorySelect option[value="${category}"]`).prop('selected',true);
           });

            $('#categorySelect').trigger('change');
    }
});


