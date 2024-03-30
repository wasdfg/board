package com.board.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    public SignUpUser create( String username, String password,String email){
        SignUpUser users = new SignUpUser();
        users.setEmail(email);
        users.setPassword(passwordEncoder.encode(password)); //해시함수로 암호화를 해줌
        users.setUsername(username);
        this.usersRepository.save(users);
        return users;
    }
}
