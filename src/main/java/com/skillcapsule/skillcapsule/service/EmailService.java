package com.skillcapsule.skillcapsule.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Welcome to SkillCapsule 🎓";

        Content content = new Content(
                "text/plain",
                "Hello " + name + ",\n\n" +
                        "Welcome to SkillCapsule!\n\n" +
                        "You have successfully logged in.\n" +
                        "Start learning and upgrading your skills with our capsules.\n\n" +
                        "Regards,\n" +
                        "SkillCapsule Team"
        );

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
