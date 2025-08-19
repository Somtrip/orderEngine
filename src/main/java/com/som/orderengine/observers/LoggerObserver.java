package com.som.orderengine.observers;


import com.som.orderengine.domain.Order;
import com.som.orderengine.domain.OrderStatus;
import com.som.orderengine.events.BaseEvent;

public class LoggerObserver implements OrderObserver {

    @Override
    public void onEventProcessed(BaseEvent event, Order order) {
        System.out.printf("[Logger] Event %s processed for Order %s%n",
                event.getEventType(), order.getOrderId());
    }

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        System.out.printf("[Logger] Order %s status changed from %s to %s%n",
                order.getOrderId(), oldStatus, newStatus);
    }
}

