package com.practicaSV.gameLabz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.practicaSV.gameLabz.utils.JsonViews;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class UserProfile {

    @Id
    @JsonView(JsonViews.Hidden.class)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JsonView(JsonViews.Default.class)
    private User user;

    @Transient
    @JsonView(JsonViews.Default.class)
    private List<Friend> friends;

    @Transient
    @JsonView(JsonViews.Default.class)
    private List<Game> ownedGames;

    @Transient
    @JsonView(JsonViews.Hidden.class)
    private List<GeneratedKey> keys;

    @JsonView(JsonViews.Hidden.class)
    private Long points;

    @JsonView(JsonViews.Hidden.class)
    private String sharedLinkId;

    @JsonView(JsonViews.Default.class)
    private String sharedLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public List<Game> getOwnedGames() {
        return ownedGames;
    }

    public void setOwnedGames(List<Game> ownedGames) {
        this.ownedGames = ownedGames;
    }

    public List<GeneratedKey> getKeys() {
        return keys;
    }

    public void setKeys(List<GeneratedKey> keys) {
        this.keys = keys;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public synchronized void addPoints(Long points) {
        this.points += points;
    }

    public synchronized void decreasePoints(Long points) {
        this.points -= points;
    }

    public String getSharedLinkId() {
        return sharedLinkId;
    }

    public void setSharedLinkId(String sharedLinkId) {
        this.sharedLinkId = sharedLinkId;
    }

    public String getSharedLink() {
        return sharedLink;
    }

    public void setSharedLink(String sharedLink) {
        this.sharedLink = sharedLink;
    }

    public static class Builder {

        private User user;

        private Long points;

        private String sharedLinkId;

        private String sharedLink;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder points(Long points){
            this.points = points;
            return this;
        }

        public Builder sharedLinkId(String sharedLinkId) {
            this.sharedLinkId = sharedLinkId;
            return this;
        }

        public Builder sharedLink(String sharedLink) {
            this.sharedLink = sharedLink;
            return this;
        }

        public UserProfile build() { return new UserProfile(this); }
    }

    private UserProfile(Builder b) {

        this.user = b.user;
        this.points = b.points;
        this.sharedLinkId = b.sharedLinkId;
        this.sharedLink = b.sharedLink;
    }

    public UserProfile() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile profile = (UserProfile) o;

        return id != null ? id.equals(profile.id) : profile.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
