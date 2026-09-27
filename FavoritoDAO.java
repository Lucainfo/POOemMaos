/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author isabe
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    public boolean favoritar(int idUsuario, int idTermo) {
        String sql = "INSERT INTO favoritos (usuario_id, termo_id) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idTermo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean desfavoritar(int idUsuario, int idTermo) {
        String sql = "DELETE FROM favoritos WHERE usuario_id = ? AND termo_id = ?";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idTermo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFavoritado(int idUsuario, int idTermo) {
        String sql = "SELECT 1 FROM favoritos WHERE usuario_id = ? AND termo_id = ?";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idTermo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean alternarFavorito(int idUsuario, int idTermo) {

        if (isFavoritado(idUsuario, idTermo)) {
            desfavoritar(idUsuario, idTermo);
            return false;
        } else {
            favoritar(idUsuario, idTermo);
            return true;
        }
    }

    public List<Termo> listarFavoritos(int idUsuario) {
        List<Termo> lista = new ArrayList<>();
        String sql = "SELECT t.* FROM termos t "
                + "JOIN favoritos f ON t.id = f.termo_id "
                + "WHERE f.usuario_id = ?";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Termo t = new Termo();
                    t.setId(rs.getInt("id"));
                    t.setTitulo(rs.getString("titulo"));
                    t.setDescricao(rs.getString("descricao"));
                    t.setNivel(rs.getString("nivel"));
                    t.setVideo(rs.getString("video"));
                    t.setCapa(rs.getString("capa"));
                    t.setImagem1(rs.getString("imagem1"));
                    t.setImagem2(rs.getString("imagem2"));
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

public String buscarFavorito(int idUsuario) {
    StringBuilder json = new StringBuilder("[");

    String sql = "SELECT t.* FROM termos t "
            + "INNER JOIN favoritos f ON t.id = f.termo_id "
            + "WHERE f.usuario_id = ?";

    try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idUsuario);

        try (ResultSet rs = stmt.executeQuery()) {
            boolean primeiro = true;
            while (rs.next()) {
                if (!primeiro) {
                    json.append(",");
                }
                
                int id = rs.getInt("id");
                String titulo = limpar(rs.getString("titulo"));
                String descricao = limpar(rs.getString("descricao"));
                String nivel = limpar(rs.getString("nivel") != null ? rs.getString("nivel") : "Básico");
                String video = limpar(rs.getString("video"));
                String capa = limpar(rs.getString("capa"));
                String imagem1 = limpar(rs.getString("imagem1"));
                String imagem2 = limpar(rs.getString("imagem2"));

                json.append("{")
                        .append("\"id\": ").append(id).append(",")
                        .append("\"idTermo\": ").append(id).append(",")
                        .append("\"titulo\": \"").append(titulo).append("\",")
                        .append("\"nome\": \"").append(titulo).append("\",")
                        .append("\"descricao\": \"").append(descricao).append("\",")
                        .append("\"nivel\": \"").append(nivel).append("\",")
                        .append("\"video\": \"").append(video).append("\",")
                        .append("\"capa\": \"").append(capa).append("\",")
                        .append("\"imagem1\": \"").append(imagem1).append("\",")
                        .append("\"imagem2\": \"").append(imagem2).append("\"")
                        .append("}");
                primeiro = false;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    json.append("]");
    return json.toString();
}

// Método auxiliar de limpeza dentro do FavoritoDAO
private String limpar(String texto) {
    if (texto == null) return "";
    return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
}
}