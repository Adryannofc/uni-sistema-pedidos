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
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;                         // removido o 'final' — JPA precisa gerenciar o campo

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;                   // tipo mudou de String para Cliente

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;           // tipo mudou de String para Restaurante

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

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
    private Endereco enderecoEntrega;

    public Pedido() {
        this.id = UUID.randomUUID().toString();
        this.dataPedido = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
        this.taxaEntrega = BigDecimal.ZERO;
    }

    public String getId() { return id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    // mantém compatibilidade com services que usam clienteId como String
    public String getClienteId() { return cliente != null ? cliente.getId() : null; }

    public Restaurante getRestaurante() { return restaurante; }
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; }

    public String getRestauranteId() { return restaurante != null ? restaurante.getId() : null; }

    public List<ItemPedido> getItens() { return itens; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getDataPedido() { return dataPedido; }
    public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(Endereco e) { this.enderecoEntrega = e; }
    public String getCodigoConfirmacao() { return codigoConfirmacao; }
    public void setCodigoConfirmacao(String c) { this.codigoConfirmacao = c; }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
    }

    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(item -> item.calcularSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(taxaEntrega);
    }
}