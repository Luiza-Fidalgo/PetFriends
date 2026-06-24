package com.petfriends.almoxarifado.messaging;

import com.petfriends.almoxarifado.domain.ItemEstoque;
import com.petfriends.almoxarifado.domain.ItemEstoqueRepository;
import com.petfriends.almoxarifado.domain.Quantidade;
import com.petfriends.almoxarifado.event.PedidoConfirmadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SERVICO QUE RECEBE OS EVENTOS DO PetFriends_Pedidos (Almoxarifado)
 *
 * O @RabbitListener faz esse metodo "escutar" a fila configurada no RabbitConfig.
 * Toda vez que chega um evento PedidoConfirmado, o metodo roda automaticamente.
 *
 * O que ele faz: para cada item do pedido, busca o estoque e RESERVA a quantidade.
 */
@Service
public class PedidoEventListener {

    private final ItemEstoqueRepository repository;

    public PedidoEventListener(ItemEstoqueRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    @Transactional
    public void aoReceberPedidoConfirmado(PedidoConfirmadoEvent evento) {
        System.out.println("\n📨 [ALMOXARIFADO] Evento recebido! Pedido: " + evento.getPedidoId());
        System.out.println("   Itens a separar: " + evento.getItens().size());

        for (PedidoConfirmadoEvent.ItemPedido item : evento.getItens()) {
            ItemEstoque estoque = repository.findById(item.getSku())
                    .orElseThrow(() -> new IllegalStateException(
                            "SKU nao encontrado no almoxarifado: " + item.getSku()));

            // chama a regra de negocio dentro do agregado
            estoque.reservar(new Quantidade(item.getQuantidade()));

            repository.save(estoque);
        }

        System.out.println("🏁 [ALMOXARIFADO] Pedido " + evento.getPedidoId()
                + " separado com sucesso!\n");
    }
}
