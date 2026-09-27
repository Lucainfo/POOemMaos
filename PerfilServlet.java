/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Server;

import Model.Usuario;
import Model.UsuarioDAO;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "PerfilServlet", urlPatterns = {"/PerfilServlet"})
public class PerfilServlet extends HttpServlet {
    private UsuarioDAO dao = new UsuarioDAO();

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
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuarioLogado") != null) {
            Usuario u = (Usuario) session.getAttribute("usuarioLogado");

            try (PrintWriter out = response.getWriter()) {
                out.print(String.format(
                    "{\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"tipo\": \"%s\"}",
                    u.getId(), u.getNome(), u.getEmail(), u.getTipo()
                ));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"status\": \"erro\", \"mensagem\": \"Não autenticado.\"}");
            }
        }
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
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuarioLogado") != null) {
            
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String tipo = request.getParameter("tipo");

           
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setTipo(tipo);

          
            boolean sucesso = dao.atualizar(nome, email, tipo);

            try (PrintWriter out = response.getWriter()) {
                if (sucesso) {
                    // Atualiza a sessão com o objeto modificado
                    session.setAttribute("usuarioLogado", usuario);
                    out.print("{\"status\": \"sucesso\", \"mensagem\": \"Perfil atualizado com sucesso!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\": \"erro\", \"mensagem\": \"Erro ao atualizar. O e-mail pode já estar cadastrado.\"}");
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
