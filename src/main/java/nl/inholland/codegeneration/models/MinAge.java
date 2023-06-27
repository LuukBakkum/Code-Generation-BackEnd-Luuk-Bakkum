package nl.inholland.codegeneration.models;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.inholland.codegeneration.configuration.MinAgeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinAgeValidator.class)
public @interface MinAge {
    String message() default "User must be at least 18 years old!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value();
}
