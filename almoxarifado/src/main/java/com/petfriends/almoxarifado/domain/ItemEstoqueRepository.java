package com.petfriends.almoxarifado.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * REPOSITORY (Repositorio)
 *
 * O repositorio e a "porta" para guardar e buscar o agregado ItemEstoque.
 * No DDD, todo agregado tem 1 repositorio.
 *
 * Usando Spring Data JPA, basta criar a interface que o Spring ja gera
 * os metodos prontos: save(), findById(), findAll(), delete()...
 *
 * O <ItemEstoque, String> significa:
 *   - guarda objetos do tipo ItemEstoque
 *   - cujo ID (sku) e do tipo String
 */
public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, String> {
    // metodo extra "magico": o Spring cria a query sozinho pelo nome
    boolean existsByNomeProduto(String nomeProduto);
}
