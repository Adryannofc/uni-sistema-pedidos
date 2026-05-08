
package com.pedidos.domain.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "areas_entrega")
public class AreaEntrega {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column (name = "bairro")
    private String bairro;

    @Column (name = "distancia_maxima_km")
    private BigDecimal distanciaKm;

    @Column (name = "taxa_entrega")
    private BigDecimal taxaEntrega;

    @Column (name = "previsao_entrega_minutos")
    private int previsaoMinutos;

    public AreaEntrega() {}

    public AreaEntrega(Restaurante restaurante, String bairro,
                       BigDecimal distanciaKm, BigDecimal taxaEntrega,
                       int previsaoMinutos) {
        this.id = UUID.randomUUID().toString();  // ← novo
        this.bairro = bairro;
        this.distanciaKm = distanciaKm;
        this.taxaEntrega = taxaEntrega;
        this.previsaoMinutos = previsaoMinutos;
        this.restaurante = restaurante;
    }

    public String getId() {
        return id;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(BigDecimal distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    public Restaurante getRestaurante() { return restaurante; }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public String getRestauranteId() { return restaurante != null ? restaurante.getId() : null; }

    public void setTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public int getPrevisaoMinutos() {
        return previsaoMinutos;
    }

    public void setPrevisaoMinutos(int previsaoMinutos) {
        this.previsaoMinutos = previsaoMinutos;
    }

    @Override
    public String toString() {
        return "AreaEntrega{bairro='" + bairro + "', taxa=" + taxaEntrega
                + ", previsao=" + previsaoMinutos + "min}";
    }
}