async function loadData() {
    const searchParams = new URLSearchParams(window.location.search);
    if (searchParams.has("id")) {
        const productId = searchParams.get("id");

        const response = await fetch("LoadSingleproduct?id=" + productId + "");

        if (response.ok) {
            const Json = await response.json();
            if (Json.status) {
                console.log(Json);
                document.getElementById("img1").src = "Product-Image\\" + Json.product.id + "\\image1.png";
                document.getElementById("img2").src = "Product-Image\\" + Json.product.id + "\\image2.png";
                document.getElementById("img3").src = "Product-Image\\" + Json.product.id + "\\image3.png";
                document.getElementById("main-image").src = "Product-Image\\" + Json.product.id + "\\image1.png";


                document.getElementById("Product-title").innerHTML = Json.product.title;
                document.getElementById("publishedOn").innerHTML =  Json.product.registerdAt;
                document.getElementById("product-price").innerHTML = "Rs:" + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(Json.product.price);
                document.getElementById("Product-discription").innerHTML = Json.product.discription;
                document.getElementById("product-size").innerHTML = Json.product.size.value;
                document.getElementById("type").innerHTML = Json.product.category.name;
                document.getElementById("sub-category").innerHTML = Json.product.subCategory.name;
                document.getElementById("brand").innerHTML = Json.product.brand.name;
                document.getElementById("metirial").innerHTML = Json.product.metirial.name;
                document.getElementById("Seller").innerHTML = Json.product.user.fristName + " " + Json.product.user.lastName;

                //product-color

                document.getElementById("product-color").style.backgroundColor = Json.product.color.value;

                const addtoCartMin = document.getElementById("addtoCart");
                addtoCartMin.addEventListener(
                        "click", (e) => {

                    let qty = document.getElementById("quantity");
                    let quantity = parseInt(qty.textContent);
                    addToCart(Json.product.id, quantity);
                    e.preventDefault();
                }
                );

                let similerProductMain = document.getElementById("smiler-product-main");
                similerProductMain.innerHTML = "";
                Json.productList.forEach(item => {
                    let similerProductHtmldisign = ` <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">
                        <div class="relative">
                           <a href="${'Single-Product-View.html?id=' + item.id}"> <img src="${"Product-Image\\" + item.id + "\\image1.png"}"
                                 alt="Slim Fit Jeans" class="w-full h-64 object-cover"></a>
                            <button class="absolute top-2 right-2 text-gray-400 hover:text-red-500">
                                <i class="far fa-heart text-xl"></i>
                            </button>
                        </div>
                        <div class="p-4">
                            <h3 class="font-medium text-gray-900">${item.title}</h3>
                           
                            <div class="mt-2 flex justify-between items-center">
                                <span class="text-gray-900 font-medium">${"Rs:" + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price)}</span>
                                <button class="text-indigo-600 hover:text-indigo-800 text-sm font-medium" onclick="addToCart(${item.id}, 1);">Add to cart</button>
                            </div>
                        </div>
                    </div>`;

                    similerProductMain.innerHTML += similerProductHtmldisign;

                });




            } else {
                console.log(Json.message);
            }
        } else {
            console.log(Json.message);
        }
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
