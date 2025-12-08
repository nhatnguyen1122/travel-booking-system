document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get("orderId");
    //
    // if (!orderId) {
    //     showError("Không tìm thấy orderId. Vui lòng đặt tour trước!");
    //     return;
    // }

    // Hiển thị loading
    const flightList = document.getElementById("flight-list");
    flightList.innerHTML = `<div class="loading">Đang tải danh sách chuyến bay...</div>`;

    fetchFlights(orderId);
});

function fetchFlights(orderId) {
    fetch("/flight/getAll")
        .then(response => response.json())
        .then(result => {
            if (result.code === 1000 && result.data) {
                renderFlights(result.data, orderId);
            } else {
                showError("Lỗi tải danh sách chuyến bay!");
            }
        })
        .catch(() => showError("Không thể tải danh sách chuyến bay!"));
}

function renderFlights(flights, orderId) {
    const flightList = document.getElementById("flight-list");
    flightList.innerHTML = "";

    if (flights.length === 0) {
        showError("Không có chuyến bay nào!");
        return;
    }

    flights.forEach(flight => {
        const flightItem = document.createElement("div");
        flightItem.classList.add("flight-item");

        flightItem.innerHTML = `
            <div class="airline-name">${flight.airlineName}</div>
            <div class="ticket-class">${flight.ticketClass}</div>
            <div class="price">${flight.price.toLocaleString()} VND</div>
            <div class="check-in-date">Ngày đi: ${new Date(flight.checkInDate).toLocaleDateString()}</div>
            <div class="check-out-date">Ngày về: ${new Date(flight.checkOutDate).toLocaleDateString()}</div>
            <div class="seat-available">Số ghế còn lại: ${flight.seatAvailable}</div>
            <button class="choose-flight" data-flight-id="${flight.id}" data-order-id="${orderId}">Chọn chuyến bay</button>
        `;

        flightList.appendChild(flightItem);
    });

    // Xử lý khi người dùng chọn chuyến bay
    document.querySelectorAll(".choose-flight").forEach(button => {
        button.addEventListener("click", function () {
            const flightId = this.getAttribute("data-flight-id");
            chooseFlight(orderId, flightId);
        });
    });
}

function chooseFlight(orderId, flightId) {

    if (!orderId) {
        showError("Không tìm thấy orderId. Vui lòng đặt tour trước!");
        return;
    }
    fetch(`/order/chooseFlight/${orderId}/${flightId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => response.json())
        .then(result => {
            if (result.message === "success") {
                alert("Chọn chuyến bay thành công!");
                Promise.all([
                    fetch(`/api/v1/email/${orderId}/announce`, { method: "POST" }),
                    new Promise(resolve => setTimeout(resolve, 500))
                ]).then(() => {
                    window.location.href = `/plan-trip`;
                });
            } else {
                alert(result.message || "Chọn chuyến bay thất bại!");
            }
        })
        .catch(() => alert("Lỗi khi chọn chuyến bay!"));
}

function showError(message) {
    const flightList = document.getElementById("flight-list");
    flightList.innerHTML = `<p class="error-message">${message}</p>`;
}
