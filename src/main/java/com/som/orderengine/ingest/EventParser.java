package com.som.orderengine.ingest;



import com.som.orderengine.events.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class EventParser {

    private final ObjectMapper mapper;

    public EventParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Optional<BaseEvent> parseLine(String jsonLine) {
        try {
            JsonNode node = mapper.readTree(jsonLine);
            if (node == null || !node.hasNonNull("eventType")) return Optional.empty();

            String type = node.get("eventType").asText();
            String normalized = type.trim().toLowerCase(Locale.ROOT);

            switch (normalized) {
                case "ordercreated":
                case "order_created":
                    return Optional.of(mapper.treeToValue(node, OrderCreatedEvent.class));
                case "paymentreceived":
                case "payment_received":
                    
                    if (node.has("amount") && !node.has("amountPaid")) {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) node)
                                .set("amountPaid", node.get("amount"));
                    }
                    return Optional.of(mapper.treeToValue(node, PaymentReceivedEvent.class));
                case "shippingscheduled":
                case "shipping_scheduled":
                    return Optional.of(mapper.treeToValue(node, ShippingScheduledEvent.class));
                case "ordercancelled":
                case "order_cancelled":
                    return Optional.of(mapper.treeToValue(node, OrderCancelledEvent.class));
                default:
                    System.err.println("Unknown eventType: " + type);
                    return Optional.empty();
            }
        } catch (Exception ex) {
            System.err.println("Failed to parse line: " + ex.getMessage());
            return Optional.empty();
        }
    }
}

