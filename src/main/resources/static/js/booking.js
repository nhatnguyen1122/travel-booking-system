document.getElementById("booking-form").addEventListener("submit", function(event) {
    event.preventDefault(); // Ngăn form gửi request mặc định

    let palaceName = document.getElementById("palaceName").value;
    let numberOfPeople = document.getElementById("numberOfPeople").value;
    let checkinTime = document.getElementById("checkinTime").value;
    let checkoutTime = document.getElementById("checkoutTime").value;

    let userId = localStorage.getItem("userId") || 1; // Lấy userId từ localStorage

    let orderData = {
        destination: palaceName,
        numberOfPeople: parseInt(numberOfPeople),
        checkInDate: checkinTime,
        checkOutDate: checkoutTime
    };

    fetch(`/order/create/${userId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(orderData)
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 1000) {
                window.location.href = `/hotel?orderId=${result.data.id}`;
            } else {
                // Hiển thị thông báo lỗi trong modal
                showErrorModal(result.message || "Booking failed!");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            // Hiển thị thông báo lỗi trong modal
            showErrorModal("An error occurred while booking!");
        });
});

// Hàm hiển thị modal thông báo lỗi
function showErrorModal(message) {
    const modal = document.getElementById("error-modal");
    const errorMessage = document.getElementById("error-message");

    errorMessage.innerText = message; // Đặt nội dung thông báo
    modal.style.display = "block"; // Hiển thị modal

    // Đóng modal khi nhấn nút đóng
    const closeBtn = document.querySelector(".close");
    closeBtn.onclick = function() {
        modal.style.display = "none";
    };

    // Đóng modal khi nhấn bên ngoài modal
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };
}