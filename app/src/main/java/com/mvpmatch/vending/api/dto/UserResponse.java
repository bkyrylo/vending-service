package com.mvpmatch.vending.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvpmatch.vending.entity.Deposit;
import com.mvpmatch.vending.entity.Role;
import com.mvpmatch.vending.entity.User;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    UUID id;

    String username;

    @JsonProperty("roles")
    List<String> roleNames;

    List<Integer> deposits;

    public static UserResponse from(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleNames(user.getRoles() != null ?
                        user.getRoles()
                                .stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .deposits(user.getDeposits().stream().map(Deposit::getCents).collect(Collectors.toList()))
                .build();
    }
}
