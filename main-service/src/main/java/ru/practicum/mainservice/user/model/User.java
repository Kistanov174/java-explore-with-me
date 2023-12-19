package ru.practicum.mainservice.user.model;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@ToString
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private Long id;
    private String name;
    @Column(name = "email", unique = true)
    private String email;
}