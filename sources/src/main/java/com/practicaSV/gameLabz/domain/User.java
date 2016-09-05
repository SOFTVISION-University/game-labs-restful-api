package com.practicaSV.gameLabz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.practicaSV.gameLabz.utils.JsonViews;

import javax.persistence.*;

@Entity
@Table(name = "gameLabz_user")
public class User {

    @Id
    @JsonView(JsonViews.Default.class)
    private String userName;

    @JsonView(JsonViews.Hidden.class)
    private String password;

    @JsonView(JsonViews.Default.class)
    private String email;

    @Enumerated(EnumType.STRING)
    @JsonView(JsonViews.Default.class)
    private UserType userType = UserType.CLIENT;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return userName != null ? userName.equals(user.userName) : user.userName == null;
    }

    @Override
    public int hashCode() {
        return userName != null ? userName.hashCode() : 0;
    }

    public enum UserType {
        ADMIN,
        CLIENT
    }

    public static class Builder {

        private String userName;

        private String password;

        private String email;

        private UserType userType = UserType.CLIENT;

        public Builder name(String userName) {

            this.userName = userName;
            return this;
        }

        public Builder pass(String password) {

            this.password = password;
            return this;
        }

        public Builder email(String email) {

            this.email = email;
            return this;
        }

        public Builder userType(UserType userType) {

            this.userType = userType;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    private User(Builder b) {
        this.userName = b.userName;
        this.password = b.password;
        this.email = b.email;
        this.userType = b.userType;
    }

    public User() {}
}
