package com.project.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Payload;

@jakarta.validation.Constraint(validatedBy = AgeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Age must be between 18 and 65 years old";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
