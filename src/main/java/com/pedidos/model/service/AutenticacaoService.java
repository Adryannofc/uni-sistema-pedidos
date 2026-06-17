package com.pedidos.model.service;

import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.model.repository.AdminRepository;
import com.pedidos.model.repository.ClienteRepository;
import com.pedidos.model.repository.RestauranteRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AutenticacaoService {
    private final AdminRepository adminRepository;
    private final RestauranteRepository restauranteRepository;
    private final ClienteRepository clienteRepository;


    public AutenticacaoService(AdminRepository adminRepository, RestauranteRepository restauranteRepository, ClienteRepository clienteRepository) {
        this.adminRepository = adminRepository;
        this.restauranteRepository = restauranteRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Criptrografa a senha do usuario
     *
     * @param senha
     * @return para senha criptografada em String
     * @ throw trata erro geracional do hash
     */
    public String hashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(senha.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao gerar hash da senha", e);
        }
    }

    /**
     * Verifica se a senha corresponde a algo existente(Autenticação)
     *
     * @param email
     * @param senha
     * @return usuario
     * @throw tratamento de erro na autenticação
     */
    public Usuario autenticar(String email, String senha) {
        try {
            String senhaHash = hashSenha(senha);

            Usuario usuario = adminRepository.buscarPorEmailSenha(email, senhaHash);
            if (usuario != null) return usuario;

            usuario = restauranteRepository.buscarPorEmailSenha(email, senhaHash);
            if (usuario != null){
                Restaurante restaurante = (Restaurante) usuario;

                if (!restaurante.isStatusAtivo()){
                  throw new RuntimeException("Conta bloqueada. Entre em contato com o suporte.");
                };

                return restaurante;
            }


            usuario = clienteRepository.buscarPorEmailSenha(email, senhaHash);
            if (usuario != null) return usuario;

            throw new RuntimeException("Email ou senha inválidos.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}