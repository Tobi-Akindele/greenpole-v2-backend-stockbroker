package com.ap.greenpole.stockbroker.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ap.greenpole.stockbroker.dto.Notification;
import com.ap.greenpole.stockbroker.dto.SearchCriteria;
import com.ap.greenpole.stockbroker.dto.SearchOperation;
import com.ap.greenpole.stockbroker.exceptions.BadRequestException;
import com.ap.greenpole.stockbroker.model.ModuleRequest;
import com.ap.greenpole.stockbroker.model.StockBroker;
import com.ap.greenpole.stockbroker.service.ModuleRequestService;
import com.ap.greenpole.usermodule.model.GreenPoleUserDetails;
import com.ap.greenpole.usermodule.model.User;

public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);
	
	private Utils() {}
	
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().equalsIgnoreCase("") || str.equalsIgnoreCase("null");
	}

	public static boolean isValidEmails(List<String> emails) {
		boolean status = false;
		List<Boolean> stat = new ArrayList<>();
		if (emails != null && !emails.isEmpty()) {

			for (String e : emails) {
				boolean s = isValidEmail(e);
				stat.add(s);

			}
			status = (!stat.isEmpty() && !stat.contains(false));
		}

		return status;
	}

	public static boolean isValidEmail(String email) {
		String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		return email.matches(EMAIL_REGEX);
	}

	public static boolean isValidListValues(List<String> listValues) {
		boolean status = false;
		List<Boolean> stat = new ArrayList<>();
		for (String value : listValues) {
			boolean b = !isEmptyString(value);
			stat.add(b);
		}
		status = (!stat.isEmpty() && !stat.contains(false));

		return status;
	}

	public static ModuleRequest checkForOngoingApproval(ModuleRequestService moduleRequestService, Long stockBrokerId,
			String requestType) {
		ModuleRequest moduleRequest = new ModuleRequest();
		moduleRequest.setResourceId(stockBrokerId);
		moduleRequest.setActionRequired(requestType);
		ModuleRequest request = moduleRequestService.getApprovalRequestByResourceIdAndActionRequired(moduleRequest);
		if (request != null && ConstantUtils.PENDING == request.getStatus())
			throw new BadRequestException("400", "There is an ongoing " + requestType + " Approval process");
		return moduleRequest;
	}

	public static List<String> commaSeperatedToList(String src) {
		List<String> result = new ArrayList<>();
		if (!isEmptyString(src)) {
			result = Arrays.asList(StringUtils.split(src, ','));
		}
		return result;
	}

	public static String getDateString(Date date) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		sdfDate.format(0);
		
		return sdfDate.format(date);
	}

	static String splitCamelCase(String s) {
		return s.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}

	public static GreenPoleUserDetails getCurrentUserDetail() {
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
			UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
					.getContext().getAuthentication();
			GreenPoleUserDetails user = null;
			if (auth != null) {
				user = (GreenPoleUserDetails) auth.getPrincipal();
			}
			return user;
		}
		return null;
	}

	public static ModuleRequest setRequestStatus(int status, Long approverId, String reason, ModuleRequest request) {
		if (request != null) {
			request.setStatus(status);
			request.setApproverId(approverId);
			request.setReason(reason);
		}
		return request;
	}

	public static <T> List<SearchCriteria> prepareSearchSpecification(T target) {

		List<SearchCriteria> result = new ArrayList<>();
		final BeanWrapper src = new BeanWrapperImpl(target);
		for (final Field property : target.getClass().getDeclaredFields()) {
			Object providedObject = src.getPropertyValue(property.getName());
			Transient fieldsDeclaredAnnotation = property.getAnnotation(Transient.class);
			if (providedObject != null && !(Collection.class.isAssignableFrom(property.getType())) && fieldsDeclaredAnnotation == null) {
				if (property.getType() == String.class && !isEmptyString(String.valueOf(providedObject))) {
					result.add(new SearchCriteria(property.getName(), SearchOperation.MATCH, providedObject));
				} else if (property.getType() == Date.class) {
					result.add(new SearchCriteria(property.getName(), SearchOperation.LESS_THAN_EQUAL, providedObject));
				} else if (property.getType() == int.class || property.getType() == Integer.class) {
					result.add(new SearchCriteria(property.getName(), SearchOperation.EQUAL, providedObject));
				} else if (property.getType() == long.class || property.getType() == Long.class) {
					result.add(new SearchCriteria(property.getName(), SearchOperation.EQUAL, providedObject));
				} else if (property.getType() == boolean.class || property.getType() == Boolean.class) {
					result.add(new SearchCriteria(property.getName(), SearchOperation.EQUAL, providedObject));
				}
			}
		}
		return result;
	}

	public static <T> List<SearchCriteria> prepareSubQuerySpecification(T target, String lowerBound,
			String upperBound) {

		List<SearchCriteria> result = new ArrayList<>();
		final BeanWrapper trg = new BeanWrapperImpl(target);
		long lowerRange = (long) trg.getPropertyValue(lowerBound);
		long upperRange = (long) trg.getPropertyValue(upperBound);

		try {
			if (lowerRange >= upperRange) {
				result.add(new SearchCriteria(target.getClass().getDeclaredField(lowerBound).getName(),
						SearchOperation.LESS_THAN_EQUAL, lowerRange));
			}
			if (upperRange > lowerRange) {
				String value = lowerRange + ":" + upperRange;
				result.add(new SearchCriteria(target.getClass().getDeclaredField(lowerBound).getName(),
						SearchOperation.BETWEEN, value));
			}
		} catch (NoSuchFieldException | SecurityException e) {
			log.error("Error occured: {}", e);
		}

		return result;
	}
	
	public static Notification setUpNotification(StockBroker stockBroker, User user) {

		return new Notification(stockBroker.getStockBrokerName(), user.getFirstName(),
				Utils.commaSeperatedToList(stockBroker.getPhoneNumbers()),
				Utils.commaSeperatedToList(stockBroker.getEmails()), Utils.commaSeperatedToList(user.getPhone()),
				Utils.commaSeperatedToList(user.getEmail()));
	}
	
	public static boolean isValidDate(String format, String date) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		try {
			LocalDateTime ldt = LocalDateTime.parse(date, formatter);
			String result = ldt.format(formatter);
			
			return result.equals(date);
		}catch(DateTimeParseException ex) {
			try {
				LocalDate ldt = LocalDate.parse(date, formatter);
				String result = ldt.format(formatter);
				
				return result.equals(date);
			}catch(DateTimeParseException exp) {
				try {
					LocalTime ldt = LocalTime.parse(date, formatter);
					String result = ldt.format(formatter);
					
					return result.equals(date);
				}catch(DateTimeParseException exp1) {
					log.info("Unable to parse date: {}. Error: {}", date, exp1);
				}
			}
		}
		return false;
	}
	
	public static Date getDate(String format, String input) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormat.parse(input);
		}catch(ParseException ex) {
			log.info("Unable to parse date: {}, Error: {}", input, ex);
		}
		return date;
	}
	
	public static Calendar getEndOfDayTime(int hr, int min, int sec, int milSec) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hr);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, milSec);

		return cal;
	}
	
	public static String capitalizeFirstLetter(String input) {
		if (input == null || input.length() == 0) {
	        return input;
	    }
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}
}
