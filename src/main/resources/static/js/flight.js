'use strict';

(function () {
  // Guard: tránh ảnh hưởng trang khác nếu nhúng nhầm
  if (!document.body || !document.body.classList.contains('hust-flight-page')) return;

  document.addEventListener('DOMContentLoaded', function () {
    initUserMenu();
    initFlightDateStore();
    initFlightsPage();
  });

  // ===== USER MENU =====
  function initUserMenu() {
    const userIcon = document.getElementById('user-icon');
    const menu = document.getElementById('user-menu');
    if (!userIcon || !menu) return;

    userIcon.addEventListener('click', function (event) {
      event.preventDefault();
      menu.style.display = (menu.style.display === 'flex') ? 'none' : 'flex';
    });

    document.addEventListener('click', function (event) {
      if (!userIcon.contains(event.target) && !menu.contains(event.target)) {
        menu.style.display = 'none';
      }
    });
  }

  // ===== STORE FLIGHT DATE (nếu có input flightDate) =====
  function initFlightDateStore() {
    const flightDateInput = document.querySelector('input[name="flightDate"]');
    if (!flightDateInput) return;

    flightDateInput.addEventListener('change', function () {
      localStorage.setItem('flightDate', this.value);
    });
  }

  // ===== ORIGINAL FLIGHT LOGIC =====
  function initFlightsPage() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');

    const flightList = document.getElementById('flight-list');
    if (!flightList) return;

    // loading
    flightList.innerHTML = '<div class="loading">Đang tải danh sách chuyến bay...</div>';

    fetchFlights(orderId);
  }

  function fetchFlights(orderId) {
    fetch('/flight/getAll')
      .then(response => response.json())
      .then(result => {
        if (result.code === 1000 && result.data) {
          renderFlights(result.data, orderId);
        } else {
          showError('Lỗi tải danh sách chuyến bay!');
        }
      })
      .catch(() => showError('Không thể tải danh sách chuyến bay!'));
  }

  function renderFlights(flights, orderId) {
    const flightList = document.getElementById('flight-list');
    if (!flightList) return;

    flightList.innerHTML = '';

    if (!flights || flights.length === 0) {
      showError('Không có chuyến bay nào!');
      return;
    }

    flights.forEach(flight => {
      const flightItem = document.createElement('div');
      flightItem.classList.add('flight-item');

      flightItem.innerHTML = `
        <div class="airline-name">${flight.airlineName}</div>
        <div class="ticket-class">${flight.ticketClass}</div>
        <div class="price">${Number(flight.price || 0).toLocaleString()} VND</div>
        <div class="check-in-date">Ngày đi: ${formatDate(flight.checkInDate)}</div>
        <div class="check-out-date">Ngày về: ${formatDate(flight.checkOutDate)}</div>
        <div class="seat-available">Số ghế còn lại: ${flight.seatAvailable}</div>
        <button class="choose-flight" data-flight-id="${flight.id}" data-order-id="${orderId || ''}">
          Chọn chuyến bay
        </button>
      `;

      flightList.appendChild(flightItem);
    });

    document.querySelectorAll('.choose-flight').forEach(button => {
      button.addEventListener('click', function () {
        const flightId = this.getAttribute('data-flight-id');
        chooseFlight(orderId, flightId);
      });
    });
  }

  function chooseFlight(orderId, flightId) {
    if (!orderId) {
      showError('Không tìm thấy orderId. Vui lòng đặt tour trước!');
      return;
    }

    fetch(`/order/chooseFlight/${orderId}/${flightId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    })
      .then(response => response.json())
      .then(result => {
        if (result.message === 'success') {
          alert('Chọn chuyến bay thành công!');
          Promise.all([
            fetch(`/api/v1/email/${orderId}/announce`, { method: 'POST' }),
            new Promise(resolve => setTimeout(resolve, 500))
          ]).then(() => {
            window.location.href = '/plan-trip';
          });
        } else {
          alert((result && result.message) || 'Chọn chuyến bay thất bại!');
        }
      })
      .catch(() => alert('Lỗi khi chọn chuyến bay!'));
  }

  function showError(message) {
    const flightList = document.getElementById('flight-list');
    if (!flightList) return;
    flightList.innerHTML = `<p class="error-message">${message}</p>`;
  }

  function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const d = new Date(dateStr);
    return isNaN(d.getTime()) ? 'N/A' : d.toLocaleDateString();
  }
})();
