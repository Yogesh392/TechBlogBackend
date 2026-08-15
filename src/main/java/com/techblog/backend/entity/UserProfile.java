package com.techblog.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "github_username", length = 50)
    private String githubUsername;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "twitter_username", length = 50)
    private String twitterUsername;

    @Column(name = "website", length = 200)
    private String website;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}