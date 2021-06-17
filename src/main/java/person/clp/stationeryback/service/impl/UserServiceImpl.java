package person.clp.stationeryback.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.clp.stationeryback.entity.User;
import person.clp.stationeryback.mapper.UserMapper;
import person.clp.stationeryback.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User selectUserByUsername(String username) {
        return userMapper.selectByName(username);
    }
}
