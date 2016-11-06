package com.smargav.api.validator.validator;

import android.content.Context;
import android.util.Patterns;

import com.smargav.api.R;
import com.smargav.api.validator.AbstractValidator;
import com.smargav.api.validator.ValidatorException;

import java.util.regex.Pattern;

/**
 * Validator to check if Phone number is correct. Created by throrin19 on
 * 13/06/13.
 */
public class PhoneValidator extends AbstractValidator {

    private static final Pattern PHONE_PATTERN = Patterns.PHONE;
    private static final int DEFAULT_ERROR_MESSAGE_RESOURCE = R.string.validator_phone;

    public PhoneValidator(Context c) {
        super(c, DEFAULT_ERROR_MESSAGE_RESOURCE);
    }

    public PhoneValidator(Context c, int errorMessageRes) {
        super(c, errorMessageRes);
    }

    @Override
    public boolean isValid(String phone) throws ValidatorException {
        /*if (phone != null) {
            phone = phone.replaceFirst("\\+?91", "");
		}*/
//		if((phone.length()<10)||(phone.length()>12)||(phone.length()>10)&&(phone.length()<12)) {
//			return false;
//		}


        //	return true;
        return phone != null && phone.length() == 10 && !phone.startsWith("0")
                && PHONE_PATTERN.matcher(phone).matches() && !Pattern.compile("0{10}|1{10}|2{10}|3{10}|4{10}|5{10}|6{10}|7{10}|8{10}|9{10}").matcher(phone).matches();
    }
}
