package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.model.request.UserCreateDTO;
import edu.hust.travelbookingsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void createAdmin_success() throws Exception {
        // Mock User trả về từ service
        User user = new User();
        user.setId(1L);
        user.setPhone("0123456789");
        user.setFullName("Admin Test");
        user.setEmail("admin@test.com");

        when(userService.createAdmin(any(UserCreateDTO.class))).thenReturn(user);

        String requestBody = """
                {
                  "phone": "0123456789",
                  "password": "123456",
                  "passwordConfirm": "123456",
                  "fullName": "Admin Test",
                  "email": "admin@test.com"
                }
                """;

        mockMvc.perform(post("/admin/acc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.data.phone").value("0123456789"))
                .andExpect(jsonPath("$.data.fullName").value("Admin Test"))
                .andExpect(jsonPath("$.data.email").value("admin@test.com"));

        verify(userService).createAdmin(any(UserCreateDTO.class));
    }
}
