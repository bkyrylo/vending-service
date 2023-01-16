package com.mvpmatch.vending.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, TYPE_USE})
@Constraint(validatedBy = AllowedRoles.AllowedRolesValidator.class)
public @interface AllowedRoles {

    String message() default "Allowed roles are 'buyer', 'seller'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Set<String> allowedRoles = Set.of("buyer", "seller");

    class AllowedRolesValidator implements ConstraintValidator<AllowedRoles, String> {

        @Override
        public boolean isValid(String roleName, ConstraintValidatorContext context) {
            return roleName != null && allowedRoles.contains(roleName);
        }
    }
}
