package com.jw.infomgr.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);

    @Query(value = "select Authority from user where id = ?1", nativeQuery = true)
    String findAuthority(int id);
}
