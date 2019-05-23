package com.example.deliveryman;


public class CartInfo {
    //Property name must be the same as what we defined in real time database
    private String customerId, customerName, customerImage;
    private String restaurantId, restaurantName, restaurantImage, restaurantComment;
    private String riderId, riderName, riderImage;
    private String status, orderedId;
    private String totalPrice, totalItems;

    public CartInfo() {
        //Constructor , it is needed
    }

    public CartInfo(String status, String orderedId
            , String riderId, String riderName, String riderImage
            , String customerId, String customerName, String customerImage
            , String restaurantId, String restaurantName, String restaurantImage, String restaurantComment
            , String totalItems, String totalPrice) {

        this.restaurantId = restaurantId;
        this.restaurantImage = restaurantImage;
        this.restaurantName = restaurantName;
        this.riderId = riderId;
        this.riderImage = riderImage;
        this.riderName = riderName;
        this.restaurantComment = restaurantComment;
        this.customerId = customerId;
        this.customerImage = customerImage;
        this.customerName = customerName;
        this.status = status;
        this.orderedId = orderedId;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;

    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderImage() {
        return riderImage;
    }

    public void setRiderImage(String riderImage) {
        this.riderImage = riderImage;
    }

    public String getRestaurantComment() {
        return restaurantComment;
    }

    public void setRestaurantComment(String restaurantComment) {
        this.restaurantComment = restaurantComment;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
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

    public String getRestaurantId() {
        return restaurantId;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}