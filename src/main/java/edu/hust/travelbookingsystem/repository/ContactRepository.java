package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC")
    Page<Contact> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.isRead = false ORDER BY c.createdAt DESC")
    Page<Contact> findUnreadContacts(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.isRead = false")
    long countUnreadContacts();
}
