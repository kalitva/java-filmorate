package ru.yandex.practicum.filmorate.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AfterValidator implements ConstraintValidator<After, LocalDate> {
    private LocalDate date;

    @Override
    public void initialize(After constraintAnnotation) {
        try {
            date = LocalDate.parse(constraintAnnotation.value(), DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("date value should be in iso format");
        }
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.isAfter(date);
    }
}
