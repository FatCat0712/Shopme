$(document).ready(function() {
     const searchWrapper = document.querySelector('.search-wrapper');
      const searchBtn = document.querySelector('#searchButton');

      searchBtn.addEventListener('click', (e) => {
          e.stopPropagation();
        searchWrapper.classList.toggle('active');
        if (searchWrapper.classList.contains('active')) {
          searchWrapper.querySelector('.search-input').focus();
        }

     });

      document.addEventListener('click', (e) => {
           //Hide the menus if visible
           if(!searchWrapper.contains(e.target)) {
                searchWrapper.classList.remove('active');
           }
     });

      document.addEventListener('keydown', (e) => {
                //Hide the menus if visible
                if(e.key == 'Escape') {
                     searchWrapper.classList.remove('active');
                }
          });
})