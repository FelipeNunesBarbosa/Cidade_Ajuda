function openModal(element) {
    
    // Obtenha os dados do atributo data- do elemento
    var id = element.getAttribute('data-id-pessoa');
    var nome = element.getAttribute('data-nome');
    var sobrenome = element.getAttribute('data-sobrenome');
    var perfil = element.getAttribute('data-perfil');
    var select = document.getElementById('opcoes');

    // Formatando o valor do perfil
    if (perfil === 'ATENDENTE') {
        perfil = 'Atendente';
        select.value = 'opcao1';
    } else if (perfil === 'USUARIO') {
        perfil = 'Usuário';
        select.value = 'opcao2';
    } else {
        perfil = 'Perfil Desconhecido';
    }

    document.getElementById('modalId').value = id;
    document.getElementById('modalNome').innerText = nome;
    document.getElementById('modalSobrenome').innerText = sobrenome;
    document.getElementById('modalPerfil').innerText = perfil;
    
    // Exiba o modal
    document.getElementById('pessoaModal').style.display = "block";
}

function closeModal() {
    document.getElementById('pessoaModal').style.display = "none";
}

// Fecha o modal se o usuário clicar fora dele
window.onclick = function(event) {
    var modal = document.getElementById('pessoaModal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
