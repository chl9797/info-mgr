package com.jw.infomgr.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(indexes = {@Index(columnList = "name", unique = true)})
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Student")
public class Student extends User {
//    private String address;
    private String department;  // 学院
    private String major;       // 专业
    private Integer grade;          // 年级
    private Integer classNum;       // 班级

    public Student() {
    }

    public Student(User user) {
        super(user);
    }
}
