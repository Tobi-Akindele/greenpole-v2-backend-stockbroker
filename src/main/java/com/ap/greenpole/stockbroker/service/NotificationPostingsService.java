package com.ap.greenpole.stockbroker.service;

import com.ap.greenpole.stockbroker.dto.Notification;

public interface NotificationPostingsService {

	public void determineVerdictAndCallNotificationService(Notification notification, int verdict);
}
