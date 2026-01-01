package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.model.request.EmailDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.service.implementation.EmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@Slf4j
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping()
    public ApiResponse sendEmail(@RequestBody @Valid  EmailDTO emailDTO) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setData(emailService.sendEmail(emailDTO));
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse(7777, e.getMessage());
        }
    }
    @PostMapping("/{orderId}/announce")
    public ApiResponse announceEmail(@PathVariable Long orderId) {
        ApiResponse apiResponse = new ApiResponse();
        try{
            apiResponse.setData(emailService.sendAnnounceEmail(orderId)) ;
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse(7777, e.getMessage());
        }
    }
    @PostMapping("/{orderId}/announce-pay-success")
    public ApiResponse announceEmailPaySuccess(@PathVariable Long orderId) {
        ApiResponse apiResponse = new ApiResponse();
        try{
            apiResponse.setData(emailService.sendAnnouncePaySuccessEmail(orderId)) ;
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse(7777, e.getMessage());
        }
    }
    @PostMapping("/{orderId}/announce-pay-falled")
    public ApiResponse announceEmailPayFalled(@PathVariable Long orderId) {
        ApiResponse apiResponse = new ApiResponse();
        try{
            apiResponse.setData(emailService.sendAnnouncePayFalledEmail(orderId)) ;
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse(7777, e.getMessage());
        }
    }
    @PostMapping("/{orderId}/announce-cancel")
    public ApiResponse announceEmailCancel(@PathVariable Long orderId) {
        ApiResponse apiResponse = new ApiResponse();
        try{
            apiResponse.setData(emailService.sendAnnouceCancel(orderId));
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse(7777, e.getMessage());
        }
    }
}
