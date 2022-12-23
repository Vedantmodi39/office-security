package com.simform.office.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value(value = "${redirect-url.reset-password}")
    private String resetPassword;

    private final JavaMailSender javaMailSender;

    public void sendResetPasswordMail(String recipient, String userName, String token, Long userId) throws MessagingException {
        String subject = "Request to set your password!";
        String body = "Hi " + userName + ",<br><br>" +
                "There was a request to change your password!<br><br>" +
                "If you did not make this request then please ignore this email.<br><br>" +
                "Otherwise, please click this link to change your password: <a href='" + resetPassword + "?token=" + token + "&id=" + userId + "'> Reset Password </a><br>" +
                "<br>" +
                "<br>" +
                "<br>" +
                "<b>Note:</b> This link will expire within 24 hours.";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(body, true);
        helper.setTo(recipient);
        helper.setSubject(subject);
        javaMailSender.send(mimeMessage);
    }
}
