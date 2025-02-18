package com.example.ASM1.Entity;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getter cho product
    public Product getProduct() {
        return product;
    }

    // Setter cho product (nếu cần)
    public void setProduct(Product product) {
        this.product = product;
    }

    // Getter cho quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter cho quantity (nếu cần)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
