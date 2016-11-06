package com.smargav.api.validator.validator;

import android.content.Context;
import android.text.TextUtils;

import com.smargav.api.R;
import com.smargav.api.validator.AbstractValidator;

public class NotEmptyValidator extends AbstractValidator {

    private static final int DEFAULT_ERROR_MESSAGE_RESOURCE = R.string.validator_empty;

    public NotEmptyValidator(Context c) {
        super(c, DEFAULT_ERROR_MESSAGE_RESOURCE);
    }

    public NotEmptyValidator(Context c, int errorMessage) {
        super(c, errorMessage);
    }

    @Override
    public boolean isValid(String text) {
        if (text != null) {
            text = text.trim();
        }
        return !TextUtils.isEmpty(text);
    }
}
