package com.pedidos.domain.model;

import com.pedidos.domain.enums.StatusPedido;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table (name = "Pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final String id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private String clienteId;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private String restauranteId;

    @OneToMany
    @JoinColumn (name = "itens_pedido")
    private List<ItemPedido> itens;

    @Column (name = "status")
    private StatusPedido status = StatusPedido.AGUARDANDO_CONFIRMACAO;

    @Column (name = "taxa_entrega")
    private BigDecimal taxaEntrega;

    @Column (name = "total")
    private BigDecimal total;

    @Column (name = "data_pedido")
    private LocalDateTime dataPedido;

    @ManyToOne
    @JoinColumn(name = "endereco_entrega")
    private Endereco enderecoEntrega;

    @Column
    private String codigoConfirmacao;

    public Pedido(String id, String clienteId, String restauranteId, BigDecimal taxaEntrega) {
        this.id = (id != null) ? id : UUID.randomUUID().toString();
        this.clienteId = (clienteId != null) ? clienteId : UUID.randomUUID().toString();
        this.restauranteId = (restauranteId != null) ? restauranteId : UUID.randomUUID().toString();
        this.taxaEntrega = (taxaEntrega != null) ? taxaEntrega : BigDecimal.ZERO;
        this.dataPedido = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
        this.itens = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getRestauranteId() {
        return restauranteId;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }

    public void setCodigoConfirmacao(String codigoConfirmacao) {
        this.codigoConfirmacao = codigoConfirmacao;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(taxaEntrega);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteId='" + clienteId + '\'' +
                ", restauranteId='" + restauranteId + '\'' +
                ", itens=" + itens +
                ", status=" + status +
                ", taxaEntrega=" + taxaEntrega +
                ", total=" + total +
                ", dataPedido=" + dataPedido +
                '}';
    }

}
