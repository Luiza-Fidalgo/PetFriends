package com.petfriends.transporte.messaging;

import com.petfriends.transporte.event.PedidoDespachadoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * CONTROLLER SO PARA TESTE (simula o PetFriends_Pedidos despachando um pedido).
 *
 * Acesse no navegador:  http://localhost:8082/teste/despachar-pedido
 */
@RestController
public class TesteController {

    private final RabbitTemplate rabbitTemplate;

    public TesteController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/teste/despachar-pedido")
    public String despacharPedido() {
        PedidoDespachadoEvent evento = new PedidoDespachadoEvent();
        evento.setPedidoId("PED-" + UUID.randomUUID().toString().substring(0, 4));
        evento.setClienteNome("Maria Silva");
        evento.setRua("Rua das Flores");
        evento.setNumero("123");
        evento.setCidade("Sao Paulo");
        evento.setCep("01001-000");

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                evento);

        System.out.println("📤 [TESTE] Evento publicado para o pedido " + evento.getPedidoId());
        return "Evento publicado! Olhe o console. Pedido: " + evento.getPedidoId();
    }
}
