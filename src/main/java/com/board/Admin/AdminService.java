package com.board.Admin;

import com.board.User.Users;
import com.board.User.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    @Autowired
    private final UsersRepository usersRepository;
    public Page<Users> getAllUsers(int page){
        Pageable pageable = PageRequest.of(page, 20);
        return usersRepository.findAll(pageable);
    }
}
