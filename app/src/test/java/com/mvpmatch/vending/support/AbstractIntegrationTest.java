package com.mvpmatch.vending.support;

import com.mvpmatch.vending.Application;
import com.mvpmatch.vending.api.dto.ProductResponse;
import com.mvpmatch.vending.api.dto.UserResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import static io.restassured.RestAssured.given;

@SpringBootTest(
        classes = {
                Application.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Slf4j
public abstract class AbstractIntegrationTest {
    static {
        var postgresql = new PostgreSQLContainer<>("postgres:14.4-alpine");
        Startables.deepStart(List.of(postgresql)).join();
        System.getProperties().putAll(Map.of(
                "spring.datasource.url", postgresql.getJdbcUrl(),
                "spring.datasource.username", postgresql.getUsername(),
                "spring.datasource.password", postgresql.getPassword(),
                "spring.flyway.url", postgresql.getJdbcUrl(),
                "spring.flyway.username", postgresql.getUsername(),
                "spring.flyway.password", postgresql.getPassword()
        ));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    protected RequestSpecification requestSpecification;

    @LocalServerPort
    protected int port;

    protected List<UUID> users = new ArrayList<>();

    @BeforeEach
    public void setUpAbstractIntegrationTest() {
        requestSpecification = new RequestSpecBuilder()
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
    }

    protected UUID depositTestUser(String username, String password, int depositInCents) {
        var userResponse =  given(requestSpecification)
                .auth()
                .basic(username, password)
                .when()
                .basePath("/user/deposit")
                .body(Map.of(
                        "depositInCents", depositInCents
                ))
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        return userResponse.getId();
    }

    protected UUID createTestUser(String username, String password, List<String> roleNames) {
        var userResponse =  given(requestSpecification)
                .when()
                .basePath("/user")
                .body(Map.of(
                        "username", username,
                        "password", password,
                        "roles", roleNames
                ))
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        users.add(userResponse.getId());
        return userResponse.getId();
    }

    protected UUID createTestProduct(String username, String password, String productName, int cost, int amountAvailable) {
        var productResponse =  given(requestSpecification)
                .auth()
                .basic(username, password)
                .when()
                .basePath("/product")
                .body(Map.of(
                        "productName", productName,
                        "cost", cost,
                        "amountAvailable", amountAvailable
                ))
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        return productResponse.getId();
    }
}