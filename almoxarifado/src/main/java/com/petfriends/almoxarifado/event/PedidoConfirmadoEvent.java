package com.petfriends.almoxarifado.event;

import java.io.Serializable;
import java.util.List;

/**
 * EVENTO DE DOMINIO (visto pelo lado do Almoxarifado)
 *
 * Esse e o "contrato" da mensagem que o PetFriends_Pedidos envia.
 * O Almoxarifado precisa SEPARAR produtos, entao o evento traz um payload
 * com os itens (SKU + quantidade) - e nao so o ID do pedido.
 * (a explicacao do porque esta nas perguntas conceituais)
 */
public class PedidoConfirmadoEvent implements Serializable {

    private String pedidoId;
    private List<ItemPedido> itens;

    public PedidoConfirmadoEvent() {
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    /** Linha do pedido: qual produto e quantos. */
    public static class ItemPedido implements Serializable {
        private String sku;
        private int quantidade;

        public ItemPedido() {
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }
}
