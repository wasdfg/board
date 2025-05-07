package com.board.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersSecurityService implements UserDetailsService {
    private final UsersRepository usersRepository; //@RequiredArgsConstructor로 자동 초기화

    private final UsersDetailRepository usersDetailRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UsersDetail usersDetail = this.usersDetailRepository.findByUsers(username); //username과 같은 이름을 찾아온다

        if (usersDetail == null) { //만약 비어있다면
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다."); //에러를 표시
        }
        Users users = usersDetail.getUsers();
        List<GrantedAuthority> authorities = new ArrayList<>(); //권한에 대한 리스트
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UsersRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UsersRole.USER.getValue()));
        }
        return new User(users.getUsername(), usersDetail.getPassword(), authorities);
    }
}
