package com.jw.infomgr.api;

import com.alibaba.fastjson.JSONObject;
import com.jw.infomgr.annotation.CurrentUser;
import com.jw.infomgr.annotation.LoginRequired;
import com.jw.infomgr.config.UploadConfig;
import com.jw.infomgr.model.*;
import com.jw.infomgr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

// 供普通学生和教师的api
@RestController
@RequestMapping("/api/user")
public class UserApi {
    private UserService userService;

    private final UserRepository userRepository;
    private final UploadConfig uploadConfig;

    @Autowired
    public UserApi(UserService userService, UserRepository userRepository, UploadConfig uploadConfig) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.uploadConfig = uploadConfig;
    }

//    @PostMapping("/add")
//    public Object add(@RequestBody User user) {
//        if (userRepository.findByName(user.getName()) != null) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("error", "用户名已被使用");
//            return jsonObject;
//        }
//        return userService.add(user);
//    }

    @LoginRequired
    @GetMapping("/info")
    public Object findById(@CurrentUser User currentUser) {
        return userRepository.getFullOneById(currentUser.getId());
    }

    @LoginRequired
    @PostMapping("/update")
    public Object userInfo(@RequestBody JSONObject request) {
        User userInDB = userRepository.findByName(request.getString("name"));
        return userService.update(userInDB, request);
    }

    @LoginRequired
    @PostMapping("/uploadImage")
    public Object uploadImage(@RequestParam(value = "image") MultipartFile file, @CurrentUser User currentUser) {
        JSONObject jsonObject = new JSONObject();
        if (file.isEmpty()) {
            jsonObject.put("error", "文件不能为空");
            return jsonObject;
        }
        String uploadPath = uploadConfig.getUploadPath();
        File uploadDir = new File(uploadPath);
        // 如果文件夹不存在则创建
        if (!uploadDir.exists() && !uploadDir.isDirectory()) {
            uploadDir.mkdir();
        }
        File dest = new File(uploadPath + currentUser.getId() + ".jpg");
        try {
            file.transferTo(dest);
            jsonObject.put("message", "success");
            return jsonObject;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        jsonObject.put("error", "文件上传失败");
        return jsonObject;
    }
}