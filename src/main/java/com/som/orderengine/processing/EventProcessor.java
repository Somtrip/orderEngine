package com.som.orderengine.processing;

import com.som.orderengine.domain.Order;
import com.som.orderengine.domain.OrderStatus;
import com.som.orderengine.events.*;
import com.som.orderengine.observers.OrderObserver;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EventProcessor - processes events and keeps an in-memory order store.
 * Exposes addObserver(...) so external code (e.g. Application) can register observers.
 */
@Component
public class EventProcessor {

    private final Map<String, Order> store = new ConcurrentHashMap<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    // PUBLIC: allow registration of observers
    public void addObserver(OrderObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void process(BaseEvent event) {
        if (event == null) return;

        if (event instanceof OrderCreatedEvent oce) {
            handleOrderCreated(oce);
        } else if (event instanceof PaymentReceivedEvent pre) {
            handlePaymentReceived(pre);
        } else if (event instanceof ShippingScheduledEvent sse) {
            handleShippingScheduled(sse);
        } else if (event instanceof OrderCancelledEvent oce2) {
            handleOrderCancelled(oce2);
        } else {
            System.err.println("Unsupported event class: " + event.getClass().getSimpleName());
        }
    }

    private void handleOrderCreated(OrderCreatedEvent e) {
        if (store.containsKey(e.getOrderId())) {
            System.err.printf("Order %s already exists. Ignoring OrderCreatedEvent.%n", e.getOrderId());
            notifyEvent(e, store.get(e.getOrderId()));
            return;
        }
        Order order = new Order(e.getOrderId(), e.getCustomerId(), e.getItems(), e.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.addEvent(e);

        store.put(order.getOrderId(), order);
        notifyEvent(e, order);
    }

    private void handlePaymentReceived(PaymentReceivedEvent e) {
        Order order = getOrWarn(e.getOrderId(), e);
        if (order == null) return;

        if (isTerminal(order.getStatus())) {
            System.err.printf("Order %s is %s; payment ignored.%n", order.getOrderId(), order.getStatus());
            notifyEvent(e, order);
            return;
        }

        BigDecimal newPaid = order.getAmountPaid().add(nz(e.getAmountPaid()));
        order.setAmountPaid(newPaid);

        OrderStatus old = order.getStatus();
        OrderStatus next = computePaymentStatus(order.getTotalAmount(), newPaid);
        if (next != old) {
            order.setStatus(next);
            notifyStatus(order, old, next);
        }

        order.addEvent(e);
        notifyEvent(e, order);
    }

    private void handleShippingScheduled(ShippingScheduledEvent e) {
        Order order = getOrWarn(e.getOrderId(), e);
        if (order == null) return;

        if (order.getStatus() == OrderStatus.CANCELLED) {
            System.err.printf("Order %s is CANCELLED; cannot schedule shipping.%n", order.getOrderId());
            notifyEvent(e, order);
            return;
        }

        order.setShippingDate(e.getShippingDate());
        OrderStatus old = order.getStatus();
        order.setStatus(OrderStatus.SHIPPED);
        order.addEvent(e);

        if (old != OrderStatus.SHIPPED) {
            notifyStatus(order, old, OrderStatus.SHIPPED);
        }
        notifyEvent(e, order);
    }

    private void handleOrderCancelled(OrderCancelledEvent e) {
        Order order = getOrWarn(e.getOrderId(), e);
        if (order == null) return;

        if (order.getStatus() == OrderStatus.SHIPPED) {
            System.err.printf("Order %s already SHIPPED; cannot cancel.%n", order.getOrderId());
            notifyEvent(e, order);
            return;
        }

        OrderStatus old = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order.addEvent(e);

        if (old != OrderStatus.CANCELLED) {
            notifyStatus(order, old, OrderStatus.CANCELLED);
        }
        notifyEvent(e, order);
    }

    private Order getOrWarn(String orderId, BaseEvent e) {
        Order order = store.get(orderId);
        if (order == null) {
            System.err.printf("Order not found for event %s (%s).%n", e.getEventId(), e.getClass().getSimpleName());
        }
        return order;
    }

    private boolean isTerminal(OrderStatus s) {
        return s == OrderStatus.CANCELLED || s == OrderStatus.SHIPPED;
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private OrderStatus computePaymentStatus(BigDecimal total, BigDecimal paid) {
        if (paid == null) return OrderStatus.PENDING;
        if (total == null) total = BigDecimal.ZERO;
        if (paid.signum() == 0) return OrderStatus.PENDING;
        if (paid.compareTo(total) < 0) return OrderStatus.PARTIALLY_PAID;
        return OrderStatus.PAID;
    }

    private void notifyEvent(BaseEvent e, Order order) {
        for (OrderObserver obs : observers) {
            try {
                obs.onEventProcessed(e, order);
            } catch (Exception ex) {
                System.err.println("Observer failed in onEventProcessed: " + ex.getMessage());
            }
        }
    }

    private void notifyStatus(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        for (OrderObserver obs : observers) {
            try {
                obs.onStatusChanged(order, oldStatus, newStatus);
            } catch (Exception ex) {
                System.err.println("Observer failed in onStatusChanged: " + ex.getMessage());
            }
        }
    }

    public void printSummary() {
        store.values().stream()
                .sorted(Comparator.comparing(Order::getOrderId))
                .forEach(o -> System.out.println(" - " + o));
    }

    // === REST support ===
    public Order getOrder(String orderId) {
        return store.get(orderId);
    }

    public Collection<Order> getAllOrders() {
        return store.values();
    }
}
