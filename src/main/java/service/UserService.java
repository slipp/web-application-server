package service;

import controller.dto.UserLoginRequestDto;
import db.DataBase;
import controller.dto.UserCreateRequestDto;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService() {
    }

    public User create(UserCreateRequestDto userCreateRequestDto) {
        log.debug("====================[USER CREATE START]====================");
        if(DataBase.findUserById(userCreateRequestDto.getUserId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디 입니다.");
        }
        DataBase.addUser(userCreateRequestDto.toEntity());
        log.debug("USER CREATED: {}",DataBase.findUserById(userCreateRequestDto.getUserId()).toString());
        log.debug("====================[USER CREATE END]====================");
        return DataBase.findUserById(userCreateRequestDto.getUserId());
    }

    public User login(UserLoginRequestDto userLoginDto) {
        log.debug("====================[USER LOGIN START]====================");
        User user = DataBase.findUserById(userLoginDto.getUserId());
        if(user.getUserId() == null) throw new RuntimeException("존재하지 않는 아이디 입니다.");
        if(!user.getPassword().equals(userLoginDto.getPassword())) throw new RuntimeException("잘못된 패스워드 입니다.");
        log.debug("====================[USER LOGIN END]====================");
        return user;
    }

    public List<User> findAllUser(){
        log.debug("====================[findAllUser START]====================");
        List<User> userList = DataBase.findAll().stream().collect(Collectors.toCollection(ArrayList::new));
        for (User user : userList) {
            log.debug("{}", user);
        }
        log.debug("====================[findAllUser END]====================");
        return userList;
    }

}
