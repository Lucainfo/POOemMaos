/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */


function abrirModalListaSolicitacoes(event) {
  if (event)
        event.preventDefault();

    const tipoUsuario = (localStorage.getItem('tipoUsuario') || '').toUpperCase();

    if (tipoUsuario !== 'TRADUTOR') {
        alert('Acesso restrito: apenas usuários cadastrados como Tradutor têm permissão para acessar lista de solicitados.');
        return; // Interrompe e não abre o modal
    }
    const modal =document.getElementById('modalListaSolicitacoes').classList.remove('escondido');
}

function fecharModalListaSolicitacoes() {
    document.getElementById('modalListaSolicitacoes').classList.add('escondido');
}

function fecharModalListaSolicitacoesFora(event) {
    if (event.target.id === 'modalListaSolicitacoes') {
        fecharModalListaSolicitacoes();
    }
}

/**
 * Armazena a lista completa de termos em memória para o filtro rápido.
 * @type {Array<Object>}
 */
let todosOsTermos = [];

document.addEventListener('DOMContentLoaded', () => {
    carregarGlossario();
    
    // Escuta a digitação no campo de busca/filtro (se existir no HTML)
    const inputBusca = document.getElementById('inputBusca') || document.getElementById('filtroTermos');
    if (inputBusca) {
        inputBusca.addEventListener('input', filtrarTermos);
    }
});

/**
 * Busca todos os termos do glossário no backend (sem cache).
 * @returns {void}
 */
function carregarGlossario() {
    const container = document.getElementById('card-link') || document.getElementById('glossarioContainer');
    if (!container) 
        return;
    console.error("ERRO DE INTERFACE: Não foi encontrado o container do glossário no HTML (procurei por 'containerCards' e 'glossarioContainer')");
        return;

    fetch('ListarTermosServlet?t=' + new Date().getTime(), { cache: 'no-store' })
        .then(res => {
            if (!res.ok) throw new Error("Erro ao buscar termos: " + res.status);
            return res.json();
        })
        .then(termos => {
            todosOsTermos = termos;
            renderizarCardsGlossario(todosOsTermos);
        })
        .catch(err => console.error("Erro ao carregar glossário:", err));
}

/**
 * Renderiza os cards dos termos no container principal.
 * @param {Array<Object>} termos - Lista de termos a renderizar.
 * @returns {void}
 */
function renderizarCardsGlossario(termos) {
    const container = document.getElementById('containerCards') || document.getElementById('glossarioContainer');
    if (!container) return;

    if (!Array.isArray(termos) || termos.length === 0) {
        container.innerHTML = '<p class="mensagem-vazia">Nenhum termo encontrado.</p>';
        return;
    }

    let html = '';
    termos.forEach(termo => {
        const titulo = termo.titulo || termo.nome || 'Sem título';
        const definicao = termo.definicao || termo.descricao || '';
        
        // Clique no card redireciona para a página de detalhes com o ID do termo
        html += `
            <div class="card-termo" onclick="verDetalhesTermo(${termo.id})" style="cursor: pointer;">
                <h3>${titulo}</h3>
                <p>${definicao.substring(0, 100)}${definicao.length > 100 ? '...' : ''}</p>
            </div>
        `;
    });

    container.innerHTML = html;
}

/**
 * Filtra os cards dinamicamente com base no texto digitado no input.
 * @returns {void}
 */
function filtrarTermos() {
    const inputBusca = document.getElementById('inputBusca') || document.getElementById('filtroTermos');
    if (!inputBusca) return;

    const termoBuscado = inputBusca.value.toLowerCase().trim();

    const termosFiltrados = todosOsTermos.filter(termo => {
        const titulo = (termo.titulo || termo.nome || '').toLowerCase();
        const definicao = (termo.definicao || termo.descricao || '').toLowerCase();
        return titulo.includes(termoBuscado) || definicao.includes(termoBuscado);
    });

    renderizarCardsGlossario(termosFiltrados);
}

/**
 * Redireciona o usuário para a página de detalhes do termo selecionado.
 * @param {number|string} id - ID do termo.
 * @returns {void}
 */
function verDetalhesTermo(id) {
    if (id) {
        window.location.href = `termoDetalhe.html?id=${id}`;
    }
}

// --- LÓGICA DA LISTA DE SOLICITAÇÕES ---

/**
 * Busca a lista de solicitações no servidor.
 * @returns {void}
 */
function carregarSolicitacoes() {
    const container = document.getElementById('listaSolicitacoesContainer');
    if (!container) return;

    fetch('SolicitacaotServlet?t=' + new Date().getTime(), { cache: 'no-store' })
        .then(res => {
            if (!res.ok) throw new Error("Erro ao buscar solicitações: " + res.status);
            return res.json();
        })
        .then(lista => {
            renderizarListaSolicitacoes(lista);
        })
        .catch(err => console.error("Erro ao carregar solicitações:", err));
}

/**
 * Renderiza a lista de solicitações pendentes/feitas.
 * @param {Array<Object>} lista - Array de solicitações.
 * @returns {void}
 */
function renderizarListaSolicitacoes(lista) {
    const container = document.getElementById('listaSolicitacoesContainer');
    if (!container) return;

    if (!Array.isArray(lista) || lista.length === 0) {
        container.innerHTML = '<p class="mensagem-vazia">Nenhuma solicitação no momento.</p>';
        return;
    }

    let html = '<ul class="lista-solicitacoes-container" style="list-style: none; padding: 0; margin: 0;">';

    lista.forEach(item => {
        const nomeTermo = item.termoSolicitado || item.termo || 'Termo sem nome';
        const status = (item.status || 'PENDENTE').toUpperCase();
        const termoEscapado = nomeTermo.replace(/'/g, "\\'");

        const isFeito = (status === 'FEITO' || status === 'CONCLUIDO');

        const acaoClique = isFeito 
            ? '' 
            : `onclick="selecionarTermoParaAdicionar(${item.id}, '${termoEscapado}')"`;

        const estiloCursor = isFeito ? 'cursor: default; opacity: 0.75;' : 'cursor: pointer;';
        const corStatus = isFeito 
            ? 'background: #d4edda; color: #155724;' 
            : 'background: #fff3cd; color: #856404;';

        html += `
            <li class="item-solicitacao ${status.toLowerCase()}" 
                ${acaoClique} 
                style="${estiloCursor} padding: 10px; border-bottom: 1px solid #ddd; display: flex; justify-content: space-between; align-items: center;">
                
                <span class="nome-termo" style="font-weight: bold;">${nomeTermo}</span>
                <span class="status-termo" style="font-size: 0.85em; ${corStatus} padding: 3px 8px; border-radius: 4px; font-weight: bold;">
                    ${isFeito ? 'FEITO' : 'PENDENTE'}
                </span>
            </li>
        `;
    });

    html += '</ul>';
    container.innerHTML = html;
}

function selecionarTermoParaAdicionar(idSolicitacao, termo) {
    solicitacaoSelecionadaId = idSolicitacao;

    // 1. Preenche o campo de título/nome no modal de adição de termo
  const inputTitulo = document.querySelector('input[name="titulo"]');
    if (inputTitulo) {
        inputTitulo.value = termo;
    }

    // 2. Armazena o ID também em um input hidden, se existir no HTML
    const inputHiddenId = document.getElementById('solicitacaoIdHidden');
    if (inputHiddenId) {
        inputHiddenId.value = idSolicitacao;
    }

    // 3. Fecha a lista de solicitações e abre o modal de adicionar termo
    fecharModalListaSolicitacoes();

    const modalAdicionar = document.getElementById('modalAdicionarTermo');
    if (modalAdicionar) {
        modalAdicionar.classList.remove('escondido');
    }
}

/**
 * Função responsável por salvar o novo termo
 */
function salvarNovoTermo(event) {
    if (event) event.preventDefault();

    const form = document.getElementById('formAdicionarTermo');
    if (!form) return;

    const formData = new FormData(form);

    // Garante que o id da solicitação seja enviado se tiver sido selecionado
    if (solicitacaoSelecionadaId) {
        formData.append('solicitacaoId', solicitacaoSelecionadaId);
    }

    fetch('AdicionarTermoServlet', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error("Erro ao salvar o termo: " + response.status);
        return response.text();
    })
    .then(() => {
        alert("Termo adicionado com sucesso!");

        // Reset da solicitação guardada
        solicitacaoSelecionadaId = null;

        // 1. Fecha o modal de cadastro
        const modalAdicionar = document.getElementById('modalAdicionarTermo');
        if (modalAdicionar) modalAdicionar.classList.add('escondido');
        form.reset();

        // 2. Atualiza os cards da página principal
        if (typeof carregarGlossario === 'function') {
            carregarGlossario();
        }

        // 3. Atualiza a lista de solicitações
        if (typeof carregarSolicitacoes === 'function') {
            carregarSolicitacoes();
        }
    })
    .catch(err => {
        console.error("Erro ao salvar o termo:", err);
        alert("Erro ao salvar o termo. Verifique o console.");
    });
}