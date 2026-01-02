'use strict';

(function () {
  if (!document.body || !document.body.classList.contains('hust-admin-contact')) return;

  // ===== TIME =====
  function updateTime() {
    const now = new Date();
    const dateEl = document.getElementById('currentDate');
    const timeEl = document.getElementById('currentTime');
    if (dateEl) dateEl.innerText = now.toLocaleDateString();
    if (timeEl) timeEl.innerText = now.toLocaleTimeString();
  }
  setInterval(updateTime, 1000);
  updateTime();

  // ===== USER MENU =====
  const userIcon = document.getElementById('user-icon');
  const userMenu = document.getElementById('user-menu');
  if (userIcon && userMenu) {
    userIcon.addEventListener('click', function (event) {
      event.preventDefault();
      userMenu.style.display = (userMenu.style.display === 'flex') ? 'none' : 'flex';
    });

    document.addEventListener('click', function (event) {
      if (!userIcon.contains(event.target) && !userMenu.contains(event.target)) {
        userMenu.style.display = 'none';
      }
    });
  }

  // ===== STATE =====
  let currentPage = 0;
  const pageSize = 10;
  let currentFilter = 'all'; // 'all' or 'unread'
  let isSearchMode = false;
  let currentSearchQuery = '';
  let currentContactId = null;

  // ===== MODAL FUNCTIONS =====
  function closeViewModal() {
    const modal = document.getElementById('viewMessageModal');
    if (modal) modal.style.display = 'none';
  }

  function closeConfirmModal() {
    const modal = document.getElementById('confirmModal');
    if (modal) modal.style.display = 'none';
    currentContactId = null;
  }

  function deleteContactFromModal() {
    if (currentContactId) {
      openConfirmDelete(currentContactId);
    }
  }

  function openConfirmDelete(contactId) {
    currentContactId = contactId;
    closeViewModal();
    const modal = document.getElementById('confirmModal');
    if (modal) modal.style.display = 'flex';
  }

  function confirmDelete() {
    if (!currentContactId) return;

    fetch(`/contact/${currentContactId}`, {
      method: 'DELETE'
    })
      .then(res => res.json())
      .then(data => {
        if (data.code === 1000) {
          alert('Contact deleted successfully');
          closeConfirmModal();
          loadContactsByMode(currentPage);
          updateCounts();
        } else {
          alert('Failed to delete contact: ' + (data.message || 'Unknown error'));
        }
      })
      .catch(error => {
        console.error('Error deleting contact:', error);
        alert('An error occurred while deleting the contact');
      });
  }

  // Expose functions to global scope for onclick handlers
  window.closeViewModal = closeViewModal;
  window.closeConfirmModal = closeConfirmModal;
  window.deleteContactFromModal = deleteContactFromModal;
  window.confirmDelete = confirmDelete;

  // Close modals when clicking outside
  window.addEventListener('click', function (event) {
    const viewModal = document.getElementById('viewMessageModal');
    const confirmModal = document.getElementById('confirmModal');
    if (viewModal && event.target === viewModal) closeViewModal();
    if (confirmModal && event.target === confirmModal) closeConfirmModal();
  });

  // ===== FILTER FUNCTIONS =====
  function filterContacts(filter) {
    currentFilter = filter;
    isSearchMode = false;
    currentSearchQuery = '';
    currentPage = 0;
    loadContactsByMode(currentPage);

    // Clear search box
    const searchInput = document.getElementById('search');
    if (searchInput) searchInput.value = '';
  }

  function searchContacts() {
    const searchInput = document.getElementById('search');
    if (!searchInput) return;

    const query = searchInput.value.trim();
    if (!query) {
      alert('Please enter a search keyword');
      return;
    }

    isSearchMode = true;
    currentSearchQuery = query;
    currentPage = 0;
    loadContactsByMode(currentPage);
  }

  window.filterContacts = filterContacts;
  window.searchContacts = searchContacts;

  // ===== TABLE HELPERS =====
  function showNoRow(message) {
    const tbody = document.querySelector('#contactTable tbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:18px;">${message}</td></tr>`;
  }

  function renderContactTable(contacts) {
    const tableBody = document.querySelector('#contactTable tbody');
    if (!tableBody) return;

    if (!Array.isArray(contacts) || contacts.length === 0) {
      showNoRow(isSearchMode ? 'No matching contacts found' : 'No contacts found');
      return;
    }

    tableBody.innerHTML = '';
    contacts.forEach(contact => {
      const row = document.createElement('tr');
      const date = new Date(contact.createdAt).toLocaleDateString();
      const statusClass = contact.isRead ? 'status-read' : 'status-unread';
      const statusText = contact.isRead ? 'Read' : 'Unread';

      row.innerHTML = `
        <td>${contact.id}</td>
        <td>${contact.fullName || ''}</td>
        <td>${contact.email || ''}</td>
        <td>${(contact.subject || '').substring(0, 50)}${contact.subject.length > 50 ? '...' : ''}</td>
        <td>${date}</td>
        <td><span class="status-badge ${statusClass}">${statusText}</span></td>
        <td>
          <div class="action-buttons">
            <button class="view-btn" onclick="viewContact(${contact.id})">View</button>
            ${!contact.isRead ? `<button class="mark-read-btn" onclick="markAsRead(${contact.id})">Mark Read</button>` : ''}
            <button class="delete-btn" onclick="openConfirmDelete(${contact.id})">Delete</button>
          </div>
        </td>
      `;
      tableBody.appendChild(row);
    });
  }

  function renderPagination(totalPages) {
    const paginationContainer = document.querySelector('.pagination');
    if (!paginationContainer) return;

    paginationContainer.innerHTML = '';

    let pages = Number(totalPages ?? 0);
    if (!pages || pages < 1) pages = 1;

    if (currentPage > pages - 1) currentPage = pages - 1;
    if (currentPage < 0) currentPage = 0;

    const prevBtn = document.createElement('button');
    prevBtn.textContent = 'Prev';
    prevBtn.disabled = currentPage === 0;
    prevBtn.onclick = function () {
      if (currentPage > 0) {
        currentPage--;
        loadContactsByMode(currentPage);
      }
    };
    paginationContainer.appendChild(prevBtn);

    for (let i = 0; i < pages; i++) {
      const btn = document.createElement('button');
      btn.textContent = i + 1;
      btn.classList.toggle('active', i === currentPage);
      btn.onclick = function () {
        currentPage = i;
        loadContactsByMode(currentPage);
      };
      paginationContainer.appendChild(btn);
    }

    const nextBtn = document.createElement('button');
    nextBtn.textContent = 'Next';
    nextBtn.disabled = currentPage >= pages - 1;
    nextBtn.onclick = function () {
      if (currentPage < pages - 1) {
        currentPage++;
        loadContactsByMode(currentPage);
      }
    };
    paginationContainer.appendChild(nextBtn);
  }

  // ===== API FUNCTIONS =====
  function loadAllContacts(pageNo) {
    return fetch(`/contact/all?pageNo=${pageNo}&pageSize=${pageSize}`)
      .then(res => res.json())
      .then(data => {
        const items = data?.data?.items ?? [];
        const totalPages = Number(data?.data?.totalPages ?? 0);
        renderContactTable(items);
        renderPagination(totalPages);
      })
      .catch(error => {
        console.error('Error loading contacts:', error);
        showNoRow('Error loading contacts');
      });
  }

  function loadUnreadContacts(pageNo) {
    return fetch(`/contact/unread?pageNo=${pageNo}&pageSize=${pageSize}`)
      .then(res => res.json())
      .then(data => {
        const items = data?.data?.items ?? [];
        const totalPages = Number(data?.data?.totalPages ?? 0);
        renderContactTable(items);
        renderPagination(totalPages);
      })
      .catch(error => {
        console.error('Error loading unread contacts:', error);
        showNoRow('Error loading unread contacts');
      });
  }

  function searchContactsAPI(pageNo, query) {
    // For now, we'll filter client-side since there's no search endpoint
    // In production, you'd want a dedicated search endpoint
    loadAllContacts(pageNo).then(() => {
      const allRows = document.querySelectorAll('#contactTable tbody tr');
      const lowerQuery = query.toLowerCase();
      allRows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(lowerQuery) ? '' : 'none';
      });
    });
  }

  function loadContactsByMode(pageNo) {
    if (isSearchMode) {
      searchContactsAPI(pageNo, currentSearchQuery);
    } else if (currentFilter === 'unread') {
      loadUnreadContacts(pageNo);
    } else {
      loadAllContacts(pageNo);
    }
  }

  function updateCounts() {
    // Update all contacts count
    fetch('/contact/all?pageNo=0&pageSize=1')
      .then(res => res.json())
      .then(data => {
        const allCountEl = document.getElementById('allCount');
        if (allCountEl && data?.data?.totalPages !== undefined) {
          // Approximate total from totalPages * pageSize (not exact but close enough)
          allCountEl.textContent = data.data.totalPages > 0 ? data.data.totalPages * pageSize : 0;
        }
      })
      .catch(err => console.error('Error updating all count:', err));

    // Update unread count
    fetch('/contact/unread/count')
      .then(res => res.json())
      .then(data => {
        const unreadCountEl = document.getElementById('unreadCount');
        if (unreadCountEl && data?.data !== undefined) {
          unreadCountEl.textContent = data.data;
        }
      })
      .catch(err => console.error('Error updating unread count:', err));
  }

  function viewContact(contactId) {
    fetch(`/contact/all?pageNo=0&pageSize=1000`)
      .then(res => res.json())
      .then(data => {
        const contacts = data?.data?.items ?? [];
        const contact = contacts.find(c => c.id === contactId);

        if (!contact) {
          alert('Contact not found');
          return;
        }

        // Populate modal
        document.getElementById('modalFullName').textContent = contact.fullName || '';
        document.getElementById('modalEmail').textContent = contact.email || '';
        document.getElementById('modalSubject').textContent = contact.subject || '';
        document.getElementById('modalDate').textContent = new Date(contact.createdAt).toLocaleString();
        document.getElementById('modalStatus').innerHTML =
          `<span class="status-badge ${contact.isRead ? 'status-read' : 'status-unread'}">${contact.isRead ? 'Read' : 'Unread'}</span>`;
        document.getElementById('modalMessage').textContent = contact.message || '';

        currentContactId = contactId;

        // Show modal
        const modal = document.getElementById('viewMessageModal');
        if (modal) modal.style.display = 'flex';

        // Mark as read if unread
        if (!contact.isRead) {
          markAsRead(contactId, false);
        }
      })
      .catch(error => {
        console.error('Error viewing contact:', error);
        alert('Error loading contact details');
      });
  }

  function markAsRead(contactId, reload = true) {
    fetch(`/contact/${contactId}/read`, {
      method: 'PUT'
    })
      .then(res => res.json())
      .then(data => {
        if (data.code === 1000) {
          if (reload) {
            loadContactsByMode(currentPage);
          }
          updateCounts();
        } else {
          if (reload) {
            alert('Failed to mark as read: ' + (data.message || 'Unknown error'));
          }
        }
      })
      .catch(error => {
        console.error('Error marking as read:', error);
        if (reload) {
          alert('An error occurred');
        }
      });
  }

  window.viewContact = viewContact;
  window.markAsRead = markAsRead;
  window.openConfirmDelete = openConfirmDelete;

  // ===== INIT =====
  document.addEventListener('DOMContentLoaded', function () {
    loadContactsByMode(currentPage);
    updateCounts();
  });

  // Initial load (in case DOM is already loaded)
  if (document.readyState === 'loading') {
    // Already have DOMContentLoaded listener
  } else {
    loadContactsByMode(currentPage);
    updateCounts();
  }
})();
