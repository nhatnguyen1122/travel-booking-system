'use strict';

(function () {
  // Guard: tránh ảnh hưởng trang khác nếu nhúng nhầm
  if (!document.body || !document.body.classList.contains('hust-contact-page')) return;

  document.addEventListener('DOMContentLoaded', function () {
    initUserMenu();
  });

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
})();
