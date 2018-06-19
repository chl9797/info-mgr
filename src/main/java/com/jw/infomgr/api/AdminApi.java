package com.jw.infomgr.api;

import com.alibaba.fastjson.JSONObject;
import com.jw.infomgr.annotation.AdminRequired;
import com.jw.infomgr.annotation.CurrentUser;
import com.jw.infomgr.model.*;
import com.jw.infomgr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
public class AdminApi {
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public AdminApi(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @AdminRequired
    @GetMapping("/infoall")
    public Object infoAll(@CurrentUser User currentUser) {
        JSONObject jsonObject = new JSONObject();
        if (userRepository.findAuthority(currentUser.getId()).equals("Admin")) {
            jsonObject.put("users", userRepository.findAllUsers());
            jsonObject.put("authorities", userRepository.findAllAuthorities());
        } else {
            jsonObject.put("error", "请以管理员身份登录");
        }
        return jsonObject;
    }

    @AdminRequired
    @GetMapping("/info/{id}")
    public Object findById(@PathVariable int id) {
        return userRepository.getFullOne(id);
    }

    @AdminRequired
    @PostMapping("/update/{id}")
    public Object userInfo(@RequestBody JSONObject request, @PathVariable int id) {
        User userInDB = userRepository.getFullOne(id);
        return userService.update(userInDB, request);
    }
}
