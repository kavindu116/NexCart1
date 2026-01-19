function indexLoad() {
    CheckSessionCart();
    loadProductData();
    loadNewArivals();
    updateCountdown();
    loadFeedback();
}


async function CheckSessionCart() {
    const response = await fetch("CheckSessionCart");

    if (response.ok) {

    } else {
        sweetAlert("OOPS..!", "Somthing went wrong  try again Later", "info");
    }

}

async function loadProductData() {

    const response = await fetch("LoadHomeData");

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            console.log(Json);
            loadProduct(Json);
            
        } else {

        }

    } else {

    }
}

function loadProduct(Json) {



    const Container = document.getElementById("prodduct-container");
    Container.innerHTML = "";
    Json.productList.forEach(item => {

        let products = `<div
                        class="product-card bg-white rounded-lg overflow-hidden shadow-md hover:shadow-xl transition duration-300">
                        <div class="relative">
                           <a href="${'Single-Product-View.html?id=' + item.id}"> <img src="${"Product-Image\\" + item.id + "\\image1.png"}"
                                 alt="Product" class="w-full h-48 object-cover"></a>
                            <div class="absolute top-2 left-2 bg-pink-600 text-white text-xs px-2 py-1 rounded">-30%</div>
                            
                        </div>
                        <div class="p-4">
                            <h3 class="font-medium text-gray-800 mb-1">${item.title}</h3>
                           
                            <div class="flex items-center">
                                <span class="text-pink-600 font-bold">Rs : ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price)}</span>
                               
                               <button  onclick="addToCart(${item.id}, 1);>
                                Add to Cart
                            </button>
                            </div>
                            
                            
                        </div>
                    </div>`;
        Container.innerHTML += products;

    });
}

async function loadNewArivals() {


    const response = await fetch("LoadNewArivalsData");

    if (response.ok) {
        const Json = await response.json();
        if (Json.status) {
            console.log(Json);
            const newProduct = document.getElementById("product-main");
            newProduct.innerHTML = "";

            Json.productList.forEach(item => {

                let newArivals = ` <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">
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

                newProduct.innerHTML += newArivals;

            });
        }

    } else {

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


 // countdown.js

// Set target date/time
const targetDate = new Date("2025-08-10T00:00:00");
targetDate.setMinutes(targetDate.getMinutes() + 60); // 60 minutes from now

function updateCountdown() {
  const now = new Date().getTime();
  const distance = targetDate.getTime() - now;

  if (distance <= 0) {
    document.getElementById("days").innerText = "00";
    document.getElementById("hours").innerText = "00";
    document.getElementById("minutes").innerText = "00";
    document.getElementById("seconds").innerText = "00";
    clearInterval(timerInterval);
    return;
  }

  const days = Math.floor(distance / (1000 * 60 * 60 * 24));
  const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((distance % (1000 * 60)) / 1000);

  document.getElementById("days").innerText = String(days).padStart(2, '0');
  document.getElementById("hours").innerText = String(hours).padStart(2, '0');
  document.getElementById("minutes").innerText = String(minutes).padStart(2, '0');
  document.getElementById("seconds").innerText = String(seconds).padStart(2, '0');
}

const timerInterval = setInterval(updateCountdown, 1000);
updateCountdown(); // Call immediately


async function loadFeedback(){
     const response = await fetch("FeedBack");
     
     if(response.ok){
        const Json =  await response.json();
        console.log(Json);
        
        let feedBackCard = document.getElementById("feedBackCard");
        feedBackCard.innerHTML = "";
        
        Json.feedBacks.forEach(item =>{
           let feedback = `<div class="feedback-card bg-white p-6 rounded-xl shadow-sm">
                    <div class="text-gray-700 mb-4">Rate : <span>${item.rate}</span>%</div>
                    <p class="text-gray-700 mb-4">"${item.message}"</p>
                    <div class="flex items-center">
                        
                        <div>
                            <h4 class="font-medium text-gray-900">${item.name}</h4>
                            <p class="text-sm text-gray-500">Verified Buyer</p>
                        </div>
                    </div>
                </div>`;
           
           feedBackCard.innerHTML += feedback;
        });
        
     }else{
         
     }
}

