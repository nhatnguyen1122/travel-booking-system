package edu.hust.travelbookingsystem.controller;

import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.model.request.ChangePassDTO;
import edu.hust.travelbookingsystem.model.request.UserCreateDTO;
import edu.hust.travelbookingsystem.model.request.UserLoginDTO;
import edu.hust.travelbookingsystem.model.request.UserUpdateRequest;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ApiResponse<User> createUser(@Valid  @RequestBody UserCreateDTO userCreateDTO) {
        log.info("User created: " + userCreateDTO);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.createUser(userCreateDTO));
        apiResponse.setMessage("create user success");
        log.info("User created: " + apiResponse);
        return apiResponse;
    }
    @PostMapping("/login")
    public ApiResponse<User> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("User login : " + userLoginDTO);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        User user = userService.loginUser(userLoginDTO);
        apiResponse.setData(user);
        apiResponse.setMessage("login user success");
        if(user.getRole().getRoleCode().toString().equals("ADMIN")){
            apiResponse.setCode(8888);
            apiResponse.setMessage("login admin success");
            log.info("Admin login success");
        }
        return apiResponse;
    }
    @PatchMapping("/changePassword")
    public ApiResponse<User> changePassword(@Valid @RequestBody ChangePassDTO changePassDto) {
        log.info("User change password : " + changePassDto);
        userService.changePassword(changePassDto);
        log.info("User change password success");
        return new ApiResponse<>(1000,"success") ;
    }

    @GetMapping("/allUsers")
    public ApiResponse<PageResponse> getAllUsers(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                                @RequestParam(defaultValue = "5",required = false) int pageSize) {
        log.info("User getAllUsers , pageNo = {}, pageSize = {}", pageNo, pageSize);
        try{
            PageResponse<?> users = userService.getAllUsers(pageNo,pageSize) ;
            return new ApiResponse<>(1000,"get all users success",users);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    @GetMapping("/searchUser")
    public ApiResponse<PageResponse> searchUser(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                               @RequestParam(defaultValue = "5",required = false) int pageSize,
                                               @RequestParam(required = false) String search) {
        log.info("User searchUser : " + search);
        try {
            PageResponse users = userService.findUserBySearch(pageNo,pageSize,search) ;
            return new ApiResponse<>(1000,"get search user success",users);
        } catch (Exception e) {
            log.error("bug : "+e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }

    @PatchMapping("/changeStatus/{id}")
    public ApiResponse<User> changeStatus(@PathVariable Long id) {
        log.info("User change status id = {} : ", id);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.changeStatus(id));
        apiResponse.setMessage("change status success");
        log.info("User change status success");
        return apiResponse;
    }
    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        log.info("User getUser : " + id);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.findUserById(id));
        apiResponse.setMessage("get user success");
        log.info("User get success");
        return apiResponse;
    }
    @PutMapping("/update/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest user) {
        log.info("User update : " + user);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.updateUser(id,user));
        apiResponse.setMessage("update user success");
        log.info("User update success");
        return apiResponse;
    }
}
