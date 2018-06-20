package com.jw.infomgr.api;

import com.alibaba.fastjson.JSONObject;
import com.jw.infomgr.model.User;
import com.jw.infomgr.model.UserRepository;
import com.jw.infomgr.service.AuthService;
import com.jw.infomgr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
public class AuthApi {
    private AuthService authService;
    private UserService userService;

    private final UserRepository userRepository;

    @Autowired
    public AuthApi(AuthService authService, UserService userService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("")
    public Object login(@RequestBody User user) {
        User userInDB = userRepository.findByName(user.getName());
        JSONObject jsonObject = new JSONObject();
        if (userInDB == null) {
            jsonObject.put("error", "用户不存在");
        } else if (!userService.comparePassword(user, userInDB)) {
            jsonObject.put("error", "密码不正确");
        } else {
            String token = authService.getToken(userInDB);
            String authority = userRepository.findAuthorityById(userInDB.getId());
            jsonObject.put("token", token);
            jsonObject.put("user", userInDB);
            jsonObject.put("authority", authority); // 用户类型，用于前端的显示
        }
        return jsonObject;
    }
}
