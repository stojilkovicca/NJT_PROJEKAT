package com.mycompany.njtspringbioskop.servis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String frontendBaseUrl;
    private final String appName;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.frontend.base-url}") String frontendBaseUrl,
                       @Value("${app.name:SpringBioskop}") String appName) {
        this.mailSender = mailSender;
        this.frontendBaseUrl = frontendBaseUrl;
        this.appName = appName;
    }

    public void sendVerificationEmail(String to, String token) {
        String verifyUrl = frontendBaseUrl + "/verify?token=" + token;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[" + appName + "] Verifikacija e-mail adrese");
        msg.setText("""
                Zdravo,

                Molimo kliknite na sledeći link da verifikujete e-mail adresu:
                %s

                Link važi 24 sata.

                Pozdrav,
                %s
                """.formatted(verifyUrl, appName));

        mailSender.send(msg);
    }
}
