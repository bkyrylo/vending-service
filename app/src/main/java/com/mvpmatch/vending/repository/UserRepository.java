package com.mvpmatch.vending.repository;

import com.mvpmatch.vending.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where u.username = :username")
    User findByUsername(@Param("username") String username);

    @Query("select u from User u left join fetch u.products where u.id = :id")
    Optional<User> findByIdWithProducts(@Param("id") UUID id);

    @Query("select u from User u left join fetch u.deposits where u.id = :id")
    Optional<User> findByIdWithDeposits(@Param("id") UUID id);
}