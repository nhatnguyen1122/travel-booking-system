package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.Contact;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.ContactDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.repository.ContactRepository;
import edu.hust.travelbookingsystem.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ContactServiceImplementation implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Contact createContact(ContactDTO contactDTO) {
        log.info("Creating new contact from: {}", contactDTO.getEmail());

        Contact contact = new Contact();
        contact.setFullName(contactDTO.getFullName());
        contact.setEmail(contactDTO.getEmail());
        contact.setSubject(contactDTO.getSubject());
        contact.setMessage(contactDTO.getMessage());
        contact.setIsRead(false);

        Contact savedContact = contactRepository.save(contact);
        log.info("Contact created successfully with ID: {}", savedContact.getId());

        // Send confirmation email to user
        try {
            emailService.sendContactConfirmationEmail(
                savedContact.getEmail(),
                savedContact.getFullName(),
                savedContact.getSubject(),
                savedContact.getMessage()
            );
            log.info("Confirmation email sent to: {}", savedContact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email to user: {}", e.getMessage());
            // Don't fail the whole operation if email fails
        }

        // Send notification email to admin
        try {
            emailService.sendContactNotificationToAdmin(
                savedContact.getFullName(),
                savedContact.getEmail(),
                savedContact.getSubject(),
                savedContact.getMessage(),
                savedContact.getId()
            );
            log.info("Notification email sent to admin");
        } catch (Exception e) {
            log.error("Failed to send notification email to admin: {}", e.getMessage());
            // Don't fail the whole operation if email fails
        }

        return savedContact;
    }

    @Override
    public PageResponse<?> getAllContacts(int pageNo, int pageSize) {
        log.info("Fetching all contacts - page: {}, size: {}", pageNo, pageSize);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Contact> contactPage = contactRepository.findAllOrderByCreatedAtDesc(pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(contactPage.getTotalPages())
                .items(contactPage.getContent())
                .build();
    }

    @Override
    public PageResponse<?> getUnreadContacts(int pageNo, int pageSize) {
        log.info("Fetching unread contacts - page: {}, size: {}", pageNo, pageSize);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Contact> contactPage = contactRepository.findUnreadContacts(pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(contactPage.getTotalPages())
                .items(contactPage.getContent())
                .build();
    }

    @Override
    @Transactional
    public Contact markAsRead(Long contactId) {
        log.info("Marking contact {} as read", contactId);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTACT_NOT_FOUND));

        contact.setIsRead(true);
        return contactRepository.save(contact);
    }

    @Override
    @Transactional
    public void deleteContact(Long contactId) {
        log.info("Deleting contact {}", contactId);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTACT_NOT_FOUND));

        contactRepository.delete(contact);
        log.info("Contact {} deleted successfully", contactId);
    }

    @Override
    public long getUnreadCount() {
        return contactRepository.countUnreadContacts();
    }
}
