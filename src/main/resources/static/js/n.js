document.getElementById('payment-form').addEventListener('submit', function(event) {
    event.preventDefault();

    const subtotal = 129397213;
    const discount = parseInt(document.getElementById('discount').value) || 0;
    const customerPay = parseInt(document.getElementById('customer-pay').value) || 0;

    const total = subtotal - discount;
    const remaining = total - customerPay;

    document.getElementById('total').textContent = total.toLocaleString('vi-VN') + " VNĐ";
    document.getElementById('remaining').textContent = remaining.toLocaleString('vi-VN') + " VNĐ";
});
