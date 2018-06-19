package com.jw.infomgr.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


//personService.save(new Person("Jhon", Gender.male));
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "USER")
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
//    private String image;       // 头像路径

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