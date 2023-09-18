package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@SQLDelete(sql = "UPDATE cart_items SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Min(1)
    private int quantity;
    @Column(name = "is_deleted",nullable = false)
    private boolean isDeleted;
}