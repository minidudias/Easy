// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
   console.log("Payment completed. OrderID:" + orderId);
    // Note: validate the payment and show success or failure page to the customer
    
    const popup = Notification();
    
    window.location = "index.html";
    
    popup.success({
        message:"Order placed, Thank you!" 
    });
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:"  + error);
};



async function loadData(){
    const response = await fetch("LoadCheckout");

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            console.log(json);

            const address = json.address;
            const cityList = json.cityList;
            const cartList = json.cartList;

            const citySelect = document.getElementById("city");
            cityList.forEach(city => {
                let cityOptionTag = document.createElement("option");
                cityOptionTag.value = city.id;
                cityOptionTag.innerHTML = city.name;
                citySelect.appendChild(cityOptionTag);
            });

            //load current address
            let currentAssddressCheckBox = document.getElementById("checkbox1");
            currentAssddressCheckBox.addEventListener("change", e => {

                let first_name = document.getElementById("first-name");
                let last_name = document.getElementById("last-name");
                let city = document.getElementById("city");
                let address1 = document.getElementById("address1");
                let address2 = document.getElementById("address2");
                let postal_code = document.getElementById("postal-code");
                let mobile = document.getElementById("mobile");

                if (currentAssddressCheckBox.checked) {

                    first_name.value = address.first_name;
                    last_name.value = address.last_name;

                    city.value = address.city.id;
                    city.disabled = true;
                    city.dispatchEvent(new Event("change"));

                    address1.value = address.line1;
                    address2.value = address.line2;
                    postal_code.value = address.postal_code;
                    mobile.value = address.mobile;

                } else {
                    first_name.value = "";
                    last_name.value = "";

                    city.value = 0;
                    city.disabled = false;
                    city.dispatchEvent(new Event("change"));


                    address1.value = "";
                    address2.value = "";
                    postal_code.value = "";
                    mobile.value = "";

                }
            });

            //load cart item

            let st_tbody = document.getElementById("st-tbody");
            let st_item_tr = document.getElementById("st-item-tr");
            let st_subtotal_tr = document.getElementById("st-subtotal-tr");
            let st_shipping_tr = document.getElementById("st-shipping-tr");
            let st_order_total_tr = document.getElementById("st-order-total-tr");

            st_tbody.innerHTML = "";

            let subtotal = 0;

            cartList.forEach(item => {

                let st_item_clone = st_item_tr.cloneNode(true);
                st_item_clone.querySelector("#st-item-title").innerHTML = item.product.title;
                st_item_clone.querySelector("#st-item-qty").innerHTML = item.qty;

                let st_item_subtotal = item.product.price * item.qty;
                subtotal += st_item_subtotal;

                st_item_clone.querySelector("#st-item-subtotal").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(st_item_subtotal);

                st_tbody.appendChild(st_item_clone);

            });

            st_subtotal_tr.querySelector("#st-subtotal").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(subtotal);
            st_tbody.appendChild(st_subtotal_tr);

            //update total on city change
            citySelect.addEventListener("change", e => {

                //update shipping charges

                let item_count = cartList.length;
                let shipping_amount = 0;

                if (citySelect.value == 1) {
                    //colombo
                    shipping_amount = item_count * 100;
                } else {
                    //out of colombo
                    shipping_amount = item_count * 300;
                }

                st_shipping_tr.querySelector("#st-shipping-charge").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(shipping_amount);
                st_tbody.appendChild(st_shipping_tr);

                //update total
                let total = subtotal + shipping_amount;
                st_order_total_tr.querySelector("#st-order-total-amount").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(total);
                st_tbody.appendChild(st_order_total_tr);
            });


        } else {
            window.location = "index.html";
        }
    } else {
        window.location = "index.html";
    }
}



async function checkout(){
    let isLastAddressChecked = document.getElementById("checkbox1").checked;

    //get shipping address info
    let first_name = document.getElementById("first-name");
    let last_name = document.getElementById("last-name");
    let city = document.getElementById("city");
    let address1 = document.getElementById("address1");
    let address2 = document.getElementById("address2");
    let postal_code = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");
        
    //request data (in JSON)
    const data = {
        isLastAddressChecked: isLastAddressChecked ,
        first_name: first_name.value ,
        last_name: last_name.value ,
        city_id: city.value ,
        address1: address1.value ,
        address2: address2.value ,
        postal_code: postal_code.value ,
        mobile: mobile.value
    };
    
    //fetch
    const response = await fetch("Checkout",
        {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        }
    );
    
    const popup = Notification();
    
    if (response.ok) {
        const json = await response.json();
        
        if(json.success){
            //start payhere payment
            payhere.startPayment(json.payHereJson);
            
            popup.success({
                message: "PayHere checkout process has started!"
            });
           
        }else{
            popup.error({
                message: json.message
            });            
        }
        
    } else {
        popup.error({
            message:"Cannot be processed, please try again!" 
        });
    }
}