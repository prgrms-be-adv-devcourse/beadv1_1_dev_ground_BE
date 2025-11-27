package com.example.user.utils.provider;

import java.time.Duration;

import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.user.service.RedisService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;
	private final RedisService redisService;

	public void sendEmail(String email, String verificationCode) throws MessagingException {
		try {
			redisService.save(email, verificationCode, Duration.ofMinutes(5));

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			Context context = new Context();
			context.setVariable("code", verificationCode);
			context.setVariable("logoCid", "dbayLogo");
			String htmlContent = templateEngine.process("email-template", context);

			String subject = "[DBay] 인증메일입니다.";

			helper.setSubject(subject);
			helper.setTo(email);
			helper.setText(htmlContent, true);
			ClassPathResource logo = new ClassPathResource("static/dbayLogo.png");
			helper.addInline("dbayLogo", logo);

			mailSender.send(mimeMessage);

		} catch (Exception e) {
			log.error(e.getMessage());

			throw e;
		}
	}
}