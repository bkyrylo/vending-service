package com.mvpmatch.vending.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name="product", schema = "public")
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="product_name")
    private String productName;

    private Integer cost;

    @Column(name="amount")
    private Integer amountAvailable;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User seller;
}