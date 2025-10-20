package com.order.dto;

import com.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class OrderEvent {

    private UUID orderId;
    private UUID customerId;
    private String  itemJson;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private OffsetDateTime eventTime;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getItemJson() {
        return itemJson;
    }

    public void setItemJson(String itemJson) {
        this.itemJson = itemJson;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OffsetDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(OffsetDateTime eventTime) {
        this.eventTime = eventTime;
    }
    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", itemJson='" + itemJson + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", eventTime=" + eventTime +
                '}';
    }
}
