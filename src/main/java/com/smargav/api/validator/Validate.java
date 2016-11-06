package com.smargav.api.validator;

import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class Validate extends AbstractValidate {

    private Set<AbstractValidator> mValidators = new HashSet<AbstractValidator>();
    private TextView mSourceView;

    public Validate(TextView sourceView) {
	mSourceView = sourceView;
    }

    /**
     * Add a new validator for fields attached
     *
     * @param validator {@link AbstractValidator} : The validator to attach
     */
    public void addValidator(AbstractValidator validator) {
	mValidators.add(validator);
    }

    @Override
    public boolean isValid() {
	for (AbstractValidator validator : mValidators) {
	    try {
		if (!mSourceView.isEnabled()) {
		    return true;
		}
		if (!validator.isValid(mSourceView.getText().toString())) {
		    mSourceView.setError(validator.getMessage());
		    return false;
		}
	    } catch (ValidatorException e) {
		e.printStackTrace();
		mSourceView.setError(e.getMessage());
		return false;
	    }
	}
	mSourceView.setError(null);
	return true;
    }

    public TextView getSource() {
	return mSourceView;
    }

}
