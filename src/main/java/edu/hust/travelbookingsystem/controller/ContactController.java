package edu.hust.travelbookingsystem.controller;

import edu.hust.travelbookingsystem.entity.Contact;
import edu.hust.travelbookingsystem.model.request.ContactDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.service.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@Slf4j
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ApiResponse<Contact> createContact(@Valid @RequestBody ContactDTO contactDTO) {
        log.info("POST /contact - Creating new contact");

        ApiResponse<Contact> response = new ApiResponse<>();
        Contact contact = contactService.createContact(contactDTO);
        response.setData(contact);
        response.setMessage("Contact message sent successfully");

        return response;
    }

    @GetMapping("/all")
    public ApiResponse<PageResponse<?>> getAllContacts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("GET /contact/all - Fetching all contacts");

        ApiResponse<PageResponse<?>> response = new ApiResponse<>();
        PageResponse<?> contacts = contactService.getAllContacts(pageNo, pageSize);
        response.setData(contacts);
        response.setMessage("Contacts retrieved successfully");

        return response;
    }

    @GetMapping("/unread")
    public ApiResponse<PageResponse<?>> getUnreadContacts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("GET /contact/unread - Fetching unread contacts");

        ApiResponse<PageResponse<?>> response = new ApiResponse<>();
        PageResponse<?> contacts = contactService.getUnreadContacts(pageNo, pageSize);
        response.setData(contacts);
        response.setMessage("Unread contacts retrieved successfully");

        return response;
    }

    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount() {
        log.info("GET /contact/unread/count - Getting unread count");

        ApiResponse<Long> response = new ApiResponse<>();
        long count = contactService.getUnreadCount();
        response.setData(count);
        response.setMessage("Unread count retrieved successfully");

        return response;
    }

    @PutMapping("/{contactId}/read")
    public ApiResponse<Contact> markAsRead(@PathVariable Long contactId) {
        log.info("PUT /contact/{}/read - Marking contact as read", contactId);

        ApiResponse<Contact> response = new ApiResponse<>();
        Contact contact = contactService.markAsRead(contactId);
        response.setData(contact);
        response.setMessage("Contact marked as read");

        return response;
    }

    @DeleteMapping("/{contactId}")
    public ApiResponse<Void> deleteContact(@PathVariable Long contactId) {
        log.info("DELETE /contact/{} - Deleting contact", contactId);

        ApiResponse<Void> response = new ApiResponse<>();
        contactService.deleteContact(contactId);
        response.setMessage("Contact deleted successfully");

        return response;
    }
}
