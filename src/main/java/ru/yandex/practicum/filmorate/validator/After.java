package ru.yandex.practicum.filmorate.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterValidator.class)
@Documented
public @interface After {
    /**
     * date in ISO format
     */
    String value();

    String message() default "date is not after ${value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
