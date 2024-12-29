package org.beanmaker.v2.runtime;

import java.util.List;

public class ErrorMessage extends PlatformMessage {

	public ErrorMessage(long beanId, String fieldName, String fieldLabel, String message) {
		super(beanId, fieldName, fieldLabel, message);
	}

    public ErrorMessage(long beanId, String message) {
        super(beanId, message);
    }

    public static String toJson(List<ErrorMessage> errorMessages) {
        return PlatformMessage.toJson(errorMessages, "errors");
    }

}
