package com.jw.infomgr.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


enum Gender {
    male("男"),
    female("女"),
    unknown("未知");

    private String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

enum Authority {
    student("学生"),
    teacher("老师"),
    admin("管理员");

    private String name;

    Authority(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

//personService.save(new Person("Jhon", Gender.male));
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Authority", discriminatorType = DiscriminatorType.STRING)
public class User implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;
    private int age;
    private String phone;
    private String studentID;  // 学号/工号
    private String image;       // 头像路径

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;      // 性别

//    @Column
//    @Enumerated(EnumType.STRING)
//    private Authority authority;    // 权限

    @CreatedDate
    private Date createdTime;

    @LastModifiedDate
    private Date updateTime;
}