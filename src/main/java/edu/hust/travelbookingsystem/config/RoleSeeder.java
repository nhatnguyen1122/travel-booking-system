//package com.web.travelbookingsystem.config;
//
//import com.web.travelbookingsystem.entity.Role;
//import com.web.travelbookingsystem.entity.User;
//import com.web.travelbookingsystem.enums.RoleCode;
//import com.web.travelbookingsystem.repository.RoleRepository;
//import com.web.travelbookingsystem.repository.UserRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Component;
//
//import hust.time.LocalDate;
//import hust.time.ZoneId;
//import hust.util.Date;
//
//@Component
//public class RoleSeeder {
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//
//    public RoleSeeder(RoleRepository roleRepository, UserRepository userRepository) {
//        this.roleRepository = roleRepository;
//        this.userRepository = userRepository;
//    }
//
//    @PostConstruct
//    @Transactional
//    public void initRoles() {
//        if (roleRepository.count() == 0) {
//            roleRepository.save(new Role(RoleCode.USER));
//            Role role = new Role(RoleCode.ADMIN);
//            roleRepository.save(role);
//            User user = new User("0123456789","123456",role);
//            userRepository.save(user);
//        }
//        else{
//            Role role = roleRepository.findByRoleCode(RoleCode.ADMIN)
//                    .orElseThrow(() -> new RuntimeException("Role Not Found"));
//            User user = new User("0123456789","123456",role);
//            userRepository.save(user);
//        }
//
//    }
//}
package edu.hust.travelbookingsystem.config;

import edu.hust.travelbookingsystem.entity.Role;
import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.enums.RoleCode;
import edu.hust.travelbookingsystem.repository.RoleRepository;
import edu.hust.travelbookingsystem.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RoleSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.count() == 0) {
            // Tạo và lưu role USER
            Role userRole = new Role(RoleCode.USER);
            roleRepository.save(userRole);

            // Tạo và lưu role ADMIN
            Role adminRole = new Role(RoleCode.ADMIN);
            roleRepository.save(adminRole);

            // Tạo và lưu user admin
            LocalDate localDate = LocalDate.of(2000, 10, 10);
            Date birthday = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            User adminUser = new User("0123456789", passwordEncoder.encode("123456"), "ADMIN", "a@gmail.com", birthday, true);
            adminUser.setRole(adminRole);
            userRepository.save(adminUser);
        } else {
            // Nếu bảng roles đã có dữ liệu, kiểm tra và tạo user admin nếu chưa tồn tại
            Role adminRole = roleRepository.findByRoleCode(RoleCode.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

            if (userRepository.findByPhone("0123456789").isEmpty()) {
                LocalDate localDate = LocalDate.of(2000, 10, 10);
                Date birthday = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                User adminUser = new User("0123456789", passwordEncoder.encode("123456"), "ADMIN", "a@gmail.com", birthday, true);
                adminUser.setRole(adminRole);
                userRepository.save(adminUser);
            }
        }
    }
}
