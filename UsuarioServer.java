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

/**
 *
 * @author isabe
 */
@WebServlet(name = "UsuarioServer", urlPatterns = {"/UsuarioServer"})
public class UsuarioServer extends HttpServlet {

    private UsuarioDAO user = new UsuarioDAO();

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
        doPost(request, response);
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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String acao = request.getParameter("acao");

            if (acao == null || acao.trim().isEmpty()) {
                acao = "cadastrar"; // Ação padrão caso não seja informada
            }

            switch (acao.toLowerCase()) {
                case "cadastrar":
                    tratarCadastro(request, out);
                    break;
                case "login":
                    tratarLogin(request, out);
                    break;
                case "atualizar":
                    tratarAtualizacao(request, out);
                    break;
                default:
                    out.print("{\"sucesso\": false, \"mensagem\": \"Ação não reconhecida.\"}");
                    break;
            }

        } catch (Exception e) {
            System.err.println("Erro no UsuarioServer: " + e.getMessage());
            e.printStackTrace();

            String msgErro = (e.getMessage() != null) ? e.getMessage().replace("\"", "'") : "Erro no servidor";
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro interno: " + msgErro + "\"}");
        } finally {
            out.flush();
        }
    }

    private void tratarCadastro(HttpServletRequest request, PrintWriter out) {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String tipo = request.getParameter("tipo");

        if (nome == null || email == null || senha == null ||
            nome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().isEmpty()) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Preencha todos os campos obrigatórios!\"}");
            return;
        }

        tipo = (tipo == null || tipo.trim().isEmpty()) ? "ALUNO" : tipo.toUpperCase().trim();

        Usuario novoUsuario = new Usuario(nome, email, senha, tipo);
        boolean sucesso = user.cadastrar(novoUsuario);

        if (sucesso) {
            out.print("{\"sucesso\": true, \"mensagem\": \"Usuário cadastrado com sucesso!\"}");
        } else {
            out.print("{\"sucesso\": false, \"mensagem\": \"E-mail já cadastrado ou erro ao salvar.\"}");
        }
    }

    private void tratarLogin(HttpServletRequest request, PrintWriter out) {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || senha == null || email.trim().isEmpty() || senha.trim().isEmpty()) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Informe o e-mail e a senha!\"}");
            return;
        }

        // Exemplo: Método autenticar no seu DAO que retorna o objeto Usuario ou null
        Usuario usuarioLogado = user.autenticar(email, senha);

        if (usuarioLogado != null) {
            out.print("{\"sucesso\": true, \"mensagem\": \"Login realizado com sucesso!\", " +
                      "\"nome\": \"" + usuarioLogado.getNome() + "\", " +
                      "\"email\": \"" + usuarioLogado.getEmail() + "\", " +
                      "\"tipo\": \"" + usuarioLogado.getTipo() + "\"}");
        } else {
            out.print("{\"sucesso\": false, \"mensagem\": \"E-mail ou senha incorretos.\"}");
        }
    }

    private void tratarAtualizacao(HttpServletRequest request, PrintWriter out) {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String tipo = request.getParameter("tipo");

        if (nome == null || email == null || nome.trim().isEmpty() || email.trim().isEmpty()) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Nome e e-mail são obrigatórios!\"}");
            return;
        }

        boolean sucesso = user.atualizar(nome, email, tipo);

        if (sucesso) {
            out.print("{\"sucesso\": true, \"mensagem\": \"Perfil atualizado com sucesso!\"}");
        } else {
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro ao atualizar dados.\"}");
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

