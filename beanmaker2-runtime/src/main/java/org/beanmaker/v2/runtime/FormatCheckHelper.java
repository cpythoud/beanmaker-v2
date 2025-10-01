package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.DecimalValueParser;
import org.beanmaker.v2.util.EmailValidator;
import org.beanmaker.v2.util.SimpleInputDateFormat;

import java.util.regex.Pattern;

public class FormatCheckHelper {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final SimpleInputDateFormat ISO_DATE_FORMAT =
            new SimpleInputDateFormat(SimpleInputDateFormat.ElementOrder.YYMD, "-");

    @Deprecated
    public static boolean isNumber(String s) {
		return NUMBER_PATTERN.matcher(s).matches();
	}

    @Deprecated
	public static boolean isValidIsoDate(String s) {
		return ISO_DATE_FORMAT.validate(s);
	}

    @Deprecated
	public static boolean isEmailValid(String s) {
		return EmailValidator.validate(s, true, true);
	}
	
	public static boolean isIntNumber(String number) {
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException nfex) {
			return false;
		}

		return true;
	}

	public static boolean isLongNumber(String number) {
		try {
			Long.parseLong(number);
		} catch (NumberFormatException nfex) {
			return false;
		}

		return true;
	}

    public static boolean isNumber(String number, DecimalValueParser parser) {
        return isNumber(number, parser, false);
    }

    public static boolean isNumber(String number, DecimalValueParser parser, boolean positiveOnly) {
        try {
            var value = parser.parse(number);
            if (positiveOnly && value.isNegative())
                return false;
        } catch (NumberFormatException nfex) {
            return false;
        }

        return true;
    }

}
