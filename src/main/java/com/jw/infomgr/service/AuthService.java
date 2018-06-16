package com.jw.infomgr.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jw.infomgr.model.User;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class AuthService {
    public String getToken(User user) {
        String token = "";
        try {
            token = JWT.create()
                    .withAudience(Integer.toString(user.getId()))          // 将 user id 保存到 token 里面
                    .sign(Algorithm.HMAC256(user.getPassword()));   // 以 password 作为 token 的密钥
        } catch (UnsupportedEncodingException ignore) {
        }
        return token;
    }
}
