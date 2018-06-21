package com.jw.infomgr.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);

    @Query(value = "SELECT Authority FROM user WHERE id = ?1", nativeQuery = true)
    String findAuthorityById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET Authority = ?1 WHERE id = ?2", nativeQuery = true)
    void updateAuthorityById(String Authority, int id);

    @Query(value = "SELECT * FROM user WHERE id = ?1", nativeQuery = true)
    User getFullOneById(int id);

    @Query(value = "SELECT u FROM User u")
    List<User> findAllUsers();

    @Query(value = "SELECT Authority FROM user", nativeQuery = true)
    List<String> findAllAuthorities();

    User deleteById(int id);
}
