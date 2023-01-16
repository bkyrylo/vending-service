package com.mvpmatch.vending.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD})
@Constraint(validatedBy = AllowedCoins.AllowedCoinsValidator.class)
public @interface AllowedCoins {

    String message() default "Allowed coins are 5, 10, 20, 50 and 100 cents";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Set<Integer> allowedCoins = Set.of(5, 10, 20, 50, 100);

    class AllowedCoinsValidator implements ConstraintValidator<AllowedCoins, Integer> {

        @Override
        public boolean isValid(Integer coinInCents, ConstraintValidatorContext context) {
            return coinInCents != null && allowedCoins.contains(coinInCents);
        }
    }
}
