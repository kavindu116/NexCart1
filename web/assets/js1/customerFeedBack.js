async function customerFeedback(){
    
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const rangeValue = document.getElementById("rangeValue").innerHTML;
    const message = document.getElementById("message").value;
    
    const data = {
        name: name,
        rangeValue : rangeValue,
        email: email,
        message: message
    };

    const feedBackJson = JSON.stringify(data);

    const response = await fetch(
            "FeedBack",
            {
                method: "POST",
                body:feedBackJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    
    
     if(response.ok){//success
        const Json =  await response.json();
       if(Json.status){//if true
           sweetAlert("Success", Json.message, "success");
           window.location.reload();
       }else{ // when status false
          sweetAlert("Error", Json.message, "error"); 
        
       }
    
    
    }else{
        if (response.status === 401) {
            window.location = "Sign-In.html";
        }
    }
    
}

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