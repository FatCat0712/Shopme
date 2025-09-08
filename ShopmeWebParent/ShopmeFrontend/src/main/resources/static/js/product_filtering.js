$(document).ready(function() {
       $("#toggleBrands").on("click", function(){
         const extras = document.querySelectorAll(".extra-brand");

          extras.forEach(el => el.classList.toggle("d-none"));

          const currentText = $(this).text();

          $(this).text(currentText === 'View more' ?  'View less' : 'View more');
      });


       $('#categorySelect').select2({
          theme: 'bootstrap-5'
       });

       $('#brandFilter').on("click", function(e){
            const brandBox = $(e.target).closest('.brand-box');
            if(brandBox) {
                 brandBox.toggleClass('active-brand');
            }
       });


     $("#btnProductFilter").on("click", function(e) {
            e.preventDefault();
            let brandArr = [];
            let categoryArr = [];
            let inStock = '';
            let onSales = '';
            let rating = '';


            $("#brandFilter .active-brand").each(function() {
                  const brandName = $(this).attr('brandName');
                  brandArr.push(brandName);
            });

            $("#categoryFilter option:selected").each(function() {
                const categoryAlias = $(this).val();
                categoryArr.push(categoryAlias);
            });

            inStock = $("#availabilityFilter .form-check-input:checked").val();

            onSales = $("#discountFilter .form-check-input:checked").val();

            rating = $('#ratingFilter input[name="rating"]:checked').val();


           const currentUrl = new URL(window.location.href);
            const searchParams = currentUrl.searchParams;
           searchParams.delete('brand');
           searchParams.delete('category');
           searchParams.delete('inStock');
           searchParams.delete('onSales');
           searchParams.delete('rating');


            let criteria = '';

            if(brandArr.length > 0) {
                criteria += 'brand=' + brandArr.join(',');
             }

             if(categoryArr.length > 0) {
                const paramName = criteria !== '' ? criteria + '&category=' : 'category=';
                criteria = paramName + categoryArr.join(',');
             }

             if(inStock !== undefined && inStock !== '') {
                const paramName = criteria !== '' ? criteria + '&inStock=' : 'inStock=';
                criteria = paramName + inStock;
             }

              if(onSales !== undefined && onSales !== '') {
                const paramName = criteria !== '' ? criteria + '&onSales=' : 'onSales=';
                criteria = paramName + onSales;
             }

              if(rating !== undefined && rating !== '') {
                const paramName = criteria !== '' ? criteria + '&rating=' : 'rating=';
                criteria = paramName + rating;
             }
                //            Update the URL and reload the page
               if(criteria !== '') {
                    window.location.href = '/products/page/1?' + criteria;
               }
               else {
                     window.location.href = '/products';
               }
     });

<!--             auto checked after page loading-->

         const params = new URLSearchParams(window.location.search);

         // brand auto-selected
       if(params.has('brand')) {
            const brands = params.get('brand').split(',');
            brands.forEach(brand => {
                $('#brandFilter .brand-box[brandname="' + brand  + '"]').addClass('active-brand');
            })
       }

       if(params.has('category')) {
             const categories = params.get('category').split(',');
            categories.forEach(category => {
                $('#categoryFilter option[value="' + category  + '"]').prop('selected', true);
            })
       }

       if(params.has('inStock')) {
            const inStock = params.get('inStock');
            $(`#availabilityFilter .form-check-input[value="${inStock}"]`).prop('checked', true);
       }

        if(params.has('onSales')) {
            const onSales = params.get('onSales');
            $(`#discountFilter .form-check-input[value="${onSales}"]`).prop('checked', true);
       }


         if(params.has('rating')) {
            const rating = params.get('rating');
            $(`input[type="radio"][name="rating"][value="${rating}"]`).prop('checked',true);
         }
});

