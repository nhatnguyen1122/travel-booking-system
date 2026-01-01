package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.model.request.EmailDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.service.implementation.EmailService;
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

class EmailControllerTest {

    @InjectMocks
    private EmailController emailController;

    @Mock
    private EmailService emailService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
    }

    @Test
    void announceEmail_success() throws Exception {
        // Mock hành động gửi email thông báo từ service
        when(emailService.sendAnnounceEmail(any(Long.class))).thenReturn("Announcement email sent successfully");

        mockMvc.perform(post("/api/v1/email/1/announce")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Announcement email sent successfully"));

        verify(emailService).sendAnnounceEmail(any(Long.class));
    }

    @Test
    void announceEmailPaySuccess_success() throws Exception {
        // Mock hành động gửi email thông báo thanh toán thành công
        when(emailService.sendAnnouncePaySuccessEmail(any(Long.class))).thenReturn("Pay success email sent successfully");

        mockMvc.perform(post("/api/v1/email/1/announce-pay-success")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Pay success email sent successfully"));

        verify(emailService).sendAnnouncePaySuccessEmail(any(Long.class));
    }
}
