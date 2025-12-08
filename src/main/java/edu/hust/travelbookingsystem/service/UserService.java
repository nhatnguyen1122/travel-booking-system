package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.model.request.ChangePassDTO;
import edu.hust.travelbookingsystem.model.request.UserCreateDTO;
import edu.hust.travelbookingsystem.model.request.UserLoginDTO;
import edu.hust.travelbookingsystem.model.request.UserUpdateRequest;
import edu.hust.travelbookingsystem.model.response.PageResponse;

public interface UserService {
    public User createUser(UserCreateDTO userCreateDTO);
    public User loginUser(UserLoginDTO userLoginDTO);
    public void changePassword(ChangePassDTO changePassDto);
    public PageResponse getAllUsers(int pageNo, int pageSize);
    public User changeStatus(Long id);
    public PageResponse findUserBySearch(int pageNo,int pageSize,String search) ;
    public User findUserById(Long id);
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest);
    public User createAdmin(UserCreateDTO userCreateDTO);
}
