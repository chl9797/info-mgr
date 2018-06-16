package com.jw.infomgr.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


enum Gender {
    male("男"),
    female("女");

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
@Entity
@Table(indexes = {@Index(name = "USER", columnList = "name", unique = true)})
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private String student_id;  // 学号

    @Column
    private String image;       // 头像路径

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @CreatedDate
    private Date createdTime;

    @LastModifiedDate
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}