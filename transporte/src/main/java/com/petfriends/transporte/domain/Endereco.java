package com.petfriends.transporte.domain;

import jakarta.persistence.Embeddable;

/**
 * VALUE OBJECT (Objeto de Valor) - Endereco de entrega
 *
 * E o exemplo classico de Value Object:
 *  - Nao tem ID. Um endereco nao "existe sozinho", ele descreve um lugar.
 *  - E imutavel (se mudar a rua, e outro endereco).
 *  - Dois enderecos com os mesmos campos sao considerados IGUAIS (equals).
 *  - Valida a si mesmo (CEP nao pode ser vazio).
 *
 * Usado dentro da entidade Entrega.
 */
@Embeddable
public class Endereco {

    private String rua;
    private String numero;
    private String cidade;
    private String cep;

    protected Endereco() {
    }

    public Endereco(String rua, String numero, String cidade, String cep) {
        if (cep == null || cep.isBlank()) {
            throw new IllegalArgumentException("❌ CEP e obrigatorio no endereco!");
        }
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.cep = cep;
    }

    public String resumo() {
        return rua + ", " + numero + " - " + cidade + " (CEP " + cep + ")";
    }

    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getCidade() { return cidade; }
    public String getCep() { return cep; }

    // Igualdade por VALOR
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco)) return false;
        Endereco e = (Endereco) o;
        return rua.equals(e.rua) && numero.equals(e.numero)
                && cidade.equals(e.cidade) && cep.equals(e.cep);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(rua, numero, cidade, cep);
    }

    @Override
    public String toString() {
        return resumo();
    }
}
