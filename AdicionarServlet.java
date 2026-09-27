/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Server;

import Model.SolicitacaoDAO;
import Model.TermoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;

/**
 *
 * @author isabe
 */
@WebServlet(name = "AdicionarServlet", urlPatterns = {"/AdicionarServlet"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB (Tamanho em que o arquivo começa a ser escrito no disco temporário)
    maxFileSize = 1024 * 1024 * 50,       // 50MB (Tamanho máximo por arquivo - importante para vídeos)
    maxRequestSize = 1024 * 1024 * 100    // 100MB (Tamanho máximo total da requisição)
)
public class AdicionarServlet extends HttpServlet {

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
        processRequest(request, response);
    }
    private String salvarArquivo(Part part, String uploadPath) throws IOException {
        
        if (part != null && part.getSize() > 0) {
            String fileName = part.getSubmittedFileName(); 
            
            String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
            
            String filePath = uploadPath + File.separator + uniqueFileName;
            
           
            part.write(filePath); 
            
            
            return "uploads/" + uniqueFileName;
        }
        return null; 
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    request.setCharacterEncoding("UTF-8");
    System.out.println("CHEGOU NO SERVLET DE ADICIONAR TERMO!");

    // 1. Receber parâmetros do formulário
    String solicitacaoIdStr = request.getParameter("solicitacaoId");
    String titulo = request.getParameter("titulo");
    String nivel = request.getParameter("nivel");
    String descricao = request.getParameter("descricao");
    String exemplo = request.getParameter("exemplo");

    // 2. Preparar diretório para uploads
    String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
        uploadDir.mkdir();
    }

    // 3. Processar upload de arquivos
    String video = salvarArquivo(request.getPart("video"), uploadPath);
    String capa = salvarArquivo(request.getPart("capa"), uploadPath);
    String imagem1 = salvarArquivo(request.getPart("imagem1"), uploadPath);
    String imagem2 = salvarArquivo(request.getPart("imagem2"), uploadPath);

    // 4. Salvar o termo completo no banco de dados
    TermoDAO dao = new TermoDAO();
    boolean sucesso = dao.salvarTermoCompleto(titulo, nivel, descricao, video, capa, imagem1, imagem2, exemplo);

    // 5. Se o termo foi salvo e veio de uma solicitação, atualiza o status para "FEITO"
    if (sucesso) {
        if (solicitacaoIdStr != null && !solicitacaoIdStr.trim().isEmpty()) {
            try {
                int solicitacaoId = Integer.parseInt(solicitacaoIdStr);
                
                SolicitacaoDAO solicitacaoDAO = new SolicitacaoDAO();
                solicitacaoDAO.atualizarStatus(solicitacaoId, "FEITO");
                System.out.println("Status da solicitação " + solicitacaoId + " atualizado para FEITO com sucesso!");
                
            } catch (NumberFormatException e) {
                System.out.println("AVISO: ID de solicitação inválido: " + solicitacaoIdStr);
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Termo adicionado e solicitação atualizada!");
    } else {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao salvar o termo no banco de dados.");
    }
        
    }


    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
