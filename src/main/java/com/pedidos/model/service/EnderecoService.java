package com.pedidos.model.service;

import com.pedidos.model.entity.Endereco;
import com.pedidos.model.repository.EnderecoRepository;

public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    /**
     * Cria e persiste um endereço vinculado a um cliente.
     *
     * @param clienteId identificador do cliente dono do endereço
     * @param rua       logradouro do endereço
     * @param numero    número do endereço
     * @param bairro    bairro do endereço
     * @param cidade    cidade do endereço
     * @param estado    estado do endereço
     * @param cep       CEP do endereço
     * @return endereço criado e salvo
     * @throws IllegalArgumentException se rua, bairro ou cidade forem nulos ou vazios
     */
    public Endereco criarEndereco(String clienteId, String rua, String numero, String bairro, String cidade, String estado, String cep, Boolean isPadrao) {
        try {
            if (rua == null || rua.isBlank()) {
                throw new IllegalArgumentException("Rua é obrigatória.");
            }
            if (bairro == null || bairro.isBlank()) {
                throw new IllegalArgumentException("Bairro é obrigatório.");
            }
            if (cidade == null || cidade.isBlank()) {
                throw new IllegalArgumentException("Cidade é obrigatória.");
            }

            Endereco endereco = new Endereco(rua, numero, bairro, cidade, estado, cep, isPadrao);
            enderecoRepository.salvar(endereco);
            return endereco;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Busca o endereço de um cliente.
     *
     * @param clienteId identificador do cliente
     * @return endereço encontrado
     * @throws IllegalArgumentException se nenhum endereço for encontrado para o cliente
     */
    public Endereco buscarPorCliente(String clienteId) {
        try {
            return enderecoRepository.buscarPorCliente(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado."));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Edita o endereço de um cliente.
     *
     * @param clienteId  identificador do cliente dono do endereço
     * @param novaRua    nova rua a ser aplicada
     * @param novoBairro novo bairro a ser aplicado
     * @throws IllegalArgumentException se rua ou bairro forem nulos ou vazios
     * @throws IllegalArgumentException se nenhum endereço for encontrado para o cliente
     */
    public void editarEndereco(String clienteId, String novaRua, String novoBairro) {
        try {
            if (novaRua == null || novaRua.isBlank()) {
                throw new IllegalArgumentException("Rua é obrigatória.");
            }
            if (novoBairro == null || novoBairro.isBlank()) {
                throw new IllegalArgumentException("Bairro é obrigatório.");
            }

            Endereco endereco = buscarPorCliente(clienteId);
            endereco.setRua(novaRua);
            endereco.setBairro(novoBairro);
            enderecoRepository.salvar(endereco);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Remove o endereço de um cliente.
     *
     * @param clienteId identificador do cliente dono do endereço
     * @throws IllegalArgumentException se nenhum endereço for encontrado para o cliente
     */
    public void removerEndereco(String clienteId) {
        try {
            buscarPorCliente(clienteId);
            enderecoRepository.remover(clienteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}