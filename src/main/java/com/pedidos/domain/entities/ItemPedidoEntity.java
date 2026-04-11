package com.pedidos.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_pedido")
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedidoEntity;                    // lado proprietário da relação

    @Column(name = "produto_id")
    private String produtoId;

    @Column(name = "nome_produto", nullable = false)
    private String nomeProduto;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    protected ItemPedidoEntity() {}   // construtor no-arg para o JPA

    public ItemPedidoEntity(String produtoId, String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // setter para o JPA fechar o relacionamento bidirecional
    public void setPedido(PedidoEntity pedidoEntity) { this.pedidoEntity = pedidoEntity; }

    public String getId() { return id; }
    public String getProdutoId() { return produtoId; }
    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public BigDecimal calcularSubtotal() { return precoUnitario.multiply(BigDecimal.valueOf(quantidade)); }
}