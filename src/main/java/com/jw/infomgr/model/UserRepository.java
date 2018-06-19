package com.jw.infomgr.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);

    @Query(value = "SELECT Authority FROM user WHERE id = ?1", nativeQuery = true)
    String findAuthority(int id);

    @Query(value = "SELECT * FROM user WHERE id = ?1", nativeQuery = true)
    User getFullOne(int id);

    @Query(value = "SELECT u FROM User u")
    List<User> findAllUsers();

    @Query(value = "SELECT Authority FROM User", nativeQuery = true)
    List<String> findAllAuthorities();
}
