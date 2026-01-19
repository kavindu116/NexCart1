function loadData() {
    loadCategorys();
    loadOrderStatus();
    loadOrders();
}

async function AddCategory() {
    const category = document.getElementById("category").value;
    //console.log(category);
    const add = {

        categoryName: category
    };

    const addcatJson = JSON.stringify(add);

    const response = await fetch(
            "AddCategory",
            {
                method: "POST",
                body: addcatJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");

        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Add Category!", "error");
        ;
    }
}

async function loadCategorys() {
    const response = await fetch("AddCategory");

    if (response.ok) {
        const Json = await response.json();
        let tBody = document.getElementById("tbody");
        tBody.innerHTML = "";
        console.log(Json);
        Json.forEach(category => {
            let data = `<tr>
              <td class="p-2 border">${category.name}</td>
              <td class="p-2 border">
                
                <button type="button" class="text-red-600 hover:underline ml-2" onclick="DeleteCategory(${category.id});">Delete</button>
              </td>
            </tr>`;
            tBody.innerHTML += data;
        });

    } else {
        sweetAlert("Error", "An Error!", "error");

    }
}

async function DeleteCategory(id) {
    const response = await fetch("DeleteCategory?id=" + id + "");
    console.log(id);
    if (response.ok) {
        const Json = await response.json();

        if (Json.status) {
            sweetAlert("Success", Json.message, "success");
            loadCategorys();
        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "An Error!", "error");
    }
}


async function AddSubCategory() {
    const subCategory = document.getElementById("subCategory").value;

    const add = {

        SubcategoryName: subCategory
    };

    const addcatJson = JSON.stringify(add);

    const response = await fetch(
            "AddSubCategory",
            {
                method: "POST",
                body: addcatJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");

        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Add Sub Category!", "error");
        ;
    }
}

async function addBrand() {
    const brand = document.getElementById("brand").value;

    const add = {

        brandName: brand
    };

    const BrandJson = JSON.stringify(add);

    const response = await fetch(
            "AddBrand",
            {
                method: "POST",
                body: BrandJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");

        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Brand!", "error");
        ;
    }
}

async function addmetirial() {
    const metirial = document.getElementById("metirials").value;

    const add = {

        MetirialName: metirial
    };

    const metirialJson = JSON.stringify(add);

    const response = await fetch(
            "AddMetirial",
            {
                method: "POST",
                body: metirialJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");

        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Add Metirial!", "error");
        ;
    }
}

async function addColor() {
    const color = document.getElementById("color").value;

    const add = {

        colorName: color
    };

    const ColorJson = JSON.stringify(add);

    const response = await fetch(
            "AddColor",
            {
                method: "POST",
                body: ColorJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");

        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Add Metirial!", "error");
        ;
    }
}

async function addSize() {
    const size = document.getElementById("size").value;

    const add = {

        sizeName: size
    };

    const SizeJson = JSON.stringify(add);

    const response = await fetch(
            "AddSize",
            {
                method: "POST",
                body: SizeJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");

        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Add Metirial!", "error");
        ;
    }
}


async function loadOrders() {
    const response = await fetch("loadOrders");

    if (response.ok) {
        const Json = await response.json();
        console.log(Json);

        let tBody = document.getElementById("tbody1");
        tBody.innerHTML = "";
        console.log(Json);
        Json.forEach(order => {
            let data = ` <tr>
                                <td class="p-2 border">${order.order.id}</td>
                                <td class="p-2 border">${order.product.title}</td>
                                <td class="p-2 border">${order.orderStatus.name}</td>
                                <td class="p-2 border">
                                    
                                <button class="bg-purple-600 text-white px-2 py-1 rounded hover:bg-pink-400 p-3" onclick="openChangeStatusModal(${order.id});">change status</button>
                                </td>
                            </tr>`;
            tBody.innerHTML += data;

        });


    } else {

    }

}

async function loadOrderStatus() {
    const response = await fetch("loadOrderStatus");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const statusSelect = document.getElementById("orderStatus");

        json.forEach(status => {
            let option = document.createElement("option");
            option.innerHTML = status.name;
            option.value = status.id;
            statusSelect.appendChild(option);

        });

    }
}

async function ChangeOrderStatus() {


}


function openChangeStatusModal(orderItemId) {
    document.getElementById('orderItemId').value = orderItemId;
    document.getElementById('changeStatusModal').classList.remove('hidden');





}

function closeChangeStatusModal() {
    document.getElementById('changeStatusModal').classList.add('hidden');
}

async function updateOrderStatus() {
    const id = document.getElementById('orderItemId').value;
    const statusSelect = document.getElementById('orderStatus').value;

    if (!statusSelect) {

        sweetAlert("Error", "Please select a status!", "error");
        return;
    }


   


    const add = {
        itemID : id,
        status: statusSelect
    };

    const StatusJson = JSON.stringify(add);

    const response = await fetch(
            "LoadOrder",
            {
                method: "POST",
                body: StatusJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            sweetAlert("Success", Json.message, "success");
document.getElementById('changeStatusModal').classList.add('hidden');
            loadOrders();
        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Cant Add Change Status!", "error");
        ;
    }



}



