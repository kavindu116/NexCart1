function loadData() {
    getUserData();
    getCityData();
    loadAccountDash();
}

async function getUserData() {
    const response = await fetch("MyAccount");

    if (response.ok) {
        const  json = await response.json();

        document.getElementById("uname").innerHTML = `Hello, ${json.fristname} ${json.lastname}`;
        document.getElementById("fname1").innerHTML = `Welcome back, ${json.fristname} `;
        document.getElementById("since").innerHTML = `Member since, ${json.createdAt}`;
        document.getElementById("fname").value = json.fristname;
        document.getElementById("lname").value = json.lastname;
        document.getElementById("password").value = json.password;

        if (json.hasOwnProperty("addressList") && json.addressList !== undefined) {
            console.log(json);

            let email;
            let line1;
            let line2;
            let city;
            let pscode;
            let CityId;

            json.addressList.forEach(address => {
                email = address.user.email;
                line1 = address.line1;
                line2 = address.line2;
                city = address.city.city;
                pscode = address.postalCode;
                CityId = address.city.id;

            });

            document.getElementById("line1").value = line1;
            document.getElementById("line2").value = line2;
            document.getElementById("postalCode").value = pscode;
            document.getElementById("citySelect").value = Number(CityId);
            document.getElementById("email").innerHTML = email;

        }

    } else {

    }
}

async function getCityData() {
    const response = await fetch("CityData");
    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const citySelect = document.getElementById("citySelect");

        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.city;
            option.value = city.id;
            citySelect.appendChild(option);

        });

    }
}

async function saveChanges() {

    const fname = document.getElementById("fname").value;
    const lname = document.getElementById("lname").value;
    const line1 = document.getElementById("line1").value;
    const line2 = document.getElementById("line2").value;
    const postalCode = document.getElementById("postalCode").value;
    const citySelect = document.getElementById("citySelect").value;
    const password = document.getElementById("password").value;
    const nPassword = document.getElementById("newPassword").value;
    const cPassword = document.getElementById("confirmPassword").value;




    const userDataObject = {
        fristName: fname,
        lastName: lname,
        line1: line1,
        line2: line2,
        posCode: postalCode,
        cityId: citySelect,
        currentPassword: password,
        newPassword: nPassword,
        confirmPassword: cPassword


    };
    const userDataJson = JSON.stringify(userDataObject);

    const response = await fetch(
            "saveChanges",
            {
                method: "PUT",
                body: userDataJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const Json = await response.json();
        if (Json.ststus) {
            sweetAlert(Json.message, "info");
        } else {
            sweetAlert(Json.message, "info");
        }
    } else {

    }
}

async function loadAccountDash() {
    const response = await fetch("AccountDetails");
    if (response.ok) {
        const Json = await response.json();
        console.log(Json);

        document.getElementById("OrderCount").innerHTML = Json.allOrderCount + ` Orders`;
        document.getElementById("ProductCount").innerHTML = Json.allProductCount + ` Products`;



        let tBody = document.getElementById("tbody");
        tBody.innerHTML = "";
        let total;
        let maxResults = 5; // Set your desired limit here

        for (let i = 0; i < Math.min(Json.orderList.length, maxResults); i++) {
            let item = Json.orderItemList[i];
            let content = `
        <tr>
            <td>${item.order.id}</td>
            <td>${item.order.orderAt}</td>
            <td>${item.product.title}</td>
            <td>${item.qty}</td>
            <td>RS : ${Number(item.qty) * Number(item.product.price)}</td>
            <td><span class="order-status status-delivered">${item.orderStatus.name}</span></td>
        </tr>
    `;
            tBody.innerHTML += content;
        }


//load all orders
        let maxResults1 = 10;
        let tBody1 = document.getElementById("tbody1");
        tBody1.innerHTML = "";
        for (let i = 0; i < Math.min(Json.orderList.length, maxResults1); i++) {
            let item = Json.orderItemList[i];
            let content1 = `
        <tr>
            <td>${item.order.id}</td>
            <td>${item.order.orderAt}</td>
            <td>${item.product.title}</td>
            <td>${item.qty}</td>
            <td>RS : ${Number(item.qty) * Number(item.product.price)}</td>
            <td><span class="order-status status-delivered">${item.orderStatus.name}</span></td>
        </tr>
    `;
            tBody1.innerHTML += content1;
        }

        //loadmyProducts

        let MyProductCard = document.getElementById("myproductCard");
        MyProductCard.innerHTML = "";

        Json.productList.forEach(product => {
            let productData = ` <div class="col-md-6 mb-4">
                                        <div class="card h-100">
                                            <div class="row g-0">
                                                <div class="col-md-4">
                                                    <img src="${"Product-Image\\" + product.id + "\\image1.png"}"
                                                        class="img-fluid rounded-start h-100" alt="Product">
                                                </div>
                                                <div class="col-md-8">
                                                    <div class="card-body">
                                                        <h5 class="card-title">${product.title}</h5>
                                                        <p class="card-text text-muted small">Category: ${product.category.name}</p>
                                                        <div class="d-flex justify-content-between align-items-center">
                                                            <span class="fw-bold">Rs : ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price)}</span>
                                                            <span class="badge bg-success">Quntity ${product.qty}</span>
                                                        </div>
                                                        
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>`;

            MyProductCard.innerHTML += productData;

        });




    } else {
        console.log("error");
    }
}





