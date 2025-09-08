$(document).ready(function () {
       let currentPath = window.location.pathname.replace(/\/$/, ""); // remove trailing slash
       if(currentPath === "") {
               $(".navbar #home").addClass("active");
       }
       else {
               $(".navbar .nav-link").each(function () {
                       let linkPath = $(this).attr("href").replace(/\/$/, "");
                       if (window.location.pathname.replace(/\/$/, "") === linkPath || window.location.pathname.replace(/\/$/, "").startsWith(linkPath)) {
                           $(this).addClass("active");
                       } else {
                           $(this).removeClass("active");
                       }
               });
       }


});