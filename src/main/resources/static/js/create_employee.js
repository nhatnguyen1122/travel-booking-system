'use strict';

(function () {
  // Guard: tránh ảnh hưởng trang khác nếu nhúng nhầm
  if (!document.body || !document.body.classList.contains('hust-create-employee-page')) return;

  document.addEventListener('DOMContentLoaded', function () {
    initUserMenu();
    initCreateStaffForm();
  });

  // --- USER MENU LOGIC ---
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

  // --- FORM SUBMIT LOGIC ---
  function initCreateStaffForm() {
    const form = document.getElementById('createStaffForm');
    if (!form) return;

    form.addEventListener('submit', function (event) {
      event.preventDefault();

      const formData = new FormData(form);

      if (formData.get('password') !== formData.get('confirmPassword')) {
        alert('Passwords do not match!');
        return;
      }

      const payload = {
        name: formData.get('name'),
        email: formData.get('email'),
        telephone: formData.get('phone'),
        password: formData.get('password'),
        confirmPassword: formData.get('confirmPassword'),
        roles: 'admin'
      };

      fetch('api/auth/createStaff', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
        .then(res => res.json())
        .then(data => {
          if (data && data.success) {
            alert('Employee created successfully!');
            window.location.href = 'admin_account';
          } else {
            alert('Error: ' + (data && data.message ? data.message : 'Unknown error'));
          }
        })
        .catch(err => {
          console.error('Error:', err);
          alert('An error occurred while creating the employee.');
        });
    });
  }
})();
