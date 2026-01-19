
var SubCategoryList;
var BrandList;
async function loadProduct() {
    const response = await fetch("LoadProductData");

    if (response.ok) {//success
        const Json = await response.json();
        if (Json.status) {//if true
            console.log(Json);
            SubCategoryList = Json.subCategoryList;
            BrandList = Json.brandList;

            loadSelect("category", Json.categoryList, "name");

            loadSelect("subCategory", Json.subCategoryList, "name");
            loadSelect("Brand", Json.brandList, "name");
            loadSelect("metirial", Json.metirialList, "name");
            loadSelect("size", Json.sizeList, "value");
            loadSelect("color", Json.colorList, "value");


        } else { // when status false

            sweetAlert("Error", "Unable to get Product data! Please Try Again Later", "info");

        }


    } else {
        sweetAlert("Error", "Unable to get Product data! Please Try Again Later", "info");
    }
}

function loadSelect(selectId, list, property) {
    const Select = document.getElementById(selectId);

    list.forEach(item => {
        const Option = document.createElement("option");
        Option.value = item.id;
        Option.innerHTML = item[property];
        Select.appendChild(Option);
    });
}

//function loadSubCategory() {
//
//    const categoryId = document.getElementById("category").value;
//    const subCategorySelect = document.getElementById("subCategory");
//
//    subCategorySelect.length = 1;
//
//    SubCategoryList.forEach(item => {
//        if (item.category.id == categoryId) {
//            const Option = document.createElement("option");
//            Option.value = item.id;
//            Option.innerHTML = item.name;
//            subCategorySelect.appendChild(Option);
//        }
//
//    });
//
//}
//
//function loadBrands() {
//    const subCategoryId = document.getElementById("subCategory").value;
//    const Brand = document.getElementById("Brand");
//    Brand.length = 1;
//
//    BrandList.forEach(item => {
//        if (item.subCategory.id == subCategoryId) {
//            const Option = document.createElement("option");
//            Option.value = item.id;
//            Option.innerHTML = item.name;
//            Brand.appendChild(Option);
//        }
//
//    });
//
//}

async function saveProduct() {
    const Pname = document.getElementById("productName").value;
    const discription = document.getElementById("discription").value;
    const category = document.getElementById("category").value;
    const subcategory = document.getElementById("subCategory").value;
    const brand = document.getElementById("Brand").value;
    const metirial = document.getElementById("metirial").value;
    const price = document.getElementById("price").value;
    const qty = document.getElementById("qty").value;
    const size = document.getElementById("size").value;
    const color = document.getElementById("color").value;

    const img1 = document.getElementById("img1").files[0];
    const img2 = document.getElementById("img2").files[0];
    const img3 = document.getElementById("img3").files[0];


    const form = new FormData();
    form.append("Pname", Pname);
    form.append("discription", discription);
    form.append("category", category);
    form.append("subcategory", subcategory);
    form.append("brand", brand);
    form.append("metirial", metirial);
    form.append("price", price);
    form.append("qty", qty);
    form.append("size", size);
    form.append("color", color);

    form.append("img1", img1);
    form.append("img2", img2);
    form.append("img3", img3);

    console.log(brand);


    //console.log(Pname,discription,category,subcategory,brand,metirial,price,qty,size,color);

    const response = await fetch(
            " SaveProduct",
            {
                method: "POST",
                body: form
            }
    );

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            
            window.location.reload();
            sweetAlert("Crongradulations!",Json.message, "success");
        } else {
            if (Json.message === "User Not Found! Please Singn Frist") {
                window.location = "Sign-In.html";
            } else {
                sweetAlert("Error", Json.message, "info");
            }
        }
    } else {

    }
}


