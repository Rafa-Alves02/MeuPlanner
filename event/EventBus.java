package br.com.MeuPlanner.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Barramento de eventos de domínio em memória, síncrono (Observer Pattern). */
public final class EventBus {

    private static final Map<Class<?>, List<Consumer<Object>>> LISTENERS = new ConcurrentHashMap<>();

    private EventBus() {}

    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void subscribe(Class<T> tipo, Consumer<T> listener) {
        LISTENERS.computeIfAbsent(tipo, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) listener);
    }

    public static void publish(DomainEvent evento) {
        List<Consumer<Object>> listeners = LISTENERS.get(evento.getClass());
        if (listeners == null) return;
        for (Consumer<Object> listener : listeners) {
            listener.accept(evento);
        }
    }
}