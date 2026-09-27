/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */
let solicitacaoSelecionadaId = null;

document.addEventListener('DOMContentLoaded', () => {
    verificarAcessoECarregar();
});

/**
 * Carrega as solicitações apenas para Intérpretes e Tradutores.
 * @returns {void}
 */
function verificarAcessoECarregar() {
    const tipoUsuario = (localStorage.getItem('tipoUsuario') || '').toUpperCase();

    if ((tipoUsuario === 'INTERPRETE' || tipoUsuario === 'TRADUTOR') && typeof carregarSolicitacoes === 'function') {
        carregarSolicitacoes();
    }
}

function abrirModalSolicitacao(event) {
    if (event) event.preventDefault();

    const modal = document.getElementById('modalSolicitacao');
    const conteudoForm = document.getElementById('conteudoFormulario');
    const msgSucesso = document.getElementById('mensagemSucesso');

    if (modal) {
        modal.classList.remove('escondido');

        // Reseta o estado do modal para exibir o formulário e esconder mensagens anteriores
        if (conteudoForm) conteudoForm.classList.remove('escondido');
        if (msgSucesso) msgSucesso.classList.add('escondido');

        // Limpa o campo de entrada
        const campoInput = document.getElementById('campo-solicitacao');
        if (campoInput) campoInput.value = '';
    }
}

// Fecha o modal ao clicar no botão 'X'
function fecharModalSolicitacao() {
    const modal = document.getElementById('modalSolicitacao');
    if (modal) {
        modal.classList.add('escondido');
    }
}

// Fecha o modal ao clicar no fundo escuro fora da janela
function fecharModalFora(event) {
    const modal = document.getElementById('modalSolicitacao');
    if (event.target === modal) {
        fecharModalSolicitacao();
    }
}

/**
 * Envia a solicitação para o backend.
 * @param {Event} [event] - Evento de submit.
 * @returns {void}
 */
function enviarSolicitacao(event) {
    if (event) event.preventDefault();

    // 1. Captura o campo de texto
    const inputCampo = document.getElementById('campo-solicitacao');
    const valorTermo = inputCampo ? inputCampo.value.trim() : '';

    if (!valorTermo) {
        alert("Por favor, digite o termo antes de enviar.");
        return;
    }

    // 2. Prepara os dados para envio
    const params = new URLSearchParams();
    // Chave alterada para 'termoSolicitado' para bater com o request.getParameter do Java
    params.append('termoSolicitado', valorTermo); 
    
    // Opcional: Se você tiver o ID do usuário no JS, passe aqui. Caso contrário, o Java usará o padrão (1).
    // params.append('usuarioId', '1');

    // 3. Envia para o Servlet
    fetch('SolicitacaotServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Erro no servidor: HTTP ${response.status}`);
        }
        return response.text();
    })
    .then(mensagem => {
        const conteudoForm = document.getElementById('conteudoFormulario');
        const msgSucesso = document.getElementById('mensagemSucesso');

        if (conteudoForm) conteudoForm.classList.add('escondido');
        if (msgSucesso) msgSucesso.classList.remove('escondido');

        if (typeof carregarSolicitacoes === 'function') {
            carregarSolicitacoes();
        }
    })
    .catch(erro => {
        console.error("Erro na solicitação:", erro);
        alert("Erro ao enviar a solicitação. Verifique o console do navegador.");
    });
}
/**
 * Busca os dados específicos de um termo pelo ID no backend.
 * @param {string|number} id - ID do termo.
 * @returns {void}
 */
function carregarDetalhesDoTermo(id) {
    fetch(`BuscarTermoServlet?id=${id}`)
        .then(res => res.json())
        .then(termo => {
            // Preenche os dados na tela de detalhes
            document.getElementById('tituloTermo').textContent = termo.titulo;
            document.getElementById('definicaoTermo').textContent = termo.definicao;
            
            if (termo.exemplo) {
                document.getElementById('exemploCodigo').textContent = termo.exemplo;
            }
        })
        .catch(err => console.error("Erro ao carregar detalhes do termo:", err));
};