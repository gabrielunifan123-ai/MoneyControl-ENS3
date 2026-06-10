package com.moneycontrol.service;

import com.moneycontrol.dao.UsuarioDAO;
import com.moneycontrol.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario cadastrar(String nome, String email, String senha) throws ServiceException {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);

        try {
            if (dao.emailExiste(email)) {
                throw new ServiceException("Este e-mail já está cadastrado.");
            }
            String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
            Usuario u = new Usuario(nome.trim(), email.trim().toLowerCase(), hash);
            dao.salvar(u);
            return u;
        } catch (SQLException e) {
            throw new ServiceException("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public Usuario autenticar(String email, String senha) throws ServiceException {
        if (email == null || email.isBlank()) throw new ServiceException("Informe o e-mail.");
        if (senha == null || senha.isBlank()) throw new ServiceException("Informe a senha.");

        try {
            Optional<Usuario> opt = dao.buscarPorEmail(email.trim().toLowerCase());
            if (opt.isEmpty()) throw new ServiceException("E-mail ou senha incorretos.");
            Usuario u = opt.get();
            if (!BCrypt.checkpw(senha, u.getSenhaHash())) {
                throw new ServiceException("E-mail ou senha incorretos.");
            }
            return u;
        } catch (SQLException e) {
            throw new ServiceException("Erro ao autenticar: " + e.getMessage());
        }
    }

    private void validarNome(String nome) throws ServiceException {
        if (nome == null || nome.isBlank()) throw new ServiceException("Informe o nome completo.");
        if (nome.trim().length() < 3) throw new ServiceException("Nome deve ter ao menos 3 caracteres.");
    }

    private void validarEmail(String email) throws ServiceException {
        if (email == null || email.isBlank()) throw new ServiceException("Informe o e-mail.");
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[\\w.\\-]+$")) {
            throw new ServiceException("E-mail inválido.");
        }
    }

    private void validarSenha(String senha) throws ServiceException {
        if (senha == null || senha.length() < 8) {
            throw new ServiceException("A senha deve ter no mínimo 8 caracteres.");
        }
    }
}
