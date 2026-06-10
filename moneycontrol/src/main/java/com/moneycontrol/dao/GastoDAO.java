package com.moneycontrol.dao;

import com.moneycontrol.model.Categoria;
import com.moneycontrol.model.Gasto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GastoDAO {

    public void salvar(Gasto gasto) throws SQLException {
        String sql = "INSERT INTO gastos (descricao, valor, data, categoria, usuario_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gasto.getDescricao());
            ps.setDouble(2, gasto.getValor());
            ps.setString(3, gasto.getData().toString());
            ps.setString(4, gasto.getCategoria().getLabel());
            ps.setInt(5, gasto.getUsuarioId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) gasto.setId(rs.getInt(1));
            }
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM gastos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Gasto> listarPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM gastos WHERE usuario_id = ? ORDER BY data DESC, id DESC";
        return executarLista(sql, usuarioId);
    }

    public List<Gasto> listarPorUsuarioECategoria(int usuarioId, String categoria) throws SQLException {
        String sql = "SELECT * FROM gastos WHERE usuario_id = ? AND categoria = ? ORDER BY data DESC, id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, categoria);
            return mapearLista(ps.executeQuery());
        }
    }

    public List<Gasto> listarRecentes(int usuarioId, int limite) throws SQLException {
        String sql = "SELECT * FROM gastos WHERE usuario_id = ? ORDER BY data DESC, id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, limite);
            return mapearLista(ps.executeQuery());
        }
    }

    public double totalPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM gastos WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    public double totalPorCategoria(int usuarioId, String categoria) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM gastos WHERE usuario_id = ? AND categoria = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    public double maiorGasto(int usuarioId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(valor), 0) FROM gastos WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    public long contar(int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM gastos WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private List<Gasto> executarLista(String sql, int usuarioId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            return mapearLista(ps.executeQuery());
        }
    }

    private List<Gasto> mapearLista(ResultSet rs) throws SQLException {
        List<Gasto> lista = new ArrayList<>();
        while (rs.next()) lista.add(mapear(rs));
        return lista;
    }

    private Gasto mapear(ResultSet rs) throws SQLException {
        Gasto g = new Gasto();
        g.setId(rs.getInt("id"));
        g.setDescricao(rs.getString("descricao"));
        g.setValor(rs.getDouble("valor"));
        g.setData(LocalDate.parse(rs.getString("data")));
        g.setCategoria(Categoria.fromLabel(rs.getString("categoria")));
        g.setUsuarioId(rs.getInt("usuario_id"));
        return g;
    }
}
