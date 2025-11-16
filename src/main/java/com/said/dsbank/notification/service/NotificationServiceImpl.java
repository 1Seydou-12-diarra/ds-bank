package com.said.dsbank.notification.service;

import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.enums.NotificationType;
import com.said.dsbank.notification.dtos.NotificationDTO;
import com.said.dsbank.notification.entity.Notification;
import com.said.dsbank.notification.repo.NotificationRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepo notificationRepo;
    private final JavaMailSender mailSender;
    private  final TemplateEngine templateEngine;


    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO, User user) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());

            if (notificationDTO.getTemplateName() != null) {

                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String template = templateEngine.process(notificationDTO.getTemplateName(), context);
                helper.setText(template, true);

            }else {
                helper.setText(notificationDTO.getBody(), true);

            }
            mailSender.send(mimeMessage);
            log.info("email send out");

            //save
            // Notification notificationToSave = Notification.builder()
                   // .recipient(notificationDTO.getRecipient())
                   // .subject(notificationDTO.getSubject())
                        //.body(notificationDTO.getBody())
                   // .type(NotificationType.EMAIL)
                   // .user(user)
                   // .build();   // ✔️ Correct


            //notificationRepo.save(notificationToSave);


        }catch (MessagingException e){
            log.error(e.getMessage());
        }


    }
}
