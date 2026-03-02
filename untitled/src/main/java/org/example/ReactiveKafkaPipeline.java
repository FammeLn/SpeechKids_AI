package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReactiveKafkaPipeline {

    private static final String BOOTSTRAP_SERVERS = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    private static final String TOPIC = "orders-topic";
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        // Запускаем эмуляцию отправки данных в Kafka (Producer)
        produceOrders();

        // Запускаем реактивную обработку из Kafka (Consumer)
        consumeAndProcess();

        Thread.sleep(20000);
    }

    // Эмуляция отправки заказов в Kafka

    private static void produceOrders() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        SenderOptions<Integer, String> senderOptions = SenderOptions.create(props);
        KafkaSender<Integer, String> sender = KafkaSender.create(senderOptions);

        Flux<SenderRecord<Integer, String, Integer>> orderFlux = Flux.range(1, 10)
                .delayElements(Duration.ofMillis(500)) // Имитируем появление заказов раз в полсекунды
                .map(i -> {
                    String payload = "Product-" + i + ":" + (random.nextInt(1000) + 100);
                    return SenderRecord.create(new ProducerRecord<>(TOPIC, i, payload), i);
                });

        sender.send(orderFlux)
                .doOnNext(r -> System.out.println(">>> Kafka: Отправлен заказ #" + r.correlationMetadata()))
                .subscribe();
    }

 // Чтение из Kafka и запуск пайплайна обработки

    private static void consumeAndProcess() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processors");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions<Integer, String> receiverOptions = ReceiverOptions.<Integer, String>create(props)
                .subscription(Collections.singleton(TOPIC));

        KafkaReceiver.create(receiverOptions)
                .receive()
                .flatMap(record -> {
                    // Парсим данные из Kafka в объект Order
                    Order order = parseOrder(record.key(), record.value());

                    return Mono.just(order)
                            .doOnNext(o -> System.out.println("Получен из Kafka: " + o))
                            .flatMap(ReactiveKafkaPipeline::validateOrder)
                            .flatMap(ReactiveKafkaPipeline::calculateDiscount)
                            .flatMap(ReactiveKafkaPipeline::processPayment)
                            .flatMap(ReactiveKafkaPipeline::shipOrder)
                            // Подтверждаем (commit) смещение в Kafka только после успешной обработки
                            .doOnSuccess(o -> record.receiverOffset().acknowledge())
                            .onErrorResume(e -> {
                                System.err.println("Ошибка обработки заказа " + record.key() + ": " + e.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    private static Order parseOrder(Integer id, String value) {
        String[] parts = value.split(":");
        return new Order(id, parts[0], Double.parseDouble(parts[1]));
    }

    private static Mono<Order> validateOrder(Order order) {
        return Mono.delay(Duration.ofMillis(100)).map(tick -> order);
    }

    private static Mono<Order> calculateDiscount(Order order) {
        return Mono.delay(Duration.ofMillis(50)).map(tick -> {
            if (order.amount > 500) {
                order.discount = order.amount * 0.1;
                order.amount -= order.discount;
            }
            return order;
        });
    }

    private static Mono<Order> processPayment(Order order) {
        return Mono.delay(Duration.ofMillis(200)).map(tick -> {
            order.paid = true;
            return order;
        });
    }

    private static Mono<Order> shipOrder(Order order) {
        return Mono.delay(Duration.ofMillis(150)).map(tick -> {
            order.shipped = true;
            System.out.println(" [OK] Заказ завершен: " + order);
            return order;
        });
    }

    // --- Модель данных ---

    static class Order {
        int id;
        String product;
        double amount;
        double discount;
        boolean paid;
        boolean shipped;

        Order(int id, String product, double amount) {
            this.id = id;
            this.product = product;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return String.format("Order{id=%d, prod='%s', total=%.2f, disc=%.2f}", id, product, amount, discount);
        }
    }
}