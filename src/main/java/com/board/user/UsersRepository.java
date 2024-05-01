package com.board.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<SignUpUser, Long> {
    Optional<SignUpUser> findByUsername(String username);

    Optional<SignUpUser> findByEmail(String email);
}
