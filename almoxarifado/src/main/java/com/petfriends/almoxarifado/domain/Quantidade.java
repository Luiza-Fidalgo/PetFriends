package com.petfriends.almoxarifado.domain;

import jakarta.persistence.Embeddable;

/**
 * VALUE OBJECT (Objeto de Valor)
 *
 * Caracteristicas que fazem disso um Value Object:
 *  - NAO tem identidade propria (nao tem ID).
 *  - E imutavel (os campos sao "final", so se cria um novo).
 *  - E comparado pelo VALOR e nao por referencia (equals/hashCode).
 *  - Protege as proprias regras (nao deixa criar quantidade negativa).
 *
 * Aqui ele representa "uma quantidade de itens em estoque".
 */
@Embeddable // o JPA vai "embutir" esse VO dentro da tabela da entidade
public class Quantidade {

    private int valor;

    // Construtor vazio exigido pelo JPA
    protected Quantidade() {
    }

    public Quantidade(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("❌ Quantidade nao pode ser negativa!");
        }
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    // Como e imutavel, operacoes retornam um NOVO Value Object
    public Quantidade subtrair(Quantidade outra) {
        return new Quantidade(this.valor - outra.valor);
    }

    public Quantidade somar(Quantidade outra) {
        return new Quantidade(this.valor + outra.valor);
    }

    public boolean ehMaiorOuIgual(Quantidade outra) {
        return this.valor >= outra.valor;
    }

    // Comparacao por VALOR (regra de ouro do Value Object)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantidade)) return false;
        Quantidade that = (Quantidade) o;
        return valor == that.valor;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(valor);
    }

    @Override
    public String toString() {
        return valor + " un";
    }
}
