package com.mvpmatch.vending.service;

import com.mvpmatch.vending.entity.Deposit;
import com.mvpmatch.vending.entity.Role;
import com.mvpmatch.vending.entity.User;
import com.mvpmatch.vending.exception.UserAlreadyExistsException;
import com.mvpmatch.vending.exception.UserNotFoundException;
import com.mvpmatch.vending.exception.UserUpdateException;
import com.mvpmatch.vending.repository.RoleRepository;
import com.mvpmatch.vending.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.defer(() ->  Mono.justOrEmpty(userRepository.findByUsername(username)))
                .cast(UserDetails.class);
    }

    @Transactional
    public Mono<User> createUser(User user, List<String> roleNames) {
        return Mono.fromSupplier(() -> {
            roleRepository.findByRoleNames(roleNames).forEach(user::addRole);
            return user;
        })
                .<User>handle((result, sink) -> {
                    try {
                        if (userRepository.findByUsername(result.getUsername()) != null) {
                            sink.error(new UserAlreadyExistsException());
                        } else {
                            sink.complete();
                        }
                    } catch (Exception e) {
                        sink.error(e);
                    }
                })
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    user.setPassword(encoder.encode(user.getPassword()));
                    return userRepository.save(user);
                }));
    }

    public Mono<Void> deleteUser(UUID id) {
        return Mono.fromRunnable(() -> userRepository.deleteById(id))
                   .onErrorMap(e -> new UserNotFoundException())
                   .then();
    }

    public Mono<User> findUser(UUID id) {
        return Mono.fromSupplier(() -> userRepository.findById(id))
                   .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                   .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Transactional
    public Mono<User> resetUserDeposit(UUID id) {
        return Mono.fromSupplier(() -> userRepository.findByIdWithDeposits(id))
            .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
            .switchIfEmpty(Mono.error(new UserNotFoundException()))
            .map(user -> {   //
                user.removeAllDeposits();
                return userRepository.save(user);
            });
    }

    @Transactional
    public Mono<User> depositCents(UUID id, int cents) {
        return Mono.fromSupplier(() -> userRepository.findByIdWithDeposits(id))
                   .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                   .switchIfEmpty(Mono.error(new UserNotFoundException()))
                   .map(user -> {   //
                       user.addDeposit(Deposit.builder().cents(cents).build());
                       return userRepository.save(user);
                   });
    }

    @Transactional
    public Mono<User> updateUser(UUID id, String password, List<String> roleNames) {
        if (!roleNames.contains("seller")) {
            return checkUpdateSellerRemoval(id, password, roleNames);
        } else {
            return Mono.fromSupplier(() -> userRepository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> {   //
                    Set<Role> roles = new HashSet<>(roleRepository.findByRoleNames(roleNames));
                    user.setRoles(roles);
                    user.setPassword(encoder.encode(password));
                    return userRepository.save(user);
                });
        }
    }

    private Mono<User> checkUpdateSellerRemoval(UUID id, String password, List<String> roleNames) {
        return Mono.fromSupplier(() -> userRepository.findByIdWithProducts(id))
                   .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                   .switchIfEmpty(Mono.error(new UserNotFoundException()))
                   .handle((userFound, sink) -> {
                       try {
                           if (userFound.getRoles().contains("seller") && !userFound.getProducts().isEmpty()) {
                               sink.error(new UserUpdateException("Cannot remove 'seller' role for the user with products"));
                           } else {
                               Set<Role> roles = new HashSet<>(roleRepository.findByRoleNames(roleNames));
                               userFound.setRoles(roles);
                               userFound.setPassword(encoder.encode(password));
                               sink.next(userRepository.save(userFound));
                           }
                       } catch (Exception e) {
                           sink.error(e);
                       }
                   });
    }
}
