/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Server;

import Model.Solicitacao;
import Model.SolicitacaoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author isabe
 */
@WebServlet(name = "SolicitacaotServlet", urlPatterns = {"/SolicitacaotServlet"})
public class SolicitacaotServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    SolicitacaoDAO dao = new SolicitacaoDAO();
    List<Solicitacao> lista = dao.listarPendentes();

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < lista.size(); i++) {
        Solicitacao s = lista.get(i);
        String termo = s.getTermoSolicitado() != null ? s.getTermoSolicitado().replace("\"", "\\\"") : "";
        String status = s.getStatus() != null ? s.getStatus() : "PENDENTE";

        json.append("{")
            .append("\"id\":").append(s.getId()).append(",")
            .append("\"termoSolicitado\":\"").append(termo).append("\",")
            .append("\"status\":\"").append(status).append("\"")
            .append("}");

        if (i < lista.size() - 1) {
            json.append(",");
        }
    }
    json.append("]");

    PrintWriter out = response.getWriter();
    out.print(json.toString());
    out.flush();
}
    

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       System.out.println("========== NOVA TENTATIVA DE SOLICITAÇÃO ==========");
    request.setCharacterEncoding("UTF-8");

    String termo = request.getParameter("termoSolicitado");
    String usuarioIdStr = request.getParameter("usuarioId");
    
    System.out.println("1. Termo que chegou do JS: " + termo);
    System.out.println("2. ID de usuário que chegou do JS: " + usuarioIdStr);
    
    Solicitacao solicitacao = new Solicitacao();
    solicitacao.setTermoSolicitado(termo);
    
    try {
        solicitacao.setUsuarioId(Integer.parseInt(usuarioIdStr));
    } catch (NumberFormatException e) {
        System.out.println("ERRO: ID inválido, usando o ID 1 como padrão.");
        solicitacao.setUsuarioId(1);
    }

    System.out.println("3. Chamando o DAO...");
    SolicitacaoDAO dao = new SolicitacaoDAO();
    boolean salvou = dao.registrar(solicitacao);
    
    System.out.println("4. O DAO conseguiu salvar? " + salvou);
    System.out.println("===================================================");

    if (salvou) {
        response.setStatus(HttpServletResponse.SC_OK);
    } else {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
