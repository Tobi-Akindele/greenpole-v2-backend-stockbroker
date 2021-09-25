package com.ap.greenpole.usermodule.model;

import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 28-Aug-20 05:15 AM
 */
public class MultipleValuesRequest {

    long userId;

    List<String> values;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}
