/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Aluno
 */
public class TermoDAO {

    public String listarTodosTermosJson() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder json = new StringBuilder("[");

        // Ordena pelo ID do mais recente ao mais antigo
        String sql = "SELECT id, titulo, descricao, nivel, video, capa, imagem1, imagem2, exemplo FROM termos ORDER BY id DESC";

        try {
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            boolean primeiro = true;

            while (rs.next()) {
                if (!primeiro) {
                    json.append(",");
                }

                int id = rs.getInt("id");
                String titulo = tratarJsonString(rs.getString("titulo"));
                String descricao = tratarJsonString(rs.getString("descricao"));
                String nivel = tratarJsonString(rs.getString("nivel")).trim();
                String capa = tratarJsonString(rs.getString("capa"));
                String video = tratarJsonString(rs.getString("video"));
                String imagem1 = tratarJsonString(rs.getString("imagem1"));
                String imagem2 = tratarJsonString(rs.getString("imagem2"));
                String exemplo = tratarJsonString(rs.getString("exemplo"));

                json.append("{")
                        .append("\"id\": ").append(id).append(",")
                        .append("\"idTermo\": ").append(id).append(",") // Mantém idTermo para compatibilidade
                        .append("\"titulo\": \"").append(titulo).append("\",")
                        .append("\"descricao\": \"").append(descricao).append("\",")
                        .append("\"nivel\": \"").append(nivel).append("\",")
                        .append("\"capa\": \"").append(capa).append("\",")
                        .append("\"video\": \"").append(video).append("\",")
                        .append("\"imagem1\": \"").append(imagem1).append("\",")
                        .append("\"imagem2\": \"").append(imagem2).append("\",")
                        .append("\"exemplo\": \"").append(exemplo).append("\"")
                        .append("}");

                primeiro = false;
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar termos no DAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }

        json.append("]");
        return json.toString();
    }

    public boolean salvarTermoCompleto(String titulo, String nivel, String descricao, String video, String capa, String imagem1, String imagem2, String exemplo) {
        String sql = "INSERT INTO termos (titulo, nivel, descricao, video, capa, imagem1, imagem2, exemplo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            stmt.setString(2, nivel);
            stmt.setString(3, descricao);
            stmt.setString(4, video);
            stmt.setString(5, capa);
            stmt.setString(6, imagem1);
            stmt.setString(7, imagem2);
            stmt.setString(8, exemplo);
            

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            System.err.println("Erro ao salvar termo no banco: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String buscarTermosJson(String busca) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder json = new StringBuilder("[");

        String sql = "SELECT id, titulo, descricao, nivel, capa FROM termos WHERE titulo LIKE ? OR descricao LIKE ?";

        try {
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(sql);
            String parametroBusca = "%" + busca + "%";
            stmt.setString(1, parametroBusca);
            stmt.setString(2, parametroBusca);
            rs = stmt.executeQuery();

            boolean primeiro = true;
            while (rs.next()) {
                if (!primeiro) {
                    json.append(",");
                }

                int id = rs.getInt("id");
                String titulo = tratarJsonString(rs.getString("titulo"));
                String descricao = tratarJsonString(rs.getString("descricao"));
                String nivel = tratarJsonString(rs.getString("nivel")).trim();
                String capa = tratarJsonString(rs.getString("capa"));

                json.append("{")
                        .append("\"id\": ").append(id).append(",")
                        .append("\"idTermo\": ").append(id).append(",")
                        .append("\"titulo\": \"").append(titulo).append("\",")
                        .append("\"descricao\": \"").append(descricao).append("\",")
                        .append("\"nivel\": \"").append(nivel).append("\",")
                        .append("\"capa\": \"").append(capa).append("\"")
                        .append("}");

                primeiro = false;
            }
        } catch (Exception e) {
            System.err.println("Erro na busca de termos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }

        json.append("]");
        return json.toString();
    }

    private String tratarJsonString(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "/") // Transforma caminhos Windows (uploads\a.jpg) para barras Web (uploads/a.jpg)
                .replace("\"", "\\\"") // Escapa aspas
                .replace("\n", "\\n") // Escapa quebras de linha
                .replace("\r", "");
    }
}
