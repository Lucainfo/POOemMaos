/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author isabe
 */
import java.sql.Timestamp;

public class Solicitacao {

    private int id;
    private int usuarioId;
    private String termoSolicitado;
    private Timestamp dataSolicitacao;
    private String status;
    private String nomeUsuario;

    public Solicitacao() {
    }

    public Solicitacao(int usuarioId, String termoSolicitado, String status) {
        this.usuarioId = usuarioId;
        this.termoSolicitado = termoSolicitado;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }


    public String getTermoSolicitado() {
        return termoSolicitado;
    }

    public void setTermoSolicitado(String termoSolicitado) {
        this.termoSolicitado = termoSolicitado;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Timestamp getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Timestamp dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
}
