package com.board.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersDetailRepository extends JpaRepository<UsersDetail, Long> {
    //Optional<UsersDetail> findByUsername(String username);

    UsersDetail findByUsers(Optional<Users> users);
    UsersDetail findByEmail(String email);
}
