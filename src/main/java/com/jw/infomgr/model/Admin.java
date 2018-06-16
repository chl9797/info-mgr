package com.jw.infomgr.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(indexes = {@Index(columnList = "name", unique = true)})
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Admin")
public class Admin extends User {
}
