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
import Model.Solicitacao;

public class SolicitacaoDAO {
    public boolean registrar(Solicitacao s) {
        String sql = "INSERT INTO solicitacoes (usuario_id, termo_solicitado, status) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, s.getUsuarioId());
            stmt.setString(2, s.getTermoSolicitado());
            stmt.setString(3, s.getStatus() != null ? s.getStatus() : "PENDENTE");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Solicitacao> listarPendentes() {
        List<Solicitacao> lista = new ArrayList<>();
        // LEFT JOIN garante que a solicitação não desapareça caso o usuario_id seja NULL
        String sql = "SELECT s.*, u.nome AS nome_usuario FROM solicitacoes s " +
                     "LEFT JOIN usuarios u ON s.usuario_id = u.id " +
                     "WHERE s.status = 'PENDENTE' ORDER BY s.data_solicitacao DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Solicitacao s = new Solicitacao();
                s.setId(rs.getInt("id"));
                s.setUsuarioId(rs.getInt("usuario_id"));
                s.setTermoSolicitado(rs.getString("termo_solicitado"));
                s.setStatus(rs.getString("status"));
                s.setNomeUsuario(rs.getString("nome_usuario"));
                s.setDataSolicitacao(rs.getTimestamp("data_solicitacao"));
                
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizarStatus(int solicitacaoId, String status) {
        String sql = "UPDATE solicitacoes SET status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, solicitacaoId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean concluirSolicitacaoENotificar(int idSolicitacao, String tituloTermo) {
        Connection con = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtSelect = null;
        PreparedStatement stmtNotif = null;
        ResultSet rs = null;

        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false); // Inicia transação de banco de dados

            // 1. Busca o usuario_id da solicitação
            String sqlSelect = "SELECT usuario_id FROM solicitacoes WHERE id = ?";
            stmtSelect = con.prepareStatement(sqlSelect);
            stmtSelect.setInt(1, idSolicitacao);
            rs = stmtSelect.executeQuery();

            int usuarioId = 0;
            if (rs.next()) {
                usuarioId = rs.getInt("usuario_id");
            }

            if (usuarioId > 0) {
                // 2. Atualiza o status da solicitação de 'pendente' para 'feito'
                String sqlUpdate = "UPDATE solicitacoes SET status = 'feito' WHERE id = ?";
                stmtUpdate = con.prepareStatement(sqlUpdate);
                stmtUpdate.setInt(1, idSolicitacao);
                stmtUpdate.executeUpdate();

                // 3. Insere a notificação vinculada ao usuario_id
                String sqlNotif = "INSERT INTO notificacoes (usuario_id, mensagem, lida) VALUES (?, ?, false)";
                stmtNotif = con.prepareStatement(sqlNotif);
                stmtNotif.setInt(1, usuarioId);
                stmtNotif.setString(2, "Sua solicitação para o termo '" + tituloTermo + "' foi atendida!");
                stmtNotif.executeUpdate();

                con.commit(); // Confirma ambas as operações no banco
                return true;
            }

            con.rollback();
            return false;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            System.err.println("Erro ao concluir solicitação e notificar: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            ConnectionFactory.closeConnection(con, stmtUpdate);
            ConnectionFactory.closeConnection(null, stmtSelect, rs);
            ConnectionFactory.closeConnection(null, stmtNotif);
        }
    }
}