package com.said.dsbank.notification.service;

import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.notification.dtos.NotificationDTO;
import com.said.dsbank.notification.entity.Notification;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);

}
