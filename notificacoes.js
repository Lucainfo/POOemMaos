/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */
function carregarNotificacoes() {
    const container = document.getElementById('container-notificacoes');
    if (!container) return;

    fetch('ListarNotificacoesServlet')
        .then(res => res.json())
        .then(notificacoes => {
            if (!notificacoes || notificacoes.length === 0) {
                container.innerHTML = '<p class="sem-notif">Nenhuma notificação encontrada.</p>';
                return;
            }

            let html = '';
            notificacoes.forEach(n => {
                const classeLida = n.lida ? 'notif-lida' : 'notif-nova';
                html += `
                    <div class="card-notificacao ${classeLida}">
                        <p class="notif-mensagem">${n.mensagem}</p>
                        <span class="notif-data">${n.dataCriacao || ''}</span>
                    </div>
                `;
            });

            container.innerHTML = html;
        })
        .catch(err => console.error('Erro ao buscar notificações:', err));
}

// Executa ao carregar a página de notificações ou abrir o painel
document.addEventListener('DOMContentLoaded', carregarNotificacoes);

