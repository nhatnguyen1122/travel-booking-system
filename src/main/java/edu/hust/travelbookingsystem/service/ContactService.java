package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Contact;
import edu.hust.travelbookingsystem.model.request.ContactDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;

public interface ContactService {
    Contact createContact(ContactDTO contactDTO);

    PageResponse<?> getAllContacts(int pageNo, int pageSize);

    PageResponse<?> getUnreadContacts(int pageNo, int pageSize);

    Contact markAsRead(Long contactId);

    void deleteContact(Long contactId);

    long getUnreadCount();
}
