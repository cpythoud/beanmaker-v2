package org.beanmaker.v2.runtime;

import java.util.List;

public class WarningMessage extends PlatformMessage {

    public WarningMessage(long beanId, String fieldName, String fieldLabel, String message) {
        super(beanId, fieldName, fieldLabel, message);
    }

    public WarningMessage(long beanId, String message) {
        super(beanId, message);
    }

    public static String toJson(List<WarningMessage> warningMessages) {
        return PlatformMessage.toJson(warningMessages, "warnings");
    }

}
