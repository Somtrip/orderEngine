package com.som.orderengine.domain;



import com.som.orderengine.events.BaseEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private List<ItemLine> items = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private OrderStatus status = OrderStatus.PENDING;

    private BigDecimal amountPaid = BigDecimal.ZERO;
    private LocalDate shippingDate; // optional
    private final List<BaseEvent> eventHistory = new ArrayList<>();

    public Order() {}

    public Order(String orderId, String customerId, List<ItemLine> items, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        if (items != null) this.items = items;
        if (totalAmount != null) this.totalAmount = totalAmount;
    }

    public void addEvent(BaseEvent e) {
        eventHistory.add(e);
    }

    // getters/setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public List<ItemLine> getItems() { return items; }
    public void setItems(List<ItemLine> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public LocalDate getShippingDate() { return shippingDate; }
    public void setShippingDate(LocalDate shippingDate) { this.shippingDate = shippingDate; }

    public List<BaseEvent> getEventHistory() { return eventHistory; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", items=" + (items == null ? 0 : items.size()) +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", amountPaid=" + amountPaid +
                ", shippingDate=" + shippingDate +
                ", events=" + eventHistory.size() +
                '}';
    }
}

