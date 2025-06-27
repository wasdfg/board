package com.board.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByUsername(String username);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.createdDate >= :startOfDay")
    long countByCreatedDateAfter(LocalDateTime startOfDay);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.suspended = true")
    long countBySuspendedTrue();

    @Query("SELECT u.nickname FROM Users u ORDER BY u.lastLogin DESC LIMIT 5")
    List<String> findTop5ActiveUserNicknames();

}
