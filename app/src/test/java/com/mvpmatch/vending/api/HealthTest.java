package com.mvpmatch.vending.api;

import com.mvpmatch.vending.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

public class HealthTest extends AbstractIntegrationTest {

    @Test
    void shouldBeHealthy() {
        given(requestSpecification)
                .when()
                .get("/health")
                .then()
                .statusCode(200);
    }
}
