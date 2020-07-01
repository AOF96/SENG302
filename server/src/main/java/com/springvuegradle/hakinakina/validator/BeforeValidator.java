package com.springvuegradle.hakinakina.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

public class BeforeValidator implements ConstraintValidator<Before, Date> {

    private int years;

    @Override
    public void initialize(Before constraintAnnotation) {
        this.years = constraintAnnotation.years();
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1 * years);

        Date cutOffDate = calendar.getTime();
        return value.before(cutOffDate);

    }
}
