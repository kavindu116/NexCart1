async function loadData() {

    const response = await fetch("Loaddata");

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            console.log(Json);
            document.getElementById("all-item-count").innerHTML = Json.allProductCount;
            loadOptions("brand", Json.brandList, "name");
            loadOptions("metirial", Json.metirialList, "name");
            loadOptions("subCategory", Json.subCategoryList, "name");
            loadOptions("category", Json.categoryList, "name");
            loadOptions("size", Json.sizeList, "value");
            loadOptions("color", Json.colorList, "value");
            updateProductView(Json);

        } else {

        }

    } else {

    }

}

function loadOptions(prefix, dataList, property) {
    let option = document.getElementById(prefix + "-options");
    let li = document.getElementById(prefix + "-li");
    option.innerHTML = "";
    dataList.forEach(item => {
        let liClone = li.cloneNode(true);
        if (prefix == "color") {
            liClone.style.borderColor = "black";
            liClone.querySelector("#" + prefix + "-a").style.backgroundColor = item[property];
        } else {
            liClone.querySelector("#" + prefix + "-a").innerHTML = item[property];
        }
        option.appendChild(liClone);
    });

    const all_list = document.querySelectorAll("#" + prefix + "-options li");

    all_list.forEach(list => {
        list.addEventListener("click", function () {

            all_list.forEach(y => {
                y.classList.remove("chosen");
            });
            this.classList.add("chosen");

        });
    });

}



async function ApplyFiters(FristResult) {

    const category = document.getElementById("category-options")
            .querySelector(".chosen")?.querySelector('label[for="category"]').innerHTML;

    const Subcategory = document.getElementById("subCategory-options")
            .querySelector(".chosen")?.querySelector('label[for="subCategory"]').innerHTML;

    const brand = document.getElementById("brand-options")
            .querySelector(".chosen")?.querySelector('label[for="brand"]').innerHTML;

    const metirial = document.getElementById("metirial-options")
            .querySelector(".chosen")?.querySelector('label[for="metirial"]').innerHTML;

    const size = document.getElementById("size-options")
            .querySelector(".chosen")?.querySelector('label[for="size"]').innerHTML;

    const color = document.getElementById("color-options")
            .querySelector(".chosen")?.querySelector("div").style.backgroundColor;

    const Max_price = document.getElementById("maxprice").innerHTML;
    const min_price = "0";

    const sort_value = document.getElementById("st-sort").value;


    const data = {
        FristResult: FristResult,
        category: category,
        Subcategory: Subcategory,
        brand: brand,
        metirial: metirial,
        size: size,
        color: color,
        Max_price: Max_price,
        min_price: min_price,
        sort_value: sort_value
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch("Search_product",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: dataJson
            });

    if (response.ok) {
        const Json = await response.json();

        if (Json.status) {
            console.log(Json);
            updateProductView(Json);
        } else {

        }
    } else {

    }

    console.log(Max_price);
    console.log(sort_value);
    console.log(min_price);

    console.log(category);
    console.log(Subcategory);
    console.log(brand);
    console.log(metirial);
    console.log(size);
    console.log(color);
}

let st_pegination_button = document.getElementById("st-pagination-button");
let current_page = "0";

function updateProductView(Json) {
    let productCart = document.getElementById("productCart");
    productCart.innerHTML = "";

    Json.productList.forEach(product => {



        let  product_container = `<!-- Product Card 1 -->
                            <div class="product-card bg-white rounded-lg overflow-hidden shadow-sm border border-gray-100">
                                <div class="relative">
                                    <a href="${'Single-Product-View.html?id=' + product.id}"><img src="${"Product-Image\\" + product.id + "\\image1.png"}" alt="Product"
                                         class="w-full h-64 object-cover"></a>
                                    <div class="absolute top-2 right-2">
                                        <button
                                            class="w-8 h-8 bg-white rounded-full flex items-center justify-center text-gray-500 hover:text-red-500 shadow-md">
                                            <i class="far fa-heart"></i>
                                        </button>
                                    </div>
                                    
                                </div>
                                <div class="p-4">
                                    <div class="flex justify-between items-start mb-1">
                                        <h3 class="font-medium text-gray-900">${product.title}</h3>

                                    </div>


                                    <button
                                        class="w-full bg-pink -600 text-white py-2 px-4 rounded hover:bg-purple-700 transition duration-300 flex items-center justify-center" onclick="addToCart(${product.id}, 1);">
                                        <i class="fas fa-shopping-cart mr-2"></i> Add to Cart
                                    </button>
                                </div>
                            </div>`;

        productCart.innerHTML += product_container;

    });



    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";
    let all_product_count = Json.allProductCount;
    document.getElementById("all-item-count").innerHTML = all_product_count;
    let product_per_page = 8; //all product count / product per page
    let pages = Math.ceil(all_product_count / product_per_page);//round upper integer

//previous Button
    if (current_page !== 0) {
        let st_paginationButton_prve_clone = st_pegination_button.cloneNode(true);
        st_paginationButton_prve_clone.innerHTML = "Prev";
        st_paginationButton_prve_clone.addEventListener(
                "click", (e) => {
            current_page--;
            ApplyFiters(current_page * product_per_page);
        });

        st_pagination_container.appendChild(st_paginationButton_prve_clone);
    }




    //pagination button
    for (let i = 0; i < pages; i++) {
        let st_paginationButtonClone = st_pegination_button.cloneNode(true);
        st_paginationButtonClone.innerHTML = i + 1;
        st_paginationButtonClone.addEventListener(
                "click", (e) => {
            current_page = i;
            ApplyFiters(i * product_per_page);
            e.preventDefault();
        });

        if (i === Number(current_page)) {
            st_paginationButtonClone.className = "btn btn-md btn-info gap-2 fw-bold rounded";
        } else {
            st_paginationButtonClone.className = "btn btn-md btn-outline-info gap-2  fw-bold rounded";
        }

        st_pagination_container.appendChild(st_paginationButtonClone);


    }

    //Next Button
    if (current_page !== (pages - 1)) {
        let st_paginationButton_next_clone = st_pegination_button.cloneNode(true);
        st_paginationButton_next_clone.innerHTML = "Next";
        st_paginationButton_next_clone.addEventListener(
                "click", (e) => {
            current_page++;
            ApplyFiters(current_page * product_per_page);
        });

        st_pagination_container.appendChild(st_paginationButton_next_clone);
    }



}

async function addToCart(productID, qty) {
    console.log(productID + " " + qty);
    const response = await fetch("AddToCart?prId=" + productID + "&qty=" + qty);
    if (response.ok) {
        const Json = await response.json();

        if (Json.status) {
            sweetAlert("Success", Json.message, "success");
        } else {
            sweetAlert("Error", Json.message, "info");
        }
    } else {

    }
}

async function Search(){
    let searchText = document.getElementById("SearchText").value;
    
     const search = {
        searchText: searchText
       
    };

    const SearchJson = JSON.stringify(search);

    const response = await fetch(
            "Search",
            {
                method: "POST",
                body:SearchJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    
    if (response.ok) {
        const Json = await response.json();

        if (Json.status) {
            console.log(Json);
            updateProductView(Json);
           // sweetAlert("Success", Json.message, "success");
        } else {
           // sweetAlert("Error", Json.message, "info");
        }
    } else {

    }
}


