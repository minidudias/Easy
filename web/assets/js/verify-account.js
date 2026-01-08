async function verifyAccount() {

    const verification_only_dto = {
        verification: document.getElementById("verification").value
    };

    console.log(verification_only_dto);

    const response = await fetch(
            "Verification",
            {
                method: "POST",
                body: JSON.stringify(verification_only_dto),
                headers:{
                    "Content-Type": "application/json"
                }
            }
    );


    if (response.ok) {
        const jsonResponse = await response.json();
        
        if(jsonResponse.success){
            window.location= "index.html";
        }else{
            document.getElementById("message").innerHTML = jsonResponse.content;
        }        
        
    } else {
        document.getElementById("message").innerHTML = "Please try again!";
    }
}