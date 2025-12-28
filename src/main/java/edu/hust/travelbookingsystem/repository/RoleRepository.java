package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.Role;
import edu.hust.travelbookingsystem.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCode(RoleCode roleCode);
}
