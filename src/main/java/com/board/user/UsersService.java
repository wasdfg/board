package com.board.user;

import com.board.DataNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    public SignUpUser create(String username, String password,String email){
        SignUpUser users = new SignUpUser();
        users.setEmail(email);
        users.setPassword(passwordEncoder.encode(password)); //해시함수로 암호화를 해줌
        users.setUsername(username);
        this.usersRepository.save(users);
        return users;
    }

    public SignUpUser getUser(String username) {
        Optional<SignUpUser> signUpUser = this.usersRepository.findByUsername(username);
        if (signUpUser.isPresent()) {
            return signUpUser.get();
        } else {
            throw new DataNotFoundException("SignUpUser not found");
        }
    }

    @Transactional
    public SignUpUser updatePw(SignUpUser signUpUser,String password){
        SignUpUser signUpUser1 = signUpUser;
        signUpUser1.setPassword(passwordEncoder.encode(password));
        return this.usersRepository.save(signUpUser1);
    }

    public Optional<SignUpUser> getUserByEmail(String email){
        Optional<SignUpUser> signUpUser = this.usersRepository.findByEmail(email);
        return signUpUser;
    }
}
