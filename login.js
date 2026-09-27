/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

function mostrarTela(idTela) {
    document.querySelectorAll('.card-entrada').forEach(card => card.classList.remove('ativo'));
    document.getElementById(idTela).classList.add('ativo');
}
function realizarCadastro(event) {
    event.preventDefault();

    // Captura a tag <form> que disparou o evento de submit (possui a função reset)
    const formElement = event.target;

    // Busca pelos IDs específicos ou, em fallback, pela ordem dos inputs dentro do form
    const nome = document.getElementById('cad-nome')?.value || formElement.querySelectorAll('input')[0]?.value;
    const email = document.getElementById('cad-email')?.value || formElement.querySelectorAll('input')[1]?.value;
    const senha = document.getElementById('cad-senha')?.value || formElement.querySelectorAll('input')[2]?.value;
    const select = document.getElementById('cad-tipo') || formElement.querySelector('select');
    const tipo = select ? select.value : 'ALUNO';

    const params = new URLSearchParams();
    params.append('acao', 'cadastrar'); // Informa a ação para o UsuarioServer
    params.append('nome', nome);
    params.append('email', email);
    params.append('senha', senha);
    params.append('tipo', tipo);

    fetch('UsuarioServer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        if (data && data.sucesso) {
            alert(data.mensagem || 'Cadastro realizado com sucesso! Faça seu login.');

            // Agora reseta o <form> corretamente sem dar erro
            if (typeof formElement.reset === 'function') {
                formElement.reset();
            }

            mostrarTela('tela-login');
        } else {
            alert(data.mensagem || 'Erro ao realizar cadastro.');
        }
    })
    .catch(erro => {
        console.error('Erro na requisição de cadastro:', erro);
        alert('Falha ao conectar com o servidor. Verifique se o servidor Java está rodando.');
    });
}

function realizarLogin(event) {
    event.preventDefault();

    const formElement = event.target;

    const email = document.getElementById('login-email')?.value || formElement.querySelectorAll('input')[0]?.value;
    const senha = document.getElementById('login-senha')?.value || formElement.querySelectorAll('input')[1]?.value;
    
    // CONFIRMAÇÃO NO CONSOLE: Veja se estes valores aparecem no F12
    console.log("Tentando logar com:", email, senha);

    const params = new URLSearchParams();
    params.append('acao', 'login'); // Informa a ação para o UsuarioServer
    params.append('email', email);
    params.append('senha', senha);

    // Ajustado para apontar para o 'UsuarioServer' unificado
    fetch('LoginServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        if (data && data.sucesso) {
            // Guarda as informações retornadas pelo Java no localStorage
            localStorage.setItem('nomeUsuario', data.nome || '');
            localStorage.setItem('emailUsuario', data.email || email);
            localStorage.setItem('tipoUsuario', data.tipo || 'ALUNO');

            window.location.href = 'PagPrincipal.html';
        } else {
            alert(data.mensagem || 'E-mail ou senha incorretos.');
        }
    })
    .catch(erro => {
        console.error('Erro no login:', erro);
        alert('Falha ao conectar com o servidor.');
    });
}