package com.board.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
@Entity
public class UsersDetail {
    @Id
    private Long id; // Foreign Key referencing User.id

    private String password;

    @Column(unique = true)
    private String email;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Users users;
}
