$(document).ready(function() {
     const searchWrapper = document.querySelector('.search-wrapper');
      const searchBtn = document.querySelector('#searchButton');

      searchBtn.addEventListener('click', () => {
        searchWrapper.classList.toggle('active');
        if (searchWrapper.classList.contains('active')) {
          searchWrapper.querySelector('.search-input').focus();
        }
     });
})