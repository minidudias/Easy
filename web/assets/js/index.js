async function checkSignedInOrNot() {
    const response = await fetch("CheckSignedInOrNot");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const response_DTO = json.response_dto;

        if (response_DTO.success) {
            //signed in            
            const user = response_DTO.content;

            let headerLinksShowLink1 = document.getElementById("header-links-show-link1");
            headerLinksShowLink1.remove();
            let headerLinksShowLink2 = document.getElementById("header-links-show-link2");
            headerLinksShowLink2.remove();

            let headerLinksShowLink3 = document.getElementById("header-links-show-link3");
            headerLinksShowLink3.innerHTML = user.first_name + "'s Profile";

            let signInButton = document.getElementById("sign-in-button");
            signInButton.href = "SignOut";
            signInButton.innerHTML = "Sign Out";

            let signInDiv = document.getElementById("sign-in-div");
            signInDiv.remove();

        } else {
            //not signed in            
            console.log("Not signed in");
        }


        //setting-up 3 latest products carousel
        const latest_three_products = json.latest_three_products;

        let number = 1;
        latest_three_products.forEach(product => {
            document.getElementById("carousel-product-title" + number).innerHTML = product.title;
            document.getElementById("carousel-product-link" + number).href = "single-product.html?id=" + product.id;
            document.getElementById("carousel-product-image" + number).src = "product-images/" + product.id + "/image1.png";
            document.getElementById("carousel-product-price" + number).innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(product.price);
            number++;
        });



        $('.slider-content-activation-one').slick({
            infinite: true,
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false,
            dots: false,
            focusOnSelect: false,
            speed: 500,
            fade: true,
            autoplay: true,
            asNavFor: '.slider-thumb-activation-one',
        });

        $('.slider-activation-one').slick({
            infinite: true,
            autoplay: true,
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false,
            dots: true,
            fade: true,
            focusOnSelect: false,
            speed: 400

        });

        $('.slider-activation-two').slick({
            infinite: true,
            autoplay: true,
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false,
            dots: true,
            fade: true,
            adaptiveHeight: true,
            cssEase: 'linear',
            speed: 400
        });

        $('.team-slide-activation').slick({
            infinite: true,
            slidesToShow: 3,
            slidesToScroll: 3,
            arrows: true,
            dots: false,
            prevArrow: '<button class="slide-arrow prev-arrow"><i class="fal fa-long-arrow-left"></i></button>',
            nextArrow: '<button class="slide-arrow next-arrow"><i class="fal fa-long-arrow-right"></i></button>',
            responsive: [{
                    breakpoint: 1199,
                    settings: {
                        slidesToShow: 3,
                        slidesToScroll: 3
                    }
                },
                {
                    breakpoint: 991,
                    settings: {
                        slidesToShow: 2,
                        slidesToScroll: 2
                    }
                },
                {
                    breakpoint: 576,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
            ]
        });
    }
}



async function loadIndexProducts() {
    const response = await fetch("LoadIndexProducts");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        let htmlContentOfAnIndexProduct = document.getElementById("index-product");
        document.getElementById("index-product-main").innerHTML = "";
        json.productList.forEach(itemObjectFromProductList => {
            let clonedHtmlContentOfAnIndexProduct = htmlContentOfAnIndexProduct.cloneNode(true);

            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-a1").href = "single-product.html?id=" + itemObjectFromProductList.id;
            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-image1").src = "product-images/" + itemObjectFromProductList.id + "/image1.png";
            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-image2").src = "product-images/" + itemObjectFromProductList.id + "/image1.png";
            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-title").href = "single-product.html?id=" + itemObjectFromProductList.id;
            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-title").innerHTML = itemObjectFromProductList.title;

            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(itemObjectFromProductList.price);

            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-color-border").style.borderColor = itemObjectFromProductList.color.name;
            clonedHtmlContentOfAnIndexProduct.querySelector("#index-product-color").style.backgroundColor = itemObjectFromProductList.color.name;

           

            document.getElementById("index-product-main").appendChild(clonedHtmlContentOfAnIndexProduct);
        });
    }
}