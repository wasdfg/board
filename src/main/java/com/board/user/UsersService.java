package com.board.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    public SignInUser create(String email,String password,String username){
        SignInUser users = new SignInUser();
        users.setEmail(email);
        users.setPassword(passwordEncoder.encode(password)); //해시함수로 암호화를 해줌
        users.setUsername(username);
        this.usersRepository.save(users);
        return users;
    }
}
