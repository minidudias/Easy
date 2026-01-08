async function addToCart(productId, addingQuantity){
    const response = await fetch(
        "AddToCart?id="+ productId +"&qty="+ addingQuantity
    );
    
    
    const popup = Notification();

    if (response.ok){
        const json = await response.json();
        console.log(json.content);
        
        if(json.success){
            popup.success({
                message: json.content
            });
        }else{
            popup.error({
                message: json.content
            });
        }

    } else {
        popup.error({
           message: "Unable to add the product to the cart. please try again!" 
        });
    }
}


async function loadSingleProduct(){
    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {        
        const id = parameters.get("id");
        
        const response = await fetch("LoadSingleProduct?id="+id);
        
        if(response.ok){            
            const jsonResponse = await response.json();
            console.log(jsonResponse.product.id);
            
            productId = jsonResponse.product.id;            
            document.getElementById("image1").src = "product-images/"+productId+"/image1.png";           
            document.getElementById("image1-thumb").src = "product-images/"+productId+"/image1.png";
            document.getElementById("image2").src = "product-images/"+productId+"/image2.png";
            document.getElementById("image2-thumb").src = "product-images/"+productId+"/image2.png";
            document.getElementById("image3").src = "product-images/"+productId+"/image3.png"; 
            document.getElementById("image3-thumb").src = "product-images/"+productId+"/image3.png";
            
            document.getElementById("product-title").innerHTML = jsonResponse.product.title;
            document.getElementById("product-published-on").innerHTML = jsonResponse.product.date_time;
            document.getElementById("product-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(jsonResponse.product.price);
            document.getElementById("product-category").innerHTML = jsonResponse.product.model.category.name;
            document.getElementById("product-model").innerHTML = jsonResponse.product.model.name;
            document.getElementById("product-condition").innerHTML = jsonResponse.product.product_condition.name;
            document.getElementById("product-qty").innerHTML = jsonResponse.product.qty;
            
            document.getElementById("color-border").style.borderColor = jsonResponse.product.color.name;
            document.getElementById("color-background").style.backgroundColor= jsonResponse.product.color.name;
            
            document.getElementById("prodcut-description").innerHTML= jsonResponse.product.description;
            
            document.getElementById("add-to-cart-main").addEventListener(
                "click",
                (event) => {
                    addToCart(
                        jsonResponse.product.id,
                        document.getElementById("add-to-cart-qty").value
                    );
                    event.preventDefault();
                }            
            );

            let htmlContentOfASimilarProduct = document.getElementById("similar-product");
            document.getElementById("similar-product-main").innerHTML = "";
            jsonResponse.productList.forEach( itemObjectFromProductList =>{
                let clonedHtmlContentOfASimilarProduct = htmlContentOfASimilarProduct.cloneNode(true);

                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-a1").href = "single-product.html?id=" + itemObjectFromProductList.id;
                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-image").src = "product-images/" + itemObjectFromProductList.id + "/image1.png";
                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-a2").href = "single-product.html?id=" + itemObjectFromProductList.id;
                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-title").innerHTML = itemObjectFromProductList.title;
                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-condition").innerHTML = itemObjectFromProductList.product_condition.name;

                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
                ).format(itemObjectFromProductList.price);

                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-color-border").style.borderColor = itemObjectFromProductList.color.name;
                clonedHtmlContentOfASimilarProduct.querySelector("#similar-product-color").style.backgroundColor = itemObjectFromProductList.color.name;

                clonedHtmlContentOfASimilarProduct.querySelector("#similar-produc-add-to-cart-sub").addEventListener(
                    "click",
                    (event) => {
                        addToCart(itemObjectFromProductList.id, 1);
                        event.preventDefault();
                    }    
                );

                document.getElementById("similar-product-main").appendChild(clonedHtmlContentOfASimilarProduct);
            });



            $('.recent-product-activation').slick({
                infinite: true,
                slidesToShow: 4,
                slidesToScroll: 4,
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
                    breakpoint: 479,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
                ]
            });

        } else {
            window.location = "index.html";
        }

    } else {
        window.location = "index.html";
    }
}