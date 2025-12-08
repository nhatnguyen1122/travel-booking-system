package edu.hust.travelbookingsystem.config;

import edu.hust.travelbookingsystem.entity.Role;
import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.enums.RoleCode;
import edu.hust.travelbookingsystem.repository.RoleRepository;
import edu.hust.travelbookingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RoleSeederTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleSeeder roleSeeder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Khởi tạo các đối tượng mock
    }

    @Test
    void testRunWhenRolesExistButAdminUserDoesNotExist() throws Exception {
        // Giả lập hành vi khi bảng roles đã có dữ liệu và không có user admin
        when(roleRepository.count()).thenReturn(1L);  // Giả lập bảng roles có ít nhất một role
        when(roleRepository.findByRoleCode(RoleCode.ADMIN)).thenReturn(Optional.empty());  // Role ADMIN chưa có
        when(userRepository.findByPhone("0123456789")).thenReturn(Optional.empty());  // User với số điện thoại này chưa có
        when(userRepository.save(any(User.class))).thenReturn(new User());  // Giả lập việc lưu user

        // Giả lập hành vi khi tìm role ADMIN và thêm user admin
        Role adminRole = new Role(RoleCode.ADMIN);
        when(roleRepository.findByRoleCode(RoleCode.ADMIN)).thenReturn(Optional.of(adminRole));

        // Chạy phương thức run() của RoleSeeder
        roleSeeder.run(null);

        // Kiểm tra rằng phương thức save() được gọi đúng với user admin
        verify(userRepository).save(any(User.class));  // Kiểm tra gọi save cho User
    }

    @Test
    void testRunWhenAdminUserAlreadyExists() throws Exception {
        // Giả lập khi bảng roles đã có dữ liệu và user admin đã tồn tại
        when(roleRepository.count()).thenReturn(1L);  // Giả lập bảng roles có ít nhất một role
        Role adminRole = new Role(RoleCode.ADMIN);
        when(roleRepository.findByRoleCode(RoleCode.ADMIN)).thenReturn(Optional.of(adminRole));  // Role ADMIN đã có
        when(userRepository.findByPhone("0123456789")).thenReturn(Optional.of(new User()));  // User với số điện thoại này đã có

        // Chạy phương thức run()
        roleSeeder.run(null);

        // Kiểm tra rằng phương thức save() không được gọi khi user admin đã tồn tại
        verify(userRepository, never()).save(any(User.class));  // Không gọi save nếu user đã tồn tại
    }
}
