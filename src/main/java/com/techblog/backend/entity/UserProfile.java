package com.techblog.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profiles")
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

    // Constructors
    public UserProfile() {}

    public UserProfile(String avatarUrl, String bio, String company, String githubUsername,
                       String location, String twitterUsername, String website, User user) {
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.company = company;
        this.githubUsername = githubUsername;
        this.location = location;
        this.twitterUsername = twitterUsername;
        this.website = website;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getGithubUsername() { return githubUsername; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTwitterUsername() { return twitterUsername; }
    public void setTwitterUsername(String twitterUsername) { this.twitterUsername = twitterUsername; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}