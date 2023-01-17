package com.mvpmatch.vending.api;

import com.mvpmatch.vending.api.dto.UserResponse;
import com.mvpmatch.vending.repository.UserRepository;
import com.mvpmatch.vending.support.AbstractIntegrationTest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


class UserApiTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAllById(users);
        users.clear();
    }

    private static Stream<Arguments> provideRoleNames() {
        return Stream.of(
                Arguments.of(List.of("buyer")),
                Arguments.of(List.of("seller")),
                Arguments.of(Arrays.asList("buyer", "seller"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideRoleNames")
    void shouldCreateUserWithRolesSuccessfully(List<String> roleNames) {
        var username = "testUser" + UUID.randomUUID();
        var userResponse =  given(requestSpecification)
                .basePath("/user")
                .body(Map.of(
                        "username", username,
                        "password", "testpassword",
                        "roles", roleNames
                ))
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getRoleNames()).containsExactlyInAnyOrderElementsOf(roleNames);

        users.add(userResponse.getId());
    }

    @Test
    void shouldFailOnCreationUserWithIncorrectRoles() {
        given(requestSpecification)
                .basePath("/user")
                .body(Map.of(
                        "username", "testUser" + UUID.randomUUID(),
                        "password", "testpassword",
                        "roles", List.of("WRONG", "buyer")
                ))
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void shouldFailOnCreationUserWithExistingUsername() {
        var username = "testUser" + UUID.randomUUID();

        createTestUser(username, "testpassword", List.of("buyer"));

        given(requestSpecification)
                .basePath("/user")
                .body(Map.of("username", username, "password", "testpassword", "roles", List.of("buyer")))
                .post()
                .then()
                .statusCode(409);
    }

    @Test
    void shouldFailOnUpdateWhenNonAuthorized() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        given(requestSpecification)
                .basePath("/user")
                .body(Map.of(
                        "password", "newpassword",
                        "roles", List.of("buyer", "seller")
                ))
                .put()
                .then()
                .statusCode(401);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        var roles = List.of("buyer", "seller");
        var updatedUserResponse =  given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user")
                .body(Map.of(
                        "password", "newpassword",
                        "roles", roles
                ))
                .put()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(updatedUserResponse).isNotNull();
        assertThat(updatedUserResponse.getId()).isNotNull();
        assertThat(updatedUserResponse.getRoleNames()).containsExactlyInAnyOrderElementsOf(roles);

        users.add(updatedUserResponse.getId());
    }

    @Test
    void shouldFailOnUpdateRemoveSellerWhenTheyHaveProducts() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("seller","buyer"));
        createTestProduct(username, password, "Toy Laser", 25, 10);

        var roles = List.of("buyer");
        var updatedUserResponse =  given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user")
                .body(Map.of(
                        "password", "newpassword",
                        "roles", roles
                ))
                .put()
                .then()
                .statusCode(409);
    }

    @Test
    void shouldFailOnUpdateWhenWrongRolesListSize() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user")
                .body(Map.of(
                        "password", "newpassword",
                        "roles", List.of()
                ))
                .put()
                .then()
                .statusCode(400);
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user")
                .delete()
                .then()
                .statusCode(202);
    }

    @Test
    void shouldFailOnDeleteWhenNonAuthorized() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        given(requestSpecification)
                .basePath("/user")
                .delete()
                .then()
                .statusCode(401);
    }

    @Test
    void shouldFindUserSuccessfully() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("seller"));

        var userResponse = given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user")
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getRoleNames()).contains("seller");
        assertThat(userResponse.getDeposits()).isEmpty();
    }

    @Test
    void shouldFailOnFindWhenNonAuthorized() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("seller"));

        var user = given(requestSpecification)
                .basePath("/user")
                .get()
                .then()
                .statusCode(401);
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20, 50, 100})
    void shouldDepositUserSuccessfully(int depositInCents) {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        var userResponse = given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user/deposit")
                .body(Map.of(
                        "depositInCents", depositInCents
                ))
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getRoleNames()).contains("buyer");
        assertThat(userResponse.getDeposits()).containsExactly(depositInCents);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 101})
    void shouldFailOnDepositUserWhenDepositValueNotAllowed(int depositInCents) {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        var userResponse = given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user/deposit")
                .body(Map.of(
                        "depositInCents", depositInCents
                ))
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void shouldDepositUserRepeatedly() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        List<Integer> deposits = List.of(20, 10, 5);
        createTestUser(username, password, List.of("buyer"));
        depositTestUser(username, password, deposits.get(0));
        depositTestUser(username, password, deposits.get(1));

        var userResponse = given(requestSpecification)
                .auth()
                .basic(username, password)
                .basePath("/user/deposit")
                .body(Map.of(
                        "depositInCents", deposits.get(2)
                ))
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getRoleNames()).contains("buyer");
        assertThat(userResponse.getDeposits()).containsExactlyInAnyOrderElementsOf(deposits);
    }

    @Test
    void shouldFailOnDepositUserWhenNonAuthorized() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("buyer"));

        given(requestSpecification)
                .basePath("/user/deposit")
                .body(Map.of(
                        "depositInCents", 5
                ))
                .post()
                .then()
                .statusCode(401);
    }

    @Test
    void shouldFailOnDepositUserWithWrongRole() {
        var username = "testUser" + UUID.randomUUID();
        var password = "testpassword";

        createTestUser(username, password, List.of("seller"));

        given(requestSpecification)
                .basePath("/user/deposit")
                .body(Map.of(
                        "depositInCents", 5
                ))
                .post()
                .then()
                .statusCode(401);
    }

}