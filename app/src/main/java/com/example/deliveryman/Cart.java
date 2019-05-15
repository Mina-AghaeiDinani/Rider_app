package com.example.deliveryman;


public class Cart {
    //Property name must be the same as what we defined in real time database
    private String customerId, restaurantId, status,customerAddress,restaurantAddress,orderedId;

    public Cart() {
        //Constructor , it is needed
    }

    public Cart(String customerId, String restaurantId, String status, String customerAddress, String restaurantAddress, String orderedId) {
        this.customerAddress=customerAddress;
        this.restaurantAddress=restaurantAddress;
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.status=status;
        this.orderedId=orderedId;

    }

    public String getOrderedId() {
        return orderedId;
    }

    public void setOrderedId(String orderedId) {
        this.orderedId = orderedId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantId() {
        return restaurantId;
    }


    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}