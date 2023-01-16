package com.mvpmatch.vending.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name="deposit", schema = "public")
public class Deposit {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer cents;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}