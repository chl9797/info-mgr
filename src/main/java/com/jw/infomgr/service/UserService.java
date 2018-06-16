package com.jw.infomgr.service;

import com.jw.infomgr.model.User;
import com.jw.infomgr.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        String passwordHash = passwordToHash(user.getPassword());
        user.setPassword(passwordHash);
        userRepository.save(user);
        return userRepository.getOne(user.getId());
    }

    private String passwordToHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String salt = "kira's secret";
            digest.update((password + salt).getBytes());
            byte[] src = digest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            // 字节数组转16进制字符串
            for (byte aSrc : src) {
                String s = Integer.toHexString(aSrc & 0xFF);
                if (s.length() < 2) {
                    stringBuilder.append('0');
                }
                stringBuilder.append(s);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException ignore) {
        }
        return null;
    }


    public boolean comparePassword(User user, User userInDataBase) {
        return passwordToHash(user.getPassword())      // 将用户提交的密码转换为 hash
                .equals(userInDataBase.getPassword()); // 数据库中的 password 已经是 hash，不用转换
    }
}
