package com.ap.greenpole.usermodule.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 14-Jun-20 10:05 PM
 */
@Converter(autoApply = true)
public class BooleanToIntConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final Boolean booleanValue) {
        return (booleanValue ? 1 : 0);
    }

    @Override
    public Boolean convertToEntityAttribute(final Integer dbData) {
        return (dbData == 1);
    }
}
