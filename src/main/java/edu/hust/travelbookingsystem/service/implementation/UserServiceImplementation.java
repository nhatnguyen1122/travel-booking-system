package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.*;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.enums.RoleCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.ChangePassDTO;
import edu.hust.travelbookingsystem.model.request.UserCreateDTO;
import edu.hust.travelbookingsystem.model.request.UserLoginDTO;
import edu.hust.travelbookingsystem.model.request.UserUpdateRequest;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.repository.RoleRepository;
import edu.hust.travelbookingsystem.repository.SearchRepository;
import edu.hust.travelbookingsystem.repository.UserRepository;
import edu.hust.travelbookingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SearchRepository searchRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        // CHECK MATCH PASSWORD
        if(!userCreateDTO.getPassword().equals(userCreateDTO.getPasswordConfirm())){
            throw new AppException(ErrorCode.PASSWORD_MISMATCH) ;
        }
        // check exist phone
        if(userRepository.existsByPhone(userCreateDTO.getPhone())) {
            throw new AppException(ErrorCode.USER_EXISTS) ;
        }

        User user = new User();

        user.setPhone(userCreateDTO.getPhone());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setFullName(userCreateDTO.getFullName());
        user.setEmail(userCreateDTO.getEmail());
        user.setBirthday(userCreateDTO.getBirthday());

        Role role = roleRepository.findByRoleCode(RoleCode.USER)
                        .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND)) ;
        user.setRole(role);

        user.setStatus(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User loginUser(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByPhone(userLoginDTO.getPhone())
                .orElseThrow(() -> new AppException(ErrorCode.PHONE_NOT_EXISTS));

        if(!user.isStatus()){
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        String password = userLoginDTO.getPassword();
        if(!passwordEncoder.matches(password, user.getPassword())){
           throw new AppException(ErrorCode.PASSWORD_MISMATCH) ;
        }else{
            return user;
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePassDTO changePassDTO) {
        if(!changePassDTO.getNewPassword().equals(changePassDTO.getConfirmPassword())){
            throw new AppException(ErrorCode.PASSWORD_MISMATCH) ;
        }

        User user = userRepository.findByPhone(changePassDTO.getPhone()).
                    orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTS));

        if(!passwordEncoder.matches(changePassDTO.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.WRONG_PASSWORD) ;
        }

        user.setPassword(passwordEncoder.encode(changePassDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllUsers(int pageNo , int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(users.getTotalPages())
                .items(users.getContent())
                .build();
    }

    @Override
    @Transactional
    public User changeStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTS));
        if(user.getRole().getRoleCode().equals(RoleCode.ADMIN)){
            throw new AppException(ErrorCode.NOT_CHANGE_STATUS_ADMIN) ;
        }
        user.setStatus(!user.isStatus());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse findUserBySearch(int pageNo, int pageSize, String search) {
        return searchRepository.findBySearch(pageNo,pageSize,search) ;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTS));
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTS));
        user.setFullName(userUpdateRequest.getFullName());
        user.setEmail(userUpdateRequest.getEmail());
        user.setPhone(userUpdateRequest.getPhone());
        user.setBirthday(userUpdateRequest.getBirthday());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User createAdmin(UserCreateDTO userCreateDTO) {
        // CHECK MATCH PASSWORD
        if(!userCreateDTO.getPassword().equals(userCreateDTO.getPasswordConfirm())){
            throw new AppException(ErrorCode.PASSWORD_MISMATCH) ;
        }
        // check exist phone
        if(userRepository.existsByPhone(userCreateDTO.getPhone())) {
            throw new AppException(ErrorCode.USER_EXISTS) ;
        }
        User user = new User();
        user.setPhone(userCreateDTO.getPhone());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setFullName(userCreateDTO.getFullName());
        user.setEmail(userCreateDTO.getEmail());
        user.setBirthday(userCreateDTO.getBirthday());
        user.setStatus(true);
        user.setRole(roleRepository.findByRoleCode(RoleCode.ADMIN).orElseThrow(()-> new AppException(ErrorCode.ROLE_NOT_FOUND)));
        return userRepository.save(user);
    }
}
