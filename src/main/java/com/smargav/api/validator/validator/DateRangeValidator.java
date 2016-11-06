package com.smargav.api.validator.validator;

import android.content.Context;
import android.text.TextUtils;

import com.smargav.api.R;
import com.smargav.api.validator.AbstractValidator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Validates whether the given value is between a range of values
 *
 * @author Dixon D'Cunha (Exikle)
 */
public class DateRangeValidator extends AbstractValidator {

    /**
     * The start of the range
     */
    final double START_RANGE;

    /**
     * The end of the range
     */
    final double END_RANGE;

    /**
     * The error Id from the string resource
     */
    private int mErrorMessage; // Your custom error message
    private String mErrorMessageStr; // Your custom error message
    private String dateFormat;
    /**
     * Default error message if none specified
     */
    private static final int DEFAULT_ERROR_MESSAGE_RESOURCE = R.string.validator_range;

    public DateRangeValidator(Context c, double start, double end) {
        super(c, DEFAULT_ERROR_MESSAGE_RESOURCE);
        START_RANGE = start;
        END_RANGE = end;

        mErrorMessage = DEFAULT_ERROR_MESSAGE_RESOURCE;
    }

    /**
     * @param c
     * @param start           of the range
     * @param end             of the range
     * @param errorMessageRes
     */
    public DateRangeValidator(Context c, double start, double end,
                              int errorMessageRes, String dateFormat) {
        super(c, errorMessageRes);
        mErrorMessage = errorMessageRes;
        this.dateFormat = dateFormat;
        START_RANGE = start;
        END_RANGE = end;
    }


    public DateRangeValidator(Context c, double start, double end,
                              String errorMessage) {
        super(c, DEFAULT_ERROR_MESSAGE_RESOURCE);
        mErrorMessageStr = errorMessage;
        START_RANGE = start;
        END_RANGE = end;
    }

    @Override
    public String getMessage() {

        if (!TextUtils.isEmpty(mErrorMessageStr)) {
            return mErrorMessageStr;
        }

        return getContext().getString(mErrorMessage);
    }

    /**
     * Checks is value is between given range
     *
     * @return true if between range; false if outside of range
     */
    @Override
    public boolean isValid(String value) {
        if (value != null && value.length() > 0) {
            double inputedSize = parseDate(value);
            return inputedSize >= START_RANGE
                    && inputedSize <= END_RANGE;
        } else
            return false;
    }

    private double parseDate(String value) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            Date dt = format.parse(value);
            //Double.parseDouble(value);
            return dt.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Long.MIN_VALUE;
    }
}