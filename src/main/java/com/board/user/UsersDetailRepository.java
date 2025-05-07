package com.board.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersDetailRepository extends JpaRepository<UsersDetail, Long> {
    //Optional<UsersDetail> findByUsername(String username);

    @Query("SELECT ud FROM UsersDetail ud JOIN FETCH ud.users u WHERE u.username = :username")
    UsersDetail findByUsers(String username);
    UsersDetail findByEmail(String email);
}
