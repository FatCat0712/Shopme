    // vegetable carousel
    $(document).ready(function() {
            $(".vegetable-carousel").owlCarousel({
                         autoplay: true,
                         smartSpeed: 1500,
                         center: false,
                         dots: true,
                         loop: true,
                         margin: 25,
                         nav : true,
                         navText : [
                             '<i class="bi bi-arrow-left"></i>',
                             '<i class="bi bi-arrow-right"></i>'
                         ],
                         responsiveClass: true,
                         responsive: {
                             0:{
                                 items:1
                             },
                             576:{
                                 items:1
                             },
                             768:{
                                 items:2
                             },
                             992:{
                                 items:3
                             },
                             1200:{
                                 items:4
                             }
                         }
             });

              $(".thumbnail-carousel").owlCarousel({
                    loop: false,              // set true if you want infinite scrolling
                    margin: 5,
                    nav: true,                // show arrows
                    dots: false,              // hide dots below
                    autoWidth: false,          // let images keep natural width
                    items: 4,
                    navText: ["<i class='fas fa-arrow-left'></i>","<i class='fas fa-arrow-right'></i>"],
                    responsiveClass: true,
                    responsive: {
                        0:{
                            items:1
                        },
                        576:{
                            items:1
                        },
                        768:{
                            items:2
                        },
                        992:{
                            items:4
                        },
                        1200:{
                            items:5
                        }
                    }// customize arrows
                });
    })
