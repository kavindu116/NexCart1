
// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    //console.log("Payment completed. OrderID:" + orderId);
    
     sweetAlert("Success", "Payment completed. OrderID:" + orderId, "success");

};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
    
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};





async function loadUserData() {
    const response = await fetch("loadCheckOutData");
    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            console.log(Json);
            const userAddress = Json.userAddress;
            const cityList = Json.cityList;
            const cartItem = Json.cartList;
            const deliveryTypeList = Json.deliveryTypeList;

            let city = document.getElementById("city-selsect");



            cityList.forEach(item => {
                let option = document.createElement("option");
                option.value = item.id;
                option.innerHTML = item.city;
                city.appendChild(option);
            });

            const currentAddressCheckBox = document.getElementById("checkbox1");
            currentAddressCheckBox.addEventListener("change", function () {
                let fname = document.getElementById("fname");
                let lname = document.getElementById("lname");
                let line1 = document.getElementById("line1");
                let line2 = document.getElementById("line2");
                let postalCode = document.getElementById("postalCode");
                let mobile = document.getElementById("mobile");

                if (currentAddressCheckBox.checked) {
                    fname.value = userAddress.user.fristName;
                    lname.value = userAddress.user.lastName;
                    city.value = userAddress.city.id;
                    city.disabled = true;
                    city.dispatchEvent(new Event("change"));
                    line1.value = userAddress.line1;
                    line2.value = userAddress.line2;
                    postalCode.value = userAddress.postalCode;
                    mobile.value = userAddress.mobile;

                } else {
                    fname.value = "";
                    lname.value = "";
                    city.value = "";
                    city.disabled = false;
                    city.dispatchEvent(new Event("change"));
                    line1.value = "";
                    line2.value = "";
                    postalCode.value = "";
                    mobile.value = "";
                }

            });

            //loadCartItems
            let stBody = document.getElementById("STBody");
            stBody.innerHTML = "";

            let total = 0;
            let itemCount = 0;

            cartItem.forEach(cart => {
                let content = ` <div class="cart-item flex justify-between items-center transition duration-200">
                            <div class="flex items-center">
                                <div class="w-16 h-16 bg-gray-200 rounded-md overflow-hidden mr-4">
                                    <img src="${"Product-Image\\" + cart.product.id + "\\image1.png"}" alt="Product"
                                        class="w-full h-full object-cover">
                                </div>
                                <div>
                                    <h3 class="font-medium text-gray-800">${cart.product.title}</h3>
                                    <p class="text-sm text-gray-500">${cart.product.discription}</p>
                                </div>
                            </div>
                            <div class="text-right">
                                <p class="font-medium">Rs : ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(cart.product.price)}</p>
                                <p class="text-sm text-gray-500">Qty: ${cart.qty}</p>
                            </div>
                        </div>`;
                itemCount += cart.qty;
                let itemsubTotal = Number(cart.qty) * Number(cart.product.price);
                total += itemsubTotal;
                stBody.innerHTML += content;
            });

            document.getElementById("subTotal").innerHTML =
                    `Rs: ` + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);

            let shippingCharges = 0;

            city.addEventListener("change", (e) => {

                let cityName = city.options[city.selectedIndex].innerHTML;

                if (cityName === "Colombo") {

                    shippingCharges = deliveryTypeList[0].price;

                } else {
                    shippingCharges = deliveryTypeList[1].price;
                }

                document.getElementById("shipping").innerHTML =
                        `Rs: ` + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(shippingCharges);

                document.getElementById("total").innerHTML = `Rs: ` +new Intl.NumberFormat("en-US", {minimumFractionDigits: 2})
                        .format(shippingCharges + total);



            });



        } else {
            if (Json.message === "Youer Cart is Empty!") {
                sweetAlert("Error", "Empty Cart.please Add some product!", "error");
            } else {
                sweetAlert("Error", Json.message, "error");
            }
        }
    } else {
        if (response.status === 401) {
            window.location = "Sign-In.html";
        }
    }
}


async function checkout() {

    let fname = document.getElementById("fname");
    let lname = document.getElementById("lname");
    let line1 = document.getElementById("line1");
    let line2 = document.getElementById("line2");
    let postalCode = document.getElementById("postalCode");
    let mobile = document.getElementById("mobile");
    let checkbox = document.getElementById("checkbox1").checked;
    let city = document.getElementById("city-selsect");
    
    
    let data = {
        isCurrentAddress: checkbox,
        fname: fname.value,
        lname: lname.value,
        city: city.value,
        line1: line1.value,
        line2: line2.value,
        postalCode: postalCode.value,
        mobile: mobile.value

    };
    
    let dataJson = JSON.stringify(data);

    const response = await fetch("Checkout", {
        method: "POST",
        header: {
            "Content-Type": "application/json"
        },
        body: dataJson
    });
    
    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            console.log(Json);
            //payHereProcess
            payhere.startPayment(Json.payHereJson);
        } else {
            sweetAlert("Error1", Json.message, "error");
        }
    } else {
     sweetAlert("Error2", "Somthing went Wrong.Please Try Again Later", "error");
    }

}

