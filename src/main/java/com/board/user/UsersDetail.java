package com.board.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UsersDetail {
    @Id
    private Long id; // fk reference from users.id

    private String password;

    @Column(unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Users users;
}
