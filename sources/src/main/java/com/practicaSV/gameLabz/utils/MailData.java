package com.practicaSV.gameLabz.utils;

import java.io.ByteArrayOutputStream;

public class MailData {

    private String userFrom;

    private String userTo;

    private ByteArrayOutputStream attachment;

    private String subject;

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public ByteArrayOutputStream getAttachment() {
        return attachment;
    }

    public void setAttachment(ByteArrayOutputStream attachment) {
        this.attachment = attachment;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
