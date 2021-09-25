package com.ap.greenpole.stockbroker.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanUtils<T> {
	
	private static Logger log = LoggerFactory.getLogger(BeanUtils.class);

	public T copyNonNullProperties(T target, T in) {
        if (in == null || target == null || target.getClass() != in.getClass()) return null;

        final BeanWrapper src = new BeanWrapperImpl(in);
        final BeanWrapper trg = new BeanWrapperImpl(target);

        for (final Field property : target.getClass().getDeclaredFields()) {
            Object providedObject = src.getPropertyValue(property.getName());
            if (providedObject != null) {
                trg.setPropertyValue(
                        property.getName(),
                        providedObject);
            }
        }
        return target;
    }
	
	public T setPropertiesNull(T target, List<String> fieldNames) {

		if (target != null && fieldNames != null && !fieldNames.isEmpty()) {
			fieldNames.forEach(item -> {
				try {
					Field field = target.getClass().getDeclaredField(item);
					field.setAccessible(true);
					field.set(target, null);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					log.error("Error occurred while setting values {}", e);
				}
			});
		}
		return target;
	}
}
