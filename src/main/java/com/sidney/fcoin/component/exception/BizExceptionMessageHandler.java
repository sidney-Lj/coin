package com.sidney.fcoin.component.exception;


import com.google.common.base.Joiner;
import com.sidney.fcoin.component.utils.ApplicationContextHelper;
import com.sidney.fcoin.component.utils.ReloadableMessageSource;
import org.apache.commons.lang3.StringUtils;

public class BizExceptionMessageHandler {
    private static ReloadableMessageSource messageSource;

    public static String getMessage(String errorCode, Object... args) {
        if (messageSource == null) {
            messageSource = ApplicationContextHelper.getBean(ReloadableMessageSource.class);
        }
        String message = messageSource.getMessage(errorCode, args);
        if (StringUtils.isBlank(message)) {
            StringBuilder arg = new StringBuilder("");
            if (args != null) {
                for (Object ag : args) {
                    arg.append(ag.toString());
                }
            }
            message = Joiner.on(" ").join("☺", arg.toString());
        }
        return message;
    }

}
