async function verifyAccount() {
    const verificationCode = document.getElementById("vCode").value;

    const verifyCode = {
        verificationCode: verificationCode
    };

    const  verifyCodeJson = JSON.stringify(verifyCode);
    const response = await fetch(
            "VerifyAccount",
            {
                method: "POST",
                body: verifyCodeJson,
                header: {
                    "Content-Type": "application/json"
                }

            }
    );
    if (response.ok) {
        const  Json = await response.json();

        if (Json.status) {
            if (Json.message === "101") {
                window.location = "Sign-in.html";
            } else {
                sweetAlert("Error", "Email Not Found", "error");
            }

            window.location = "index.html";
        } else {
            sweetAlert("Error", Json.message, "error");
        }
    } else {
        sweetAlert("Error", "Verification Faild.Try Again Later", "error");
    }
}


async function ResendCode() {
    const response = await fetch("ResendVerifyCode");
    const button = document.getElementById("button");
    const Json = await response.json();

    if (Json.status) {
        sweetAlert("Success", Json.message, "success");
        let timeLeft = 60;


        const countdown = setInterval(() => {
            timeLeft--;
            button.textContent = `Resend Code In (${timeLeft}s)`;
            button.disabled = true;
            if (timeLeft <= 0) {
                clearInterval(countdown);
                button.disabled = false;
                button.textContent = `Resend Code`;
            }
        }, 1000);


    }

}


