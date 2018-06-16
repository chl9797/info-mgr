package com.jw.infomgr.api;

import com.alibaba.fastjson.JSONObject;
import com.jw.infomgr.annotation.CurrentUser;
import com.jw.infomgr.annotation.LoginRequired;
import com.jw.infomgr.model.User;
import com.jw.infomgr.model.UserRepository;
import com.jw.infomgr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserApi {
    private UserService userService;

    private final UserRepository userRepository;

    @Autowired
    public UserApi(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("")
    public Object add(@RequestBody User user) {
        if (userRepository.findByName(user.getName()) != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error","用户名已被使用");
            return jsonObject;
        }
        return userService.add(user);
    }

    @GetMapping("")
    @LoginRequired
    public Object findById(@CurrentUser User currentUser) {
        return userRepository.getOne(currentUser.getId());
    }

    @GetMapping("/image")
    @LoginRequired
    public Object image(@CurrentUser User currentUser) {
        return currentUser.getPassword();
    }


}