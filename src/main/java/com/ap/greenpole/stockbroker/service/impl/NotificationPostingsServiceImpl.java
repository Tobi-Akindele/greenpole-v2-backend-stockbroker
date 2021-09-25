package com.ap.greenpole.stockbroker.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.ap.greenpole.stockbroker.dto.Notification;
import com.ap.greenpole.stockbroker.service.NotificationPostingsService;
import com.ap.greenpole.stockbroker.service.RestService;
import com.ap.greenpole.stockbroker.utils.ConstantUtils;

@Service
public class NotificationPostingsServiceImpl implements NotificationPostingsService {

	@Autowired
	private RestService restService;

	@Value("${notification.service.base.url}")
	private String notificationServiceBaseUrl;
	
	@Autowired
	private ThreadPoolExecutor notificationPostingExecutor;
	
	private Map<String, String> defaultHeaders;

	@PostConstruct
	private void setDefaultHeaders() {
		defaultHeaders = new HashMap<>();
		defaultHeaders.put("Content-Type", "application/json");
	}
	
	@Override
	public void determineVerdictAndCallNotificationService(Notification notification, int verdict) {

		notificationPostingExecutor.execute(new Runnable() {

			@Override
			public void run() {
				if (notification != null) {
					notification.setModulePermission(ConstantUtils.MODULE_APPROVAL_PERMISSION);
					notification.setModuleName(ConstantUtils.MODULE);
					String path = null;
					if (ConstantUtils.PENDING == verdict) {
						path = ConstantUtils.PENDING_URL;
					} else if (ConstantUtils.ACCEPTED == verdict) {
						path = ConstantUtils.APPROVED_URL;
					} else if (ConstantUtils.REJECTED == verdict) {
						path = ConstantUtils.REJECTED_URL;
					}
					String url = notificationServiceBaseUrl + path;
					restService.request(url, defaultHeaders, HttpMethod.POST, notification, String.class);
				}
			}

		});
	}
}
