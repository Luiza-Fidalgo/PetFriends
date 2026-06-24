package com.petfriends.almoxarifado.domain;

import jakarta.persistence.*;

/**
 * ENTITY / AGGREGATE ROOT (Raiz do Agregado)
 *
 * Esse e o agregado mais representativo do microsservico PetFriends_Almoxarifado.
 * O Almoxarifado cuida do ESTOQUE, entao o item de estoque e o coracao do dominio.
 *
 * Diferente do Value Object:
 *  - Tem IDENTIDADE propria (o campo "id").
 *  - Tem CICLO DE VIDA (quantidade muda com o tempo).
 *  - As regras de negocio do estoque ficam DENTRO dele (reservar, repor...).
 *
 * Ele usa o Value Object "Quantidade" para representar quantos itens existem.
 */
@Entity
@Table(name = "item_estoque")
public class ItemEstoque {

    @Id
    private String sku; // identidade: o codigo do produto (Stock Keeping Unit)

    private String nomeProduto;

    // VALUE OBJECT embutido na entidade (regra da segunda questao)
    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "quantidade_disponivel"))
    private Quantidade quantidadeDisponivel;

    protected ItemEstoque() {
    }

    public ItemEstoque(String sku, String nomeProduto, Quantidade quantidadeInicial) {
        this.sku = sku;
        this.nomeProduto = nomeProduto;
        this.quantidadeDisponivel = quantidadeInicial;
    }

    /**
     * Regra de negocio do dominio: separar/reservar itens para um pedido.
     * Lanca erro se nao houver estoque suficiente.
     */
    public void reservar(Quantidade quantidadePedida) {
        System.out.println("📦 [DOMINIO] Tentando reservar " + quantidadePedida
                + " de '" + nomeProduto + "' (disponivel: " + quantidadeDisponivel + ")");

        if (!quantidadeDisponivel.ehMaiorOuIgual(quantidadePedida)) {
            System.out.println("🚫 [DOMINIO] Estoque insuficiente para " + nomeProduto + "!");
            throw new IllegalStateException("Estoque insuficiente para o SKU " + sku);
        }

        this.quantidadeDisponivel = this.quantidadeDisponivel.subtrair(quantidadePedida);
        System.out.println("✅ [DOMINIO] Reservado! Novo saldo de '" + nomeProduto
                + "': " + quantidadeDisponivel);
    }

    public void repor(Quantidade quantidade) {
        this.quantidadeDisponivel = this.quantidadeDisponivel.somar(quantidade);
        System.out.println("➕ [DOMINIO] Reposto. Saldo de '" + nomeProduto
                + "': " + quantidadeDisponivel);
    }

    public String getSku() {
        return sku;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Quantidade getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
}
