package com.petfriends.transporte.domain;

/**
 * Estados da entrega, baseados no Diagrama 1 (estados do Pedido):
 *   Em Transito -> Entregue / Devolvido / Extraviado
 */
public enum StatusEntrega {
    EM_TRANSITO,
    ENTREGUE,
    DEVOLVIDO,
    EXTRAVIADO
}
