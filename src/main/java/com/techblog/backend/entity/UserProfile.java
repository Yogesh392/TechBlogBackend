package com.techblog.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 500)
    private String bio;

    private String avatarUrl;

    @Column(length = 200)
    private String website;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String company;

    @Column(length = 50)
    private String githubUsername;

    @Column(length = 50)
    private String twitterUsername;
}