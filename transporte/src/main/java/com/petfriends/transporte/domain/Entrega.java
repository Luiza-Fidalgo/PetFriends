package com.petfriends.transporte.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTITY / AGGREGATE ROOT (Raiz do Agregado) - Entrega
 *
 * Agregado mais representativo do microsservico PetFriends_Transporte.
 * O Transporte cuida de LEVAR o pedido ate o cliente, entao a "Entrega"
 * e o centro do dominio.
 *
 *  - Tem identidade propria (id).
 *  - Tem ciclo de vida / estado (EM_TRANSITO -> ENTREGUE/DEVOLVIDO/EXTRAVIADO),
 *    igual ao Diagrama 1.
 *  - Usa o Value Object "Endereco".
 */
@Entity
@Table(name = "entrega")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // identidade da entrega

    private String pedidoId; // de qual pedido essa entrega veio

    @Embedded
    private Endereco enderecoEntrega; // VALUE OBJECT

    @Enumerated(EnumType.STRING)
    private StatusEntrega status;

    private LocalDateTime despachadoEm;

    protected Entrega() {
    }

    public Entrega(String pedidoId, Endereco enderecoEntrega) {
        this.pedidoId = pedidoId;
        this.enderecoEntrega = enderecoEntrega;
        this.status = StatusEntrega.EM_TRANSITO; // nasce "em transito" ao ser despachada
        this.despachadoEm = LocalDateTime.now();
        System.out.println("🚚 [DOMINIO] Entrega criada para pedido " + pedidoId
                + " -> " + enderecoEntrega.resumo());
    }

    // Regras de negocio (transicoes de estado do Diagrama 1)
    public void confirmarEntrega() {
        garantirEmTransito();
        this.status = StatusEntrega.ENTREGUE;
        System.out.println("📍 [DOMINIO] Pedido " + pedidoId + " ENTREGUE!");
    }

    public void registrarDevolucao() {
        garantirEmTransito();
        this.status = StatusEntrega.DEVOLVIDO;
        System.out.println("↩️ [DOMINIO] Pedido " + pedidoId + " DEVOLVIDO (rejeitado).");
    }

    public void registrarExtravio() {
        garantirEmTransito();
        this.status = StatusEntrega.EXTRAVIADO;
        System.out.println("📭 [DOMINIO] Pedido " + pedidoId + " EXTRAVIADO (30 dias).");
    }

    private void garantirEmTransito() {
        if (this.status != StatusEntrega.EM_TRANSITO) {
            throw new IllegalStateException("Entrega nao esta mais em transito: " + status);
        }
    }

    public String getId() { return id; }
    public String getPedidoId() { return pedidoId; }
    public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    public StatusEntrega getStatus() { return status; }
    public LocalDateTime getDespachadoEm() { return despachadoEm; }
}
