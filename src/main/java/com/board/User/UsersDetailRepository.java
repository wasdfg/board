package com.board.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsersDetailRepository extends JpaRepository<UsersDetail, Long> {

    @Query("SELECT ud FROM UsersDetail ud JOIN FETCH ud.users u WHERE u.username = :username")
    UsersDetail findByUsers(String username);
    UsersDetail findByEmail(String email);
}
