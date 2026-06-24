package com.petfriends.transporte.messaging;

import com.petfriends.transporte.domain.Endereco;
import com.petfriends.transporte.domain.Entrega;
import com.petfriends.transporte.domain.EntregaRepository;
import com.petfriends.transporte.event.PedidoDespachadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SERVICO QUE RECEBE OS EVENTOS DO PetFriends_Pedidos (Transporte)
 *
 * Escuta a fila do Transporte. Quando chega "PedidoDespachado", ele
 * monta o Value Object Endereco com os dados do payload e cria uma
 * nova Entrega (que ja nasce EM_TRANSITO).
 */
@Service
public class PedidoEventListener {

    private final EntregaRepository repository;

    public PedidoEventListener(EntregaRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    @Transactional
    public void aoReceberPedidoDespachado(PedidoDespachadoEvent evento) {
        System.out.println("\n📨 [TRANSPORTE] Evento recebido! Pedido: " + evento.getPedidoId());

        // evita criar entrega duplicada se o evento chegar 2x (idempotencia simples)
        if (repository.findByPedidoId(evento.getPedidoId()).isPresent()) {
            System.out.println("⚠️ [TRANSPORTE] Entrega ja existe para esse pedido. Ignorando.\n");
            return;
        }

        // reconstroi o VALUE OBJECT a partir do payload
        Endereco endereco = new Endereco(
                evento.getRua(),
                evento.getNumero(),
                evento.getCidade(),
                evento.getCep());

        Entrega entrega = new Entrega(evento.getPedidoId(), endereco);
        repository.save(entrega);

        System.out.println("🏁 [TRANSPORTE] Entrega registrada (id=" + entrega.getId()
                + ") para " + evento.getClienteNome() + "\n");
    }
}
