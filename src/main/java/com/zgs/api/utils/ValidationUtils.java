package com.zgs.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by simon on 16-1-22.
 */
public class ValidationUtils {
    /**
     * @param email
     * @return validate email format
     */
    public static boolean isEmail(String email) {
        if (null == email || email.length() == 0) return false;

        return Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*").matcher(email).matches();
    }

    /**
     * @param number
     * @return validate mobile number
     */
    public static boolean isMobile(String number) {
        if (number == null || number.length() == 0) return false;

        return Pattern.compile("^[1][0-9][0-9]{9}$").matcher(number).matches();
    }

    /**
     * @param number
     * @return validate telephone number
     */
    public static boolean isTelephone(String number) {
        if (number == null || number.length() == 0) return false;

        boolean result;
        result = Pattern.compile("^[1][0-9][0-9]{9}$").matcher(number).matches();

        if (!result) {
            result = Pattern.compile("([0-9]{3,4})?[0-9]{7,8}").matcher(number).matches();
        }

        return result;
    }

    /**
     * validate number string
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * validate date
     *
     * @param str
     * @return
     */
    public static boolean isDate(String str) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param card
     * @return validate id card
     */
    public static boolean isIDCard(String card) {
        String result = IDCardUtils.validateString(card);

        if (result == null || result.length() == 0) {
            return true;
        }

        LogUtils.error(result);
        return false;
    }
}
