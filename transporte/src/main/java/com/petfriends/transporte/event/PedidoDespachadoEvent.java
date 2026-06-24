package com.petfriends.transporte.event;

import java.io.Serializable;

/**
 * EVENTO DE DOMINIO (lado do Transporte)
 *
 * Contrato da mensagem que o PetFriends_Pedidos envia quando o pedido
 * e despachado. O Transporte precisa saber PARA ONDE levar, entao o
 * payload traz os dados do destino (endereco), nao so o ID do pedido.
 */
public class PedidoDespachadoEvent implements Serializable {

    private String pedidoId;
    private String clienteNome;

    // dados do endereco (o Transporte reconstroi o Value Object Endereco com isso)
    private String rua;
    private String numero;
    private String cidade;
    private String cep;

    public PedidoDespachadoEvent() {
    }

    public String getPedidoId() { return pedidoId; }
    public void setPedidoId(String pedidoId) { this.pedidoId = pedidoId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
}
