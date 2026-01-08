async function cartItemsLoad(){
    const response = await fetch(
        "CartItemsLoad"
    );
    
    
    const popup = Notification();

    if (response.ok){
        const json = await response.json();
        
        let cartItemsPanel = document.getElementById("cart-items-panel");
        let aCartItem = document.getElementById("a-cart-item");
        
        if (json.length === 0){        
            cartItemsPanel.innerHTML ="";
            
            popup.error({
               message: "There's nothing in your cart. Hurry up, start shopping!" 
            });
//            window.location = "index.html";
            
        }else{
                       
            cartItemsPanel.innerHTML ="";
            
            let totalQty =0;
            let total =0;
            
            json.forEach( itemObject =>{
                let itemSubTotal = itemObject.product.price * itemObject.qty;
                total += itemSubTotal;
                
                totalQty += itemObject.qty;
                
                let cartItemClone = aCartItem.cloneNode(true);
                cartItemClone.querySelector("#cart-item-link").href = "single-product.html?id=" + itemObject.product.id;
                cartItemClone.querySelector("#cart-item-image").src = "product-images/" + itemObject.product.id + "/image1.png";
                cartItemClone.querySelector("#cart-item-title").innerHTML = itemObject.product.title;
                cartItemClone.querySelector("#cart-item-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
                ).format(itemObject.product.price);
                cartItemClone.querySelector("#cart-item-qty").value = itemObject.qty;
                cartItemClone.querySelector("#cart-item-qty-multiply-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
                ).format(itemSubTotal);
        
                cartItemsPanel.appendChild(cartItemClone);
            });
            
            document.getElementById("cart-qty").innerHTML = totalQty;
            document.getElementById("cart-total").innerHTML = new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                }
            ).format(total);
            
            
        }        

    } else {
        popup.error({
           message: "Unable to process the request" 
        });
    }    
}