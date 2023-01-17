package com.mvpmatch.vending.service;

import com.mvpmatch.vending.api.dto.PurchaseResponse;
import com.mvpmatch.vending.entity.Deposit;
import com.mvpmatch.vending.entity.Product;
import com.mvpmatch.vending.entity.User;
import com.mvpmatch.vending.exception.ChangeReturnException;
import com.mvpmatch.vending.exception.ProductAmountUnavailableException;
import com.mvpmatch.vending.exception.ProductNotFoundException;
import com.mvpmatch.vending.exception.UserNotEnoughFundsException;
import com.mvpmatch.vending.exception.UserNotFoundException;
import com.mvpmatch.vending.repository.ProductRepository;
import com.mvpmatch.vending.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @Transactional
    public Mono<Product> createProduct(Product product, UUID sellerId) {
        return Mono.fromSupplier(() -> userRepository.findByIdWithProducts(sellerId))
                   .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                   .switchIfEmpty(Mono.error(new UserNotFoundException("Seller not found")))
                   .map(seller -> {   //
                       seller.addProduct(product);
                       return productRepository.save(product);
                   });
    }

    public Mono<Product> findProduct(UUID id) {
        return Mono.fromSupplier(() -> productRepository.findById(id))
                   .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                   .switchIfEmpty(Mono.error(new ProductNotFoundException()));
    }

    @Transactional
    public Mono<Product> updateProduct(Product updatedProduct, UUID sellerId) {
        return Mono.fromSupplier(() -> productRepository.findById(updatedProduct.getId()))
                   .handle((result, sink) -> {
                       try {
                           result.ifPresentOrElse(product -> {
                               var seller = product.getSeller();
                               if (sellerId.equals(seller.getId())) {
                                   updatedProduct.setSeller(seller);
                                   sink.next(productRepository.save(updatedProduct));
                               } else {
                                   throw new ProductNotFoundException();
                               }
                           }, () -> sink.error(new ProductNotFoundException()));
                       } catch (Exception e) {
                           sink.error(e);
                       }
                   });
    }

    @Transactional
    public Mono<Void> deleteProduct(UUID id, UUID sellerId) {
        return Mono.fromSupplier(() -> productRepository.findById(id))
                   .handle((result, sink) -> {
                       try {
                           result.ifPresentOrElse(product -> {
                               var seller = product.getSeller();
                               if (sellerId.equals(seller.getId())) {
                                   seller.removeProduct(product);
                                   productRepository.delete(product);
                                   sink.complete();
                               } else {
                                   throw new ProductNotFoundException();
                               }
                           }, () -> sink.error(new ProductNotFoundException()));
                       } catch (Exception e) {
                           sink.error(e);
                       }
                   })
                   .then();
    }

    @Transactional
    public Mono<PurchaseResponse> buyProduct(UUID id, int amountToBuy, UUID buyerId) {   //TODO beautify!!!
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);

        int amountAvailable = product.getAmountAvailable();
        if (amountAvailable < amountToBuy) {
            throw new ProductAmountUnavailableException();
        }

        int totalCost = product.getCost() * amountToBuy;

        User user = userRepository.findByIdWithDeposits(buyerId).orElseThrow(UserNotFoundException::new);
        int totalDepositSum = user.getDeposits().stream().map(Deposit::getCents).reduce(0, Integer::sum);
        if (totalCost > totalDepositSum) {
            throw new UserNotEnoughFundsException();
        } else if (totalCost == totalDepositSum) {
            product.setAmountAvailable(amountAvailable - amountToBuy);
            productRepository.save(product);

            user.removeAllDeposits();
            userRepository.save(user);

            return Mono.just(PurchaseResponse.builder()
                                             .productName(product.getProductName())
                                             .spentInCents(totalCost)
                                             .change(List.of())
                                             .build());
        } else {
            // Find the coin change combination
            List<List<Deposit>> depositSubsets = new ArrayList<>();
            var changeInCents = totalDepositSum - totalCost;
            subsetSumOfDeposits(
                    new ArrayList<>(user.getDeposits()),
                    changeInCents,
                    new ArrayList<>(),
                    depositSubsets
            );

            if (!depositSubsets.isEmpty()) {
                List<Integer> changeCoins =  depositSubsets.get(0).stream().map(Deposit::getCents).collect(Collectors.toList());

                product.setAmountAvailable(amountAvailable - amountToBuy);
                productRepository.save(product);

                user.removeAllDeposits();
                userRepository.save(user);

                return Mono.just(PurchaseResponse.builder()
                                                 .productName(product.getProductName())
                                                 .spentInCents(totalCost)
                                                 .change(changeCoins)
                                                 .build());
            } else {
                throw new ChangeReturnException();
            }
        }

    }

    /*
     * Algorithm to find subsets of a set for a given sum.
     * deposits - a list of deposits, each one is a deposited coin of 5, 10, 20, 50, 100 cents;
     * targetSum - the amount of change in cents we would like to obtain deposit subsets for;
     * partial - partial list, needed for the algorithm;
     * depositSubsets - resulting list of possible deposits(coins) combinations. This is cents change coin combination.
     */
    private void subsetSumOfDeposits(List<Deposit> deposits, int targetSum, List<Deposit> partial, List<List<Deposit>> depositSubsets) {
        int sum = partial.stream().map(Deposit::getCents).reduce(0, Integer::sum);

        if (sum == targetSum) {
            depositSubsets.add(partial);
        } else if (sum < targetSum) {
            for (int i = 0; i < deposits.size(); i++) {
                Deposit deposit = deposits.get(i);

                List<Deposit> remaining = deposits.subList(i + 1, deposits.size());
                List<Deposit> newPartial = new ArrayList<>(partial);
                newPartial.add(deposit);
                subsetSumOfDeposits(remaining, targetSum, newPartial, depositSubsets);
            }
        }
    }
}
