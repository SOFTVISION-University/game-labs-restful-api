package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.utils.MailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendMail(List<MailData> mailDataList) {

        mailDataList.stream()
                .forEach(mailData -> {
                    MimeMessage message = mailSender.createMimeMessage();
                    try {
                        byte[] bytes = mailData.getAttachment().toByteArray();

                        MimeMessageHelper helper = new MimeMessageHelper(message, true);
                        helper.setFrom(mailData.getUserFrom());
                        helper.setTo(mailData.getUserTo());
                            helper.setSubject(mailData.getSubject());
                        helper.setText("This is the confirmation for your transaction.");
                        helper.addAttachment("Confirmation.pdf", new ByteArrayResource(bytes));

                        mailSender.send(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
    }
}
