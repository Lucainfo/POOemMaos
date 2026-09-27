/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

function toggleNotificacoes(event) {
    if (event) event.stopPropagation();
    const popover = document.getElementById('popoverNotificacoes');
    popover.classList.toggle('escondido');
}

// Fecha o popover caso clique fora dele
window.addEventListener('click', function(e) {
    const popover = document.getElementById('popoverNotificacoes');
    if (popover && !popover.classList.contains('escondido')) {
        if (!popover.contains(e.target)) {
            popover.classList.add('escondido');
        }
    }
});
