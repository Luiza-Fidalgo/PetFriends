package com.petfriends.almoxarifado.messaging;

import com.petfriends.almoxarifado.event.PedidoConfirmadoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * CONTROLLER SO PARA TESTE.
 *
 * Na vida real quem publica esse evento e o microsservico PetFriends_Pedidos.
 * Como nao temos ele aqui, esse endpoint "finge" ser o Pedidos: publica um
 * evento PedidoConfirmado no RabbitMQ para a gente ver o listener funcionando.
 *
 * Acesse no navegador:  http://localhost:8081/teste/confirmar-pedido
 */
@RestController
public class TesteController {

    private final RabbitTemplate rabbitTemplate;

    public TesteController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/teste/confirmar-pedido")
    public String confirmarPedido() {
        PedidoConfirmadoEvent evento = new PedidoConfirmadoEvent();
        evento.setPedidoId("PED-" + UUID.randomUUID().toString().substring(0, 4));

        PedidoConfirmadoEvent.ItemPedido item1 = new PedidoConfirmadoEvent.ItemPedido();
        item1.setSku("RACAO-001");
        item1.setQuantidade(2);

        PedidoConfirmadoEvent.ItemPedido item2 = new PedidoConfirmadoEvent.ItemPedido();
        item2.setSku("BRINQ-002");
        item2.setQuantidade(1);

        evento.setItens(List.of(item1, item2));

        // publica na exchange com a routing key que o Almoxarifado escuta
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                evento);

        System.out.println("📤 [TESTE] Evento publicado para o pedido " + evento.getPedidoId());
        return "Evento publicado! Olhe o console para ver os logs. Pedido: " + evento.getPedidoId();
    }
}
