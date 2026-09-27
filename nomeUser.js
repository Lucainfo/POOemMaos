/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

document.addEventListener('DOMContentLoaded', () => {
    carregarSaudacaoCabecalho();
});


async function carregarSaudacaoCabecalho() {
    try {
        const resposta = await fetch('PerfilServlet');
        
        if (resposta.ok) {
            const usuario = await resposta.json();
            const spanSaudacao = document.getElementById('saudacao-usuario');
            
            if (spanSaudacao && usuario.nome) {
                let primeiroNome = usuario.nome.split(' ')[0];
                spanSaudacao.textContent = 'Olá, ' + primeiroNome;
            }
        }
    } catch (erro) {
        console.error("Erro ao carregar a saudação do cabeçalho:", erro);
    }
}
    
    
