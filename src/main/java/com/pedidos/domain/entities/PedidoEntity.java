package com.pedidos.domain.entities;

import com.pedidos.domain.enums.StatusPedido;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;                         // removido o 'final' — JPA precisa gerenciar o campo

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity clienteEntity;                   // tipo mudou de String para Cliente

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private RestauranteEntity restauranteEntity;           // tipo mudou de String para Restaurante

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedidoEntity> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido status = StatusPedido.AGUARDANDO_CONFIRMACAO;

    @Column(name = "taxa_entrega", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxaEntrega;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "codigo_confirmacao")
    private String codigoConfirmacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private EnderecoEntity enderecoEntityEntrega;

    protected PedidoEntity() {} // no-arg para o JPA

    public PedidoEntity(String id, String clienteId, String restauranteId, BigDecimal taxaEntrega) {
        this.id = (id != null) ? id : UUID.randomUUID().toString();
        this.taxaEntrega = (taxaEntrega != null) ? taxaEntrega : BigDecimal.ZERO;
        this.dataPedido = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public String getId() { return id; }

    public ClienteEntity getCliente() { return clienteEntity; }
    public void setCliente(ClienteEntity clienteEntity) { this.clienteEntity = clienteEntity; }

    // mantém compatibilidade com services que usam clienteId como String
    public String getClienteId() { return clienteEntity != null ? clienteEntity.getId() : null; }

    public RestauranteEntity getRestaurante() { return restauranteEntity; }
    public void setRestaurante(RestauranteEntity restauranteEntity) { this.restauranteEntity = restauranteEntity; }

    public String getRestauranteId() { return restauranteEntity != null ? restauranteEntity.getId() : null; }

    public List<ItemPedidoEntity> getItens() { return itens; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getDataPedido() { return dataPedido; }
    public EnderecoEntity getEnderecoEntrega() { return enderecoEntityEntrega; }
    public void setEnderecoEntrega(EnderecoEntity e) { this.enderecoEntityEntrega = e; }
    public String getCodigoConfirmacao() { return codigoConfirmacao; }
    public void setCodigoConfirmacao(String c) { this.codigoConfirmacao = c; }

    public void adicionarItem(ItemPedidoEntity item) { itens.add(item); }

    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemPedidoEntity::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(taxaEntrega);
    }
}