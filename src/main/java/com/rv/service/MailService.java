package com.rv.service;

import com.rv.model.UserEntity;
import com.rv.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.userRepository = userRepository;
    }


    public void sendMailForOtp(String email, String otp) throws MessagingException {
        UserEntity user = userRepository.findByEmail(email);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("withlovebyrv@gmail.com");
        helper.setSubject("OTP for Password Reset");
        helper.setTo(email);
        helper.setReplyTo("withlovebyrv@gmail.com");
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("otp", otp);
        String htmlContent = templateEngine.process("password_reset.html", context);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }

    @Async
    public void sendRegistrationEmail(String email,String username, String s1) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("withlovebyrv@gmail.com");
        helper.setSubject(s1);
        helper.setTo(email);
        helper.setReplyTo("withlovebyrv@gmail.com");
        Context context = new Context();
        context.setVariable("username", username);
        String htmlContent = templateEngine.process("registration_success.html", context);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }

    public void sendMailForPasswordReset(String email,String username, String s) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("withlovebyrv@gmail.com");
        helper.setSubject(s);
        helper.setTo(email);
        helper.setReplyTo("withlovebyrv@gmail.com");
        Context context = new Context();
        context.setVariable("username", username);
        String htmlContent = templateEngine.process("password_reset_success.html", context);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }
}
