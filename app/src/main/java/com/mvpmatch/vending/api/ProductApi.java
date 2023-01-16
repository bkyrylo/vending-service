package com.mvpmatch.vending.api;

import com.mvpmatch.vending.api.dto.CreateUpdateProductRequest;
import com.mvpmatch.vending.api.dto.DepositRequest;
import com.mvpmatch.vending.api.dto.ProductResponse;
import com.mvpmatch.vending.api.dto.PurchaseRequest;
import com.mvpmatch.vending.api.dto.PurchaseResponse;
import com.mvpmatch.vending.api.dto.UserResponse;
import com.mvpmatch.vending.api.validation.AllowedProductCost;
import com.mvpmatch.vending.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductApi {

    private final ProductService productService;

    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> createProduct(
            @AuthenticationPrincipal(expression = "id") UUID sellerId,
            @Valid @RequestBody CreateUpdateProductRequest createProductRequest
    ) {
        return productService.createProduct(CreateUpdateProductRequest.asProduct(createProductRequest), sellerId)
                          .map(ProductResponse::from)
                          .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> updateProduct(
            @AuthenticationPrincipal(expression = "id") UUID sellerId,
            @PathVariable("id") @NotNull @Pattern(regexp = "^[0-9a-zA-Z\\-]{1,64}$") String productId,
            @Valid @RequestBody CreateUpdateProductRequest updateProductRequest
    ) {
        var product = CreateUpdateProductRequest.asProduct(updateProductRequest);
        product.setId(UUID.fromString(productId));
        return productService.updateProduct(product, sellerId)
                             .map(ProductResponse::from)
                             .map(ResponseEntity::ok);
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(
            @AuthenticationPrincipal(expression = "id") UUID sellerId,
            @PathVariable("id") @NotNull @Pattern(regexp = "^[0-9a-zA-Z\\-]{1,64}$") String productId
    ) {
        return productService.deleteProduct(UUID.fromString(productId), sellerId)
                          .thenReturn(ResponseEntity.accepted().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> getProduct(
            @PathVariable("id") @NotNull @Pattern(regexp = "^[0-9a-zA-Z\\-]{1,64}$") String productId
    ) {
        return productService.findProduct(UUID.fromString(productId))
                             .map(ProductResponse::from)
                             .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/buy")
    public Mono<ResponseEntity<PurchaseResponse>> buyProduct(@AuthenticationPrincipal(expression = "id") UUID buyerId,
                                                             @PathVariable("id") @NotNull @Pattern(regexp = "^[0-9a-zA-Z\\-]{1,64}$") String productId,
                                                             @Valid @RequestBody PurchaseRequest purchaseRequest) {
        return productService.buyProduct(UUID.fromString(productId), purchaseRequest.getAmount(), buyerId)
                             .map(ResponseEntity::ok);
    }
}
