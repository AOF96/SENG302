package com.springvuegradle.hakinakina.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = {BeforeValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented

/**
    Annotation to handle date validation
 */
public @interface Before {
    String message() default "You need to be at valid age";

    int years();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
