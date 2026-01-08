async function getDataForSearch() {
    const response = await fetch("GetDataForSearch");

    const popup = Notification();

    if (response.ok) {
        const json = await response.json();

        loadOptionLists("category", json.categoryList, "name");
        loadOptionLists("condition", json.conditionList, "name");
        loadOptionLists("color", json.colorList, "name");

        
        updateProductView(json);
        advancedProductSearch(0);

    } else {
        popup.error({
            message: "Unable to search for products!"
        });
    }
}


function loadOptionLists(prefix, dataList, whatProperty) {
    let options = document.getElementById(prefix + "-options");
    let optionLi = document.getElementById(prefix + "-options-li");
    options.innerHTML = "";

    dataList.forEach(data => {
        let optionLiClone = optionLi.cloneNode(true);

        if (prefix == "color") {
            optionLiClone.style.borderColor = data[whatProperty];
            optionLiClone.querySelector("#" + prefix + "-options-a").style.backgroundColor = data[whatProperty];
        } else {
            optionLiClone.querySelector("#" + prefix + "-options-a").innerHTML = data[whatProperty];
        }

        options.appendChild(optionLiClone);
    });

    const Options = document.querySelectorAll('#' + prefix + '-options li');
    Options.forEach(option => {
        option.addEventListener('click', function () {
            Options.forEach(opt => opt.classList.remove('chosen'));
            this.classList.add('chosen');
        });
    });
}


async function advancedProductSearch(firstResult) {
    const popup = Notification();
    
    let search_text = document.getElementById("search-text").value;
    let category_name = document.getElementById("category-options").querySelector(".chosen")?.querySelector("a").innerHTML;
    let condition_name = document.getElementById("condition-options").querySelector(".chosen")?.querySelector("a").innerHTML;
    let color_name = document.getElementById("color-options").querySelector(".chosen")?.querySelector("a").style.backgroundColor;

    let price_start = $('#slider-range').slider('values', 0);
    let price_end = $('#slider-range').slider('values', 1);

    let sort_text = document.getElementById("st-sort").value;

    const data = {
        search_text: search_text,
        category_name: category_name,
        condition_name: condition_name,
        color_name: color_name,
        price_start: price_start,
        price_end: price_end,
        sort_text: sort_text,
        first_result: firstResult
    };

    const response = await fetch(
            "AdvancedProductSearch",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            console.log(json.success);
            console.log(json.productList);

            updateProductView(json);

        } else {
            console.log(json.success);
            console.log(json.productList);
        }

    } else {
        popup.error({
            message: "Unable to search for products!"
        });
        
        console.log("error");               
    }
}



var st_product = document.getElementById("st-product");
var st_pagination_item = document.getElementById("st-pagination-item");

var current_page = 0;

function updateProductView(json) {
    let st_product_container = document.getElementById("st-product-container");

    st_product_container.innerHTML = "";

    json.productList.forEach(product => {
        let st_product_clone = st_product.cloneNode(true);

        st_product_clone.querySelector("#st-product-a1").href = "single-product.html?id=" + product.id;
        st_product_clone.querySelector("#st-product-img").src = "product-images/" + product.id + "/image1.png";
        st_product_clone.querySelector("#st-product-title").innerHTML = product.title;
        st_product_clone.querySelector("#st-product-title").href = "single-product.html?id=" + product.id;
        st_product_clone.querySelector("#st-product-price").innerHTML = new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                }
        ).format(product.price);


        st_product_container.appendChild(st_product_clone);
    });

    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";

    let product_count = json.countOfAllProducts;
    const product_per_page = 6;
    let pages = Math.ceil(product_count / product_per_page);

    if (current_page != 0) {
        let st_pagination_item_clone_prev = st_pagination_item.cloneNode(true);
        st_pagination_item_clone_prev.innerHTML = "⭠️";
        st_pagination_item_clone_prev.addEventListener("click", e => {
            current_page--;
            advancedProductSearch(current_page * 6);
        });
        st_pagination_container.appendChild(st_pagination_item_clone_prev);
    }

    for (let i = 0; i < pages; i++) {
        let st_pagination_item_clone = st_pagination_item.cloneNode(true);
        st_pagination_item_clone.innerHTML = i + 1;

        st_pagination_item_clone.addEventListener("click", e => {
            current_page = i;
            advancedProductSearch(i * 6);

        });

        if (i == current_page) {
            st_pagination_item_clone.className = "axil-btn btn-bg-secondary ms-1 me-1" ;
        } else {
            st_pagination_item_clone.className = "axil-btn btn-bg-primary ms-1 me-1" ;
        }

        st_pagination_container.appendChild(st_pagination_item_clone);
    }



    if (current_page != (pages - 1)) {
        let st_pagination_item_clone_next = st_pagination_item.cloneNode(true);
        st_pagination_item_clone_next.innerHTML = "➝";
        st_pagination_item_clone_next.addEventListener("click", e => {
            current_page++;
            advancedProductSearch(current_page * 6);
        });
        st_pagination_container.appendChild(st_pagination_item_clone_next);
    }
}