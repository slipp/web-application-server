package service;

import db.DataBase;
import controller.dto.UserCreateRequestDto;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService() {
    }

    public User create(UserCreateRequestDto userCreateRequestDto) {
        log.debug("====================[USER CREATE]====================");
        if(DataBase.findUserById(userCreateRequestDto.getUserId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디 입니다.");
        }
        DataBase.addUser(userCreateRequestDto.toEntity());
        log.debug("USER CREATED: {}",DataBase.findUserById(userCreateRequestDto.getUserId()).toString());
        return DataBase.findUserById(userCreateRequestDto.getUserId());
    }

}
