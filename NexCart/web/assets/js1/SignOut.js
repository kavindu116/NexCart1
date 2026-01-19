async function SignOut(){
    const response = await fetch("SignOut");
    if(response.ok){
        const json = await response.json();
        if(json.status){
            window.location = "Sign-In.html";
        }else{
            window.location.reload();
        }
    }else{
        console.log("Logout Faild!.");
    }
}


