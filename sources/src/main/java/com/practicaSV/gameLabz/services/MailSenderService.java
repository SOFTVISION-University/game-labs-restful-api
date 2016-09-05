package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.utils.MailData;

import java.util.List;

public interface MailSenderService {

    void sendMail(List<MailData> mailDataList);
}
