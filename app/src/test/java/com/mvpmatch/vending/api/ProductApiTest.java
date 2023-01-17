package com.mvpmatch.vending.api;

import com.mvpmatch.vending.api.dto.PurchaseResponse;
import com.mvpmatch.vending.repository.ProductRepository;
import com.mvpmatch.vending.repository.UserRepository;
import com.mvpmatch.vending.support.AbstractIntegrationTest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class ProductApiTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAllById(users);
        users.clear();
    }

    @Test
    void shouldBePossibleToBuyProductSuccessfully() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 10;
        var productId = createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        List.of(20, 5).forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        var amount = 1;
        var purchaseResponse = given(requestSpecification)
                .auth()
                .basic(buyerUsername, buyerPassword)
                .body(Map.of(
                        "amount", amount
                ))
                .post("/product/{id}/buy", productId)
                .then()
                .statusCode(200)
                .extract()
                .as(PurchaseResponse.class);

        assertThat(purchaseResponse).isNotNull();
        assertThat(purchaseResponse.getProductName()).isEqualTo("Toy Laser");
        assertThat(purchaseResponse.getSpentInCents()).isEqualTo(productCost * amount);
        assertThat(purchaseResponse.getChange()).containsExactlyInAnyOrderElementsOf(List.of());

        assertThat(productRepository.findById(productId))
                .hasValueSatisfying(a -> assertThat(a.getAmountAvailable()).isEqualTo(initialAmount - amount));
    }

    @Test
    void shouldNotBePossibleToBuyProductIfNoCoinChangeAvailable() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 10;
        var productId = createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        List.of(20, 50).forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        var amount = 1;
        given(requestSpecification)
                .auth()
                .basic(buyerUsername, buyerPassword)
                .body(Map.of(
                        "amount", amount
                ))
                .post("/product/{id}/buy", productId)
                .then()
                .statusCode(409);
    }

    @Test
    void shouldBePossibleToBuyProductIfCoinChangeAvailable() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 10;
        var productId = createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        var deposits = List.of(20, 5, 5, 10, 50, 100);
        var totalDepositAmount = deposits.stream().reduce(0, Integer::sum);
        deposits.forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        var amount = 3;
        var purchaseResponse = given(requestSpecification)
                .auth()
                .basic(buyerUsername, buyerPassword)
                .body(Map.of(
                        "amount", amount
                ))
                .post("/product/{id}/buy", productId)
                .then()
                .statusCode(200)
                .extract()
                .as(PurchaseResponse.class);

        assertThat(purchaseResponse).isNotNull();
        assertThat(purchaseResponse.getProductName()).isEqualTo("Toy Laser");
        assertThat(purchaseResponse.getSpentInCents()).isEqualTo(productCost * amount);

        var changeAmount = purchaseResponse.getChange().stream().reduce(0, Integer::sum);
        assertThat(changeAmount).isEqualTo(totalDepositAmount - (productCost * amount));

        assertThat(productRepository.findById(productId))
                .hasValueSatisfying(a -> assertThat(a.getAmountAvailable()).isEqualTo(initialAmount - amount));
    }

    @Test
    void shouldNotBePossibleToBuyProductIfAmountExceedsAvailable() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 3;
        var productId = createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        List.of(50, 50).forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        var amount = 4;
        given(requestSpecification)
                .auth()
                .basic(buyerUsername, buyerPassword)
                .body(Map.of(
                        "amount", amount
                ))
                .post("/product/{id}/buy", productId)
                .then()
                .statusCode(409);
    }

    @Test
    void shouldFailToBuyIfProductNotFound() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 1;
        createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        List.of(20, 5).forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        given(requestSpecification)
                .auth()
                .basic(buyerUsername, buyerPassword)
                .body(Map.of(
                        "amount", 1
                ))
                .post("/product/{id}/buy", UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    void shouldFailToBuyIfNotEnoughMoney() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 2;
        var productId = createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        List.of(5).forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        given(requestSpecification)
                .auth()
                .basic(buyerUsername, buyerPassword)
                .body(Map.of(
                        "amount", 1
                ))
                .post("/product/{id}/buy", productId)
                .then()
                .statusCode(409);
    }

    @Test
    void shouldFailToBuyIfNotAuthorized() {
        var sellerUsername = "sellerUser" + UUID.randomUUID();
        var sellerPassword = "password1";
        createTestUser(sellerUsername, sellerPassword, List.of("seller"));

        var productCost = 25;
        var initialAmount = 10;
        var productId = createTestProduct(sellerUsername, sellerPassword, "Toy Laser", productCost, initialAmount);

        var buyerUsername = "buyerUser" + UUID.randomUUID();
        var buyerPassword = "password2";
        createTestUser(buyerUsername, buyerPassword, List.of("buyer"));

        List.of(20, 5).forEach(deposit -> depositTestUser(buyerUsername, buyerPassword, deposit));

        var amount = 1;
        given(requestSpecification)
                .body(Map.of(
                        "amount", amount
                ))
                .post("/product/{id}/buy", productId)
                .then()
                .statusCode(401);
    }

}