package com.mvpmatch.vending.api.validation;

import com.mvpmatch.vending.api.dto.CreateUpdateProductRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD})
@Constraint(validatedBy = AllowedProductCost.AllowedProductCostValidator.class)
public @interface AllowedProductCost {

    String DEFAULT_VALIDATION_MESSAGE = "Product cost should be in multiplies of 5";

    String message() default DEFAULT_VALIDATION_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class AllowedProductCostValidator implements ConstraintValidator<AllowedProductCost, Integer> {

        @Override
        public boolean isValid(Integer productCostInCents, ConstraintValidatorContext context) {
            return productCostInCents != null && productCostInCents % 5 == 0;
        }
    }
}
