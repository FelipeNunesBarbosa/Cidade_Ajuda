// Obter a data atual no formato "YYYY-MM-DD"
function getCurrentDate() {
    const today = new Date();
    const year = today.getFullYear();
    let month = today.getMonth() + 1;
    let day = today.getDate();

    // Adicionar um zero à esquerda se o mês ou dia for menor que 10
    if (month < 10) {
        month = '0' + month;
    }
    if (day < 10) {
        day = '0' + day;
    }

    return year + '-' + month + '-' + day;
}

// Desabilita funções se o status estiver Encerrado
document.addEventListener('DOMContentLoaded', () => {
    const status = document.getElementById('status').value;

    if (status === "Encerrado") {
        // Desabilitar o textarea
        const novaConversa = document.getElementById('novaConversa');
              novaConversa.classList.add('readonly');
              novaConversa.setAttribute('readonly', true);

        // Desabilitar todos os botões com a classe 'button'
        const buttons = document.getElementsByClassName('button');
        for (let i = 0; i < buttons.length; i++) {
            buttons[i].setAttribute('disabled', true);
        }
    }
});

// Modal atendente
function openModal() {
    document.getElementById("myModal").style.display = "block";
}
function closeModal() {
    document.getElementById("myModal").style.display = "none";
}
function setAtendente(id_atendente, nome) {
    document.getElementById("id_atendente").value = id_atendente;
    document.getElementById("atendente").value = nome;
    closeModal()
}