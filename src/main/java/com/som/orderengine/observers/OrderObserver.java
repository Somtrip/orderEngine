package com.som.orderengine.observers;

import com.som.orderengine.domain.Order;
import com.som.orderengine.domain.OrderStatus;
import com.som.orderengine.events.BaseEvent;

public interface OrderObserver {
    default void onEventProcessed(BaseEvent event, Order order) {}
    default void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {}
}
