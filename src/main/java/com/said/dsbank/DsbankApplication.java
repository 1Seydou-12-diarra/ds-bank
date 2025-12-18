package com.said.dsbank;

import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.notification.dtos.NotificationDTO;
import com.said.dsbank.notification.entity.Notification;
import com.said.dsbank.notification.repo.NotificationRepo;
import com.said.dsbank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class DsbankApplication {

    private final NotificationRepo notificationRepo;

	public static void main(String[] args) {
		SpringApplication.run(DsbankApplication.class, args);
	}

    @Bean
    CommandLineRunner runner(NotificationService notificationService){
        return args -> {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient("sdiarrassouba194@gmail.com")
                    .subject("HELLO TESTING EMAIL")
                    .body("HI ,THIS A TEST EMAIL")
                    .build();
            notificationService.sendEmail(notificationDTO,new User());
        };
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // ou ton domaine
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

}
