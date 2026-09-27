/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Server;

import Model.FavoritoDAO;
import Model.Termo;
import Model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 *
 * @author isabe
 */

@WebServlet(name = "FavoritosServlet", urlPatterns = {"/FavoritosServlet"})
public class FavoritosServlet extends HttpServlet {

    FavoritoDAO favs = new FavoritoDAO();

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
        HttpSession session = request.getSession(false);
        int idUsuario = obterIdUsuario(session);

        
        if (idUsuario == -1) {
            response.setStatus(401);
            return;
        }

        String jsonResposta = favs.buscarFavorito(idUsuario);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResposta);

    }

    // Função auxiliar para evitar que quebras de linha ou aspas no texto quebrem o JSON
    private String limparJSON(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
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
       HttpSession session = request.getSession(false);
        int idUsuario = obterIdUsuario(session);

        // Valida se o usuário está logado usando a função auxiliar
        if (idUsuario == -1) {
            response.setStatus(401);
            return;
        }

     
        String idTermoParam = request.getParameter("idTermo");

        if (idTermoParam == null || idTermoParam.isEmpty() || idTermoParam.equals("undefined")) {
            response.setStatus(400);
            return;
        }

        int idTermo = Integer.parseInt(idTermoParam);

        boolean novoStatus = favs.alternarFavorito(idUsuario, idTermo);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"favoritado\": " + novoStatus + "}");
    }

    private int obterIdUsuario(HttpSession session) {
        if (session == null) {
            return -1;
        }

        Object objSessao = session.getAttribute("usuarioLogado");

        if (objSessao instanceof Model.Usuario) {
            Model.Usuario u = (Model.Usuario) objSessao;
            return u.getId();
        }

        return -1;
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

