package com.petfriends.transporte.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASSE DE CONFIGURACAO DA MENSAGERIA (Transporte)
 *
 * Mesma estrutura do Almoxarifado, mas com a FILA e a ROUTING KEY proprias.
 * Repare: a EXCHANGE e a MESMA ("pedidos.exchange"), pois quem publica e o
 * mesmo microsservico (Pedidos). O que muda e a routing key:
 *   - Almoxarifado escuta "pedido.confirmado"
 *   - Transporte    escuta "pedido.despachado"
 * Assim cada um recebe so o evento que lhe interessa.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pedidos.exchange";
    public static final String QUEUE = "transporte.pedido-despachado.queue";
    public static final String ROUTING_KEY = "pedido.despachado";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue transporteQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding(Queue transporteQueue, TopicExchange pedidosExchange) {
        return BindingBuilder
                .bind(transporteQueue)
                .to(pedidosExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
