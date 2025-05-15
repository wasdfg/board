package com.board.user;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String nickname;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private UsersDetail usersDetail;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUsersDetail(UsersDetail usersDetail) {
        this.usersDetail = usersDetail;
    }
}
