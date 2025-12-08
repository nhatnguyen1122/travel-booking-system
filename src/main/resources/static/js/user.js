document.getElementById("sign-up-button").addEventListener("click", function() {
    document.getElementById("sign-in-form").classList.add("hidden");
    document.getElementById("sign-up-form").classList.remove("hidden");
});

document.getElementById("sign-in-button1").addEventListener("click", function() {
    document.getElementById("sign-in-form").classList.remove("hidden");
    document.getElementById("sign-up-form").classList.add("hidden");
});

// Hàm mở modal lỗi
function showErrorModal(message) {
    document.getElementById("error-message").innerText = message;
    document.getElementById("error-modal").style.display = "flex";
}

// Đóng modal khi nhấn OK hoặc dấu X
document.getElementById("close-modal-button").addEventListener("click", function() {
    document.getElementById("error-modal").style.display = "none";
});
document.querySelector(".close-modal").addEventListener("click", function() {
    document.getElementById("error-modal").style.display = "none";
});

// Xử lý đăng nhập
document.getElementById("sign-in-form").addEventListener("submit", function(event) {
    event.preventDefault();

    let phone = document.getElementById("login-phone").value;
    let password = document.getElementById("login-password").value;

    fetch("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone: phone, password: password })
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 1000) {
                localStorage.setItem("user", JSON.stringify(result.data));
                localStorage.setItem("userId", result.data.id);
                window.location.href = "/home";
            } else if (result.code === 8888) {
                localStorage.setItem("user", JSON.stringify(result.data));
                window.location.href = "/admin_booking";
            } else {
                showErrorModal(result.message || "Đăng nhập thất bại!");
            }
        })
        .catch(() => showErrorModal("Có lỗi xảy ra khi đăng nhập!"));
});

// Xử lý đăng ký
document.getElementById("sign-up-form").addEventListener("submit", function(event) {
    event.preventDefault();

    let name = document.getElementById("signup-name").value;
    let email = document.getElementById("signup-email").value;
    let phone = document.getElementById("signup-phone").value;
    let birthday = document.getElementById("birthday").value;
    let password = document.getElementById("signup-password").value;
    let confirmPassword = document.getElementById("signup-confirm-password").value;

    if (password !== confirmPassword) {
        showErrorModal("Mật khẩu không khớp!");
        return;
    }

    fetch("/user/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fullName: name, email, phone, password, birthday, passwordConfirm: confirmPassword })
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 1000) {
                showErrorModal("Đăng ký thành công!");
            } else {
                showErrorModal(result.message || "Đăng ký thất bại!");
            }
        })
        .catch(() => showErrorModal("Có lỗi xảy ra khi đăng ký!"));
});
