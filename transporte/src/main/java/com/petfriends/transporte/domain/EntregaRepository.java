package com.petfriends.transporte.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * REPOSITORY (Repositorio) do agregado Entrega.
 *
 * Mesma ideia do Almoxarifado: 1 agregado = 1 repositorio.
 * Spring Data JPA gera save/findById/findAll automaticamente.
 */
public interface EntregaRepository extends JpaRepository<Entrega, String> {
    // busca a entrega de um pedido especifico (query gerada pelo nome)
    Optional<Entrega> findByPedidoId(String pedidoId);
}
