function updateDateTime() {
    const now = new Date();

    // Format ngày: Thứ, ngày/tháng/năm
    const days = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
    const dayOfWeek = days[now.getDay()];
    const date = now.getDate().toString().padStart(2, '0');
    const month = (now.getMonth() + 1).toString().padStart(2, '0');
    const year = now.getFullYear();

    // Format giờ: HH giờ MM phút SS giây
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');

    // Hiển thị ngày và giờ
    document.getElementById('currentDate').textContent = `${dayOfWeek}, ${date}/${month}/${year}`;
    document.getElementById('currentTime').textContent = `${hours} giờ ${minutes} phút ${seconds} giây`;
}

// Cập nhật ngày giờ mỗi giây
setInterval(updateDateTime, 1000);
updateDateTime(); // Gọi ngay lập tức để hiển thị ngay
