package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.model.request.UserCreateDTO;
import edu.hust.travelbookingsystem.model.response.ApiReponse;
import edu.hust.travelbookingsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private UserService userService;
    @PostMapping("/acc")
    public ApiReponse createAdmin(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        log.info("Start createAdmin : {}", userCreateDTO);
        ApiReponse apiReponse = new ApiReponse();
        apiReponse.setData(userService.createAdmin(userCreateDTO));
        return apiReponse;
    }
}
