package com.pedidos.model.service;

import com.pedidos.model.entity.Admin;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.model.enums.StatusRestaurante;
import com.pedidos.model.repository.AdminRepository;
import com.pedidos.model.repository.CategoriaCardapioRepository;
import com.pedidos.model.repository.RestauranteRepository;

import java.util.List;

public class AdminService {
    private final AdminRepository adminRepository;
    private final AutenticacaoService autenticacaoService;
    private final RestauranteRepository restauranteRepository;
    private CategoriaCardapioRepository categoriaCardapioRepository;

    /**
     * Construtor da classe
     *
     * @param adminRepository       gerencia os dados de um admin
     * @param autenticacaoService   verifica se as credenciais estao corretas
     * @param restauranteRepository gerencia os dados dos restaurantes
     */
    public AdminService(AdminRepository adminRepository, AutenticacaoService autenticacaoService, RestauranteRepository restauranteRepository, CategoriaCardapioRepository categoriaCardapioRepository) {
        this.adminRepository = adminRepository;
        this.autenticacaoService = autenticacaoService;
        this.restauranteRepository = restauranteRepository;
        this.categoriaCardapioRepository = categoriaCardapioRepository;
    }

    /**
     * Registra um novo administrador no sistema com as credencias informadas.
     *
     * @param nome  identificador pessoal fornecido pelo usuario que sera registrado
     * @param email credencial de acesso fornecido pelo usuario que sera registrado
     * @param senha credencial de acesso fornecido pelo usuario que sera registrado
     * @throws IllegalArgumentException se ja tiver o email informado cadastrado
     */
    public void cadastrarAdmin(String nome, String email, String senha) {
        try {
            if (adminRepository.buscarPorEmail(email).isPresent()) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            }
            String hash = autenticacaoService.hashSenha(senha);
            Admin admin = new Admin(nome, email, hash);
            adminRepository.salvar(admin);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Altera senha atual de um administrador
     *
     * @param usuario          administrador que tera senha alterada
     * @param senhaAtual       senha atual informada pelo usuario
     * @param novaSenha        nova senha que sera aplicada
     * @param confirmacaoSenha confirmacao da nova senha
     * @throws IllegalArgumentException se a senha atual fornecida for incorreta
     * @throws IllegalArgumentException se a nova senha for diferente da confirmacao
     */
    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        try {
            String hashAtual = autenticacaoService.hashSenha(senhaAtual);
            if (!usuario.verificarSenha(hashAtual)) {
                throw new IllegalArgumentException("Senha atual incorreta.");
            }

            if (!novaSenha.equals(confirmacaoSenha)) {
                throw new IllegalArgumentException("Nova senha e confirmação não coincidem.");
            }

            String novoHash = autenticacaoService.hashSenha(novaSenha);
            usuario.setSenhaHash(novoHash);
            adminRepository.salvar(usuario);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * Lista todos os restaurantes cadastrados
     *
     * @return lista de restaurantes
     */
    public List<Restaurante> listarRestaurantes() {
        try {
            return restauranteRepository.listarRestaurantes();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Aprova um restaurante, tornando-o visível e ativo no sistema.
     *
     * @param id identificador único do restaurante
     * @throws IllegalArgumentException se nenhum restaurante for encontrado com o id informado
     */
    public void aprovarRestaurante(String id) {
        try {
            Restaurante restaurante = buscarRestaurantePorId(id);
            restaurante.setStatus(StatusRestaurante.ATIVO);
            restauranteRepository.salvar(restaurante);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * Bloqueia um restaurante, tornado-o invisivel e desativado no sistema
     *
     * @param id identificador unico do restaurante
     * @throws IllegalArgumentException se nenhum restaurante for encontrado com id informado
     */
    public void bloquearRestaurante(String id) {
        try {
            Restaurante restaurante = buscarRestaurantePorId(id);
            restaurante.setStatus(StatusRestaurante.BLOQUEADO);
            restauranteRepository.salvar(restaurante);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * Deleta um restaurante do sistema com id verificado fornecido
     *
     * @param id identificador unico do restaurante
     * @throws IllegalArgumentException se nenhum restaurante for encontrado com id informado
     */
    @jakarta.transaction.Transactional
    public void removerRestaurante(String id) {
        try {
            buscarRestaurantePorId(id);
            this.categoriaCardapioRepository.remover(id);

            this.restauranteRepository.deletar(id);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Procura restaurante com id fornecido.
     *
     * @param id identificador do restaurante
     * @return o restaurante correspondente ao id informado
     * @throws IllegalArgumentException se nenhum restaurante foi encontrado com id informado.
     */
    private Restaurante buscarRestaurantePorId(String id) {
        try {
            return restauranteRepository.listarRestaurantes().stream()
                    .filter(r -> r.getId().equals(id)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}