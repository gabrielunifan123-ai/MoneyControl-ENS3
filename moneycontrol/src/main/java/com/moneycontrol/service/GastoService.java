package com.moneycontrol.service;

import com.moneycontrol.dao.GastoDAO;
import com.moneycontrol.model.Categoria;
import com.moneycontrol.model.Gasto;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class GastoService {

    private final GastoDAO dao = new GastoDAO();

    public Gasto registrar(String descricao, double valor, LocalDate data, Categoria categoria, int usuarioId)
            throws ServiceException {
        validar(descricao, valor);
        if (data == null) throw new ServiceException("Informe a data.");
        if (data.isAfter(LocalDate.now())) throw new ServiceException("A data não pode ser futura.");

        try {
            Gasto g = new Gasto(descricao.trim(), valor, data, categoria, usuarioId);
            dao.salvar(g);
            return g;
        } catch (SQLException e) {
            throw new ServiceException("Erro ao salvar gasto: " + e.getMessage());
        }
    }

    public void remover(int id) throws ServiceException {
        try {
            dao.remover(id);
        } catch (SQLException e) {
            throw new ServiceException("Erro ao remover gasto: " + e.getMessage());
        }
    }

    public List<Gasto> listar(int usuarioId) throws ServiceException {
        try { return dao.listarPorUsuario(usuarioId); }
        catch (SQLException e) { throw new ServiceException("Erro ao listar gastos: " + e.getMessage()); }
    }

    public List<Gasto> listarPorCategoria(int usuarioId, String categoria) throws ServiceException {
        try { return dao.listarPorUsuarioECategoria(usuarioId, categoria); }
        catch (SQLException e) { throw new ServiceException("Erro ao filtrar gastos: " + e.getMessage()); }
    }

    public List<Gasto> listarRecentes(int usuarioId, int limite) throws ServiceException {
        try { return dao.listarRecentes(usuarioId, limite); }
        catch (SQLException e) { throw new ServiceException("Erro ao buscar recentes: " + e.getMessage()); }
    }

    public double totalGasto(int usuarioId) throws ServiceException {
        try { return dao.totalPorUsuario(usuarioId); }
        catch (SQLException e) { throw new ServiceException(e.getMessage()); }
    }

    public double maiorGasto(int usuarioId) throws ServiceException {
        try { return dao.maiorGasto(usuarioId); }
        catch (SQLException e) { throw new ServiceException(e.getMessage()); }
    }

    public long contarGastos(int usuarioId) throws ServiceException {
        try { return dao.contar(usuarioId); }
        catch (SQLException e) { throw new ServiceException(e.getMessage()); }
    }

    public Map<Categoria, Double> totalPorCategoria(int usuarioId) throws ServiceException {
        Map<Categoria, Double> mapa = new LinkedHashMap<>();
        try {
            for (Categoria c : Categoria.values()) {
                double v = dao.totalPorCategoria(usuarioId, c.getLabel());
                if (v > 0) mapa.put(c, v);
            }
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage());
        }
        return mapa;
    }

    private void validar(String descricao, double valor) throws ServiceException {
        if (descricao == null || descricao.isBlank()) throw new ServiceException("Informe a descrição.");
        if (descricao.trim().length() < 2) throw new ServiceException("Descrição muito curta.");
        if (valor <= 0) throw new ServiceException("O valor deve ser maior que zero.");
        if (valor > 1_000_000) throw new ServiceException("Valor acima do limite permitido.");
    }
}
