'use strict';

(function () {
  // Guard: tránh ảnh hưởng trang khác nếu nhúng nhầm
  if (!document.body || !document.body.classList.contains('hust-contact-page')) return;

  document.addEventListener('DOMContentLoaded', function () {
    initUserMenu();
    initModal();
    initContactForm();
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

  // ===== MODAL =====
  let modalEl, modalMessageEl, modalIconEl, closeXEl, closeBtnEl;

  function initModal() {
    modalEl = document.getElementById('contact-modal');
    modalMessageEl = document.getElementById('modal-message');
    modalIconEl = document.getElementById('modal-icon');
    closeXEl = document.querySelector('#contact-modal .close');
    closeBtnEl = document.querySelector('#contact-modal .modal-button');

    if (!modalEl) return;

    if (closeXEl) closeXEl.addEventListener('click', closeModal);
    if (closeBtnEl) closeBtnEl.addEventListener('click', closeModal);

    modalEl.addEventListener('click', function (e) {
      if (e.target === modalEl) closeModal();
    });
  }

  function showModal(message, isSuccess) {
    if (!modalEl || !modalMessageEl || !modalIconEl) return;

    modalMessageEl.innerText = message || 'Something happened!';

    // Change icon based on success/error
    if (isSuccess) {
      modalIconEl.className = 'fas fa-check-circle';
      modalIconEl.style.color = '#4CAF50';
    } else {
      modalIconEl.className = 'fas fa-exclamation-circle';
      modalIconEl.style.color = '#f44336';
    }

    modalEl.style.display = 'flex';
  }

  function closeModal() {
    if (!modalEl) return;
    modalEl.style.display = 'none';
  }

  // ===== CONTACT FORM =====
  function initContactForm() {
    const form = document.getElementById('contact-form');
    if (!form) return;

    form.addEventListener('submit', function (event) {
      event.preventDefault();

      const fullName = document.getElementById('fullName')?.value || '';
      const email = document.getElementById('email')?.value || '';
      const subject = document.getElementById('subject')?.value || '';
      const message = document.getElementById('message')?.value || '';

      // Basic validation
      if (!fullName.trim() || !email.trim() || !subject.trim() || !message.trim()) {
        showModal('Please fill in all fields!', false);
        return;
      }

      const contactData = {
        fullName: fullName.trim(),
        email: email.trim(),
        subject: subject.trim(),
        message: message.trim()
      };

      // Disable submit button to prevent double submission
      const submitBtn = form.querySelector('button[type="submit"]');
      if (submitBtn) submitBtn.disabled = true;

      fetch('/contact', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(contactData)
      })
        .then(response => response.json())
        .then(result => {
          if (result.code === 1000) {
            showModal(result.message || 'Your message has been sent successfully!', true);
            form.reset(); // Clear the form
          } else {
            showModal(result.message || 'Failed to send message!', false);
          }
        })
        .catch(error => {
          console.error('Error:', error);
          showModal('An error occurred while sending your message!', false);
        })
        .finally(() => {
          // Re-enable submit button
          if (submitBtn) submitBtn.disabled = false;
        });
    });
  }
})();
