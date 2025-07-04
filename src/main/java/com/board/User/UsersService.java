package com.board.User;

import com.board.DataNotFoundException;
import com.board.User.dto.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    private final UsersDetailRepository usersDetailRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String FromAddress = "ljh60700@gmail.com";

    private final JavaMailSender mailSender;

    @Transactional
    public void create(String username,String password,String email){
        Users users = new Users();
        users.setUsername(username);

        UsersDetail usersDetail = new UsersDetail();
        usersDetail.setUsers(users);
        usersDetail.setEmail(email);
        usersDetail.setPassword(passwordEncoder.encode(password)); //해시함수로 암호화를 해줌
        this.usersDetailRepository.save(usersDetail);

    }

    public UsersDetail getUsersDetail(String username) {
        Users users = this.usersRepository.findByUsername(username);
        if(users != null) {
            UsersDetail usersDetail = this.usersDetailRepository.findByUsers(username);
            if (usersDetail != null) {
                return usersDetail;
            } else {
                throw new DataNotFoundException("SignUpUser not found");
            }
        }
        return null;
    }

    public Users getUsers(String username){
        Users users = this.usersRepository.findByUsername(username);
        if (users != null) {
            return users;
        } else {
            throw new DataNotFoundException("SignUpUser not found");
        }
    }

    @Transactional
    public UsersDetail updatePw(UsersDetail usersDetail,String password){
        UsersDetail usersDetail1 = usersDetail;
        usersDetail1.setPassword(passwordEncoder.encode(password));
        return this.usersDetailRepository.save(usersDetail1);
    }

    public UsersDetail getUserByEmail(String email){
        UsersDetail usersDetail = this.usersDetailRepository.findByEmail(email);
        return usersDetail;
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
