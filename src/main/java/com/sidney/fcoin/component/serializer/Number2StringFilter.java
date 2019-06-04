package com.sidney.fcoin.component.serializer;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.math.BigDecimal;

public class Number2StringFilter implements ValueFilter {

    @Override
    public Object process(Object object, String name, Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        if (value instanceof Number) {
            return value.toString();
        }
        return value;
    }

}