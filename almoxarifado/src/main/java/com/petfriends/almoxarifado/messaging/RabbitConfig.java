package com.petfriends.almoxarifado.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASSE DE CONFIGURACAO DA MENSAGERIA (Almoxarifado)
 *
 * Aqui montamos o "encanamento" do RabbitMQ para RECEBER os eventos
 * que o PetFriends_Pedidos publica.
 *
 * Ideia em 3 pecas:
 *   1) EXCHANGE  -> onde o Pedidos publica os eventos (a "central de correios")
 *   2) QUEUE     -> a caixa de correio so do Almoxarifado
 *   3) BINDING   -> a regra que liga a exchange na fila pela "routing key"
 */
@Configuration
public class RabbitConfig {

    // nomes combinados entre os times (Pedidos publica com esses nomes)
    public static final String EXCHANGE = "pedidos.exchange";
    public static final String QUEUE = "almoxarifado.pedido-confirmado.queue";
    public static final String ROUTING_KEY = "pedido.confirmado";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue almoxarifadoQueue() {
        // durable = true -> a fila sobrevive se o RabbitMQ reiniciar
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding(Queue almoxarifadoQueue, TopicExchange pedidosExchange) {
        return BindingBuilder
                .bind(almoxarifadoQueue)
                .to(pedidosExchange)
                .with(ROUTING_KEY);
    }

    /**
     * Faz o Rabbit converter a mensagem JSON <-> objeto Java automaticamente.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
