package com.board.user;

import com.board.DataNotFoundException;
import com.board.user.dto.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String FromAddress = "ljh60700@gmail.com";

    @Autowired
    private final JavaMailSender mailSender;

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

    public SignUpUser getUserByEmail(String email){
        SignUpUser signUpUser = this.usersRepository.findByEmail(email);
        return signUpUser;
    }

    public String makeTempPw(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        String a = "";
        for(int i = 0;i < 10;i++){
            a+=charSet[(int)((double)charSet.length*Math.random())];
        }
        return a;
    }

    public void sendMail(MailDto mailDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(new InternetAddress(FromAddress,"게시판"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(mailDto.getEmail());
        helper.setSubject(mailDto.getTitle());
        helper.setText(mailDto.getMessage());
        mailSender.send(message);
    }
}
