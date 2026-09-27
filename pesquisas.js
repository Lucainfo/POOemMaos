/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function realizarBusca(inputElement) {
    const input = inputElement || document.getElementById('campo-busca');

    if (!input) {
        console.error("ERRO: Campo de busca não encontrado no HTML!");
        return;
    }
    
    const termo = input.value.toLowerCase().trim();

    const cards = document.querySelectorAll('article');

    cards.forEach(card => {
        const textoDoCard = card.textContent.toLowerCase();
          if (termo === '' || textoDoCard.includes(termo)) {
            card.style.display = '';
        } else {
            card.style.display = 'none';
        }
    });


}