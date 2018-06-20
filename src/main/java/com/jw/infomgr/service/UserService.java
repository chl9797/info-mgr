package com.jw.infomgr.service;

import com.alibaba.fastjson.JSONObject;
import com.jw.infomgr.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private static final String salt = "kira's secret";

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    @Autowired
    public UserService(UserRepository userRepository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public User add(User user) {
        String passwordHash = passwordToHash(user.getPassword());
        user.setPassword(passwordHash);
        userRepository.save(user);
        return userRepository.getFullOneById(user.getId());
    }

    public void add(JSONObject jsonObject) {
        User user = new User();
        user.setName(jsonObject.getString("name"));
        user.setPassword(passwordToHash(jsonObject.getString("password")));
        user.setAge(jsonObject.getInteger("age"));
        user.setGender(Gender.valueOf(jsonObject.getString("gender")));
        user.setPhone(jsonObject.getString("phone"));
//        userRepository.save(user);
        if (jsonObject.getString("studentID") != null) {
            Student student = new Student(user);
            student.setStudentID(jsonObject.getString("studentID"));
            student.setClassNum(jsonObject.getInteger("classNum"));
            student.setDepartment(jsonObject.getString("department"));
            student.setGrade(jsonObject.getInteger("grade"));
            student.setMajor(jsonObject.getString("major"));
            studentRepository.save(student);
        } else if (jsonObject.getString("title") != null) {
            Teacher teacher = (Teacher) user;
            teacher.setDepartment(jsonObject.getString("department"));
            teacher.setTitle(jsonObject.getString("title"));
            teacherRepository.save(teacher);
        } else {
            userRepository.save(user);
        }
    }

    private String passwordToHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
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

    public JSONObject update(User user, JSONObject request) {
        JSONObject jsonObject = new JSONObject();
        if (user == null) {
            jsonObject.put("error", "用户不存在");
        } else {
            user.setAge(request.getInteger("age"));
            user.setGender(Gender.valueOf(request.getString("gender")));
            user.setPhone(request.getString("phone"));
            if (user.getClass().equals(Student.class)) {
                Student student = (Student) user;
                student.setClassNum(request.getInteger("classNum"));
                student.setDepartment(request.getString("department"));
                student.setGrade(request.getInteger("grade"));
                student.setMajor(request.getString("major"));
                studentRepository.save(student);
            } else if (user.getClass().equals(Teacher.class)) {
                Teacher teacher = (Teacher) user;
                teacher.setDepartment(request.getString("department"));
                teacher.setTitle(request.getString("title"));
                teacherRepository.save(teacher);
            } else {
                userRepository.save(user);
            }
            String authority = request.getString("authority");
            if (authority != null) {
                userRepository.updateAuthorityById(authority, user.getId());
            }
            jsonObject.put("message", "success");
        }
        return jsonObject;
    }
}
