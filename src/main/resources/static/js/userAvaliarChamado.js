// Pegar o modal
var modal = document.getElementById("myModal");

// Pegar o elemento <span> que fecha o modal
var span = document.getElementsByClassName("close")[0];

// Quando o usuário clicar em <span> (x), fechar o modal
span.onclick = function() {
    modal.style.display = "none";
}

// Quando o usuário clicar em qualquer lugar fora do modal, fechá-lo
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

// Função para fechar o modal
function closeModal() {
    modal.style.display = "none";
}

// Função para abrir o modal e configurar o título
function avaliarChamado(idChamado) {
    var header = document.getElementById("modalHeader");
    header.textContent = "Avaliar Chamado " + idChamado;
    modal.style.display = "block";
}

// Manipular o envio do formulário
document.getElementById("avaliacaoForm").onsubmit = function(event) {
    event.preventDefault();
    alert("Avaliação salva com sucesso!");
    closeModal();
}