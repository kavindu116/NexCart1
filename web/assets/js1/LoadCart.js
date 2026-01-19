function reload(){
    window.location.reload();
}


async function LoadCartItem() {
    const response = await fetch("LoadCartItem");
    if (response.ok) {
        const Json = await response.json();
        console.log(Json);
        if (Json.status) {
            const Card_Body = document.getElementById("Card-Body");
            Card_Body.innerHTML = "";
            let total = 0;
            let totalQty = 0;

            Json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;

                let tableData = ` <tr class="cart-item">
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-20 w-20">
                                                <img class="h-full w-full object-cover rounded" src="Product-Image//${cart.product.id}//image1.png" alt="Premium Cotton T-Shirt">
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">${cart.product.title}</div>
                                                <div class="text-sm text-gray-500">Color: ${cart.product.color.value} | Size: ${cart.product.size.value}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">Rs : ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.product.price)}</td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <button class="text-gray-500 focus:outline-none focus:text-indigo-600 quantity-btn" data-action="decrease">
                                                <i class="fas fa-minus"></i>
                                            </button>
                                            <input type="text" class="mx-2 w-10 text-center border rounded-md" value="${cart.qty}" readonly>
                                            <button class="text-gray-500 focus:outline-none focus:text-indigo-600 quantity-btn" data-action="increase">
                                                <i class="fas fa-plus"></i>
                                            </button>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">Rs : ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(productSubTotal)}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <button class="text-red-500 hover:text-red-700 remove-item" onclick = "removeItem(${cart.product.id});">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>`;

                Card_Body.innerHTML += tableData;
            });

            document.getElementById("qty").innerHTML = totalQty;
            document.getElementById("Total").innerHTML = `RS:` + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);

        } else {
            sweetAlert("Error", Json.message, "error");
        }

    } else {
        sweetAlert("Error", "Cart Item Load Failed", "error");
    }
}

async function removeItem(productId){
  
     const response = await fetch("RemoveItem?id=" + productId + "");
     
     if (response.ok){
          const Json = await response.json();
         if(Json.status){
             sweetAlert("Success", Json.message, "success");
             if(Json.message === "Product removed from cart."){
                 LoadCartItem();
                 window.location.reload();
             }
             
             console.log(Json.message);
         }else{
            sweetAlert("Error", Json.message, "error");
         }
     }else{
         sweetAlert("Error", "Unable to remove product.", "error");
     }
     
}

