package com.n26.validation;

import com.n26.validation.impl.FieldsValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FieldsValidator.class)
public @interface Fields {
    String message() default "{field}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}