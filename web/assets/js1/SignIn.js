async function SignIn(){
    const email = document.getElementById("email").value;
     const password = document.getElementById("password").value;
   
    const SignIn = {
        
        email: email,
        password: password
    };

    const SignInJson = JSON.stringify(SignIn);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body:SignInJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    
    if(response.ok){
        const Json = await response.json();
        if(Json.status){
            if(Json.message === "101"){
                window.location = "Verify-Account.html";
            }else if(Json.message === "admin"){
             window.location = "admin.html"; 
            }else{
              window.location = "index.html"; 
            }
        }else{
           sweetAlert("Error", Json.message, "error");
        }
    }else{
        sweetAlert("Error", "Login Faild Try Again Later!", "error"); ; 
    }
}

