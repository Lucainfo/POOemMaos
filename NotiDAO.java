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
 * @author isabe
 */
public class NotiDAO {
    public String listarNotificacoesPorUsuarioJson(int usuarioId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder json = new StringBuilder("[");

        String sql = "SELECT id, mensagem, lida, data_criacao FROM notificacoes WHERE usuario_id = ? ORDER BY id DESC";

        try {
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            rs = stmt.executeQuery();

            boolean primeiro = true;
            while (rs.next()) {
                if (!primeiro) json.append(",");

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"mensagem\":\"").append(tratarJson(rs.getString("mensagem"))).append("\",")
                    .append("\"lida\":").append(rs.getBoolean("lida")).append(",")
                    .append("\"dataCriacao\":\"").append(rs.getTimestamp("data_criacao")).append("\"")
                    .append("}");

                primeiro = false;
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar notificações: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }

        json.append("]");
        return json.toString();
    }

    private String tratarJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

}
