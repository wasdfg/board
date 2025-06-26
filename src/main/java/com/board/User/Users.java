package com.board.User;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsersRole role;

    @Column(nullable = false)
    private boolean suspended = false;

    public void suspend() {
        this.suspended = true;
    }

    public void unsuspend() {
        this.suspended = false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUsersDetail(UsersDetail usersDetail) {
        this.usersDetail = usersDetail;
    }

    public void setRole(UsersRole role) {
        this.role = role;
    }
}
