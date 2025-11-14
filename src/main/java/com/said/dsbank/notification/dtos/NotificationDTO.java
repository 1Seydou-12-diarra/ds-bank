package com.said.dsbank.notification.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private String subject;
    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String  body;
    private NotificationType type;

    //private UserDTO user;
    private  LocalDateTime createdAt ;

    //for values/variables to be passed into email to send
    private String templateName;
    private Map<String, Object> templateVariables;
}
