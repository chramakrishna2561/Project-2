document.getElementById("submitButton").onclick = function() {
    var name = document.getElementById("name").value;
    var email = document.getElementById("email").value;
    var mobile = document.getElementById("mobile").value;

    var data = {
        name: name,
        email: email,
        mobile: mobile
    };

    // Make AJAX call
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/bin/saveUserData", true); // Use POST method
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            alert("Data saved successfully!");
        } else if (xhr.readyState === 4) {
            alert("Failed to save data.");
        }
    };
    xhr.send(JSON.stringify(data));
};
