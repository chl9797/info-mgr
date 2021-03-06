package com.jw.infomgr.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(indexes = {@Index(columnList = "name", unique = true)})
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Teacher")
public class Teacher extends User {
    private String department;  // 学院
    private String title;       // 职称

    public Teacher() {
    }

    public Teacher(User user) {
        super(user);
    }
}
