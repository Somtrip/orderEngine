package com.som.orderengine.observers;


import com.som.orderengine.domain.Order;
import com.som.orderengine.domain.OrderStatus;

public class AlertObserver implements OrderObserver {  

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        System.out.printf("[ALERT] Sending alert for Order %s: Status changed to %s%n",
                order.getOrderId(), newStatus);
    }
}

