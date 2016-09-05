package com.practicaSV.gameLabz.domain;

import javax.persistence.*;

@Entity
@Table
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    private User fromWho;

    @OneToOne
    private User toWhom;

    @Transient
    private Status status;

    public static final String CHOICE = "choice";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFromWho() {
        return fromWho;
    }

    public void setFromWho(User fromWho) {
        this.fromWho = fromWho;
    }

    public User getToWhom() {
        return toWhom;
    }

    public void setToWhom(User toWhom) {
        this.toWhom = toWhom;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {

        CREATED,
        ACCEPTED,
        REJECTED,
        CANCELLED
    }
}
