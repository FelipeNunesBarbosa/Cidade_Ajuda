let cep = document.querySelector('#cep');
let rua = document.querySelector('#rua');
let bairro = document.querySelector('#bairro');
let cidade = document.querySelector('#cidade');
let estado = document.querySelector('#estado');
let btnBuscarCepModal = document.querySelector('#btnBuscarCepModal');
let rua_modal = document.querySelector('#rua_modal');
let cidade_modal = document.querySelector('#cidade_modal');
let estado_modal = document.querySelector('#estado_modal');
let listaCep = document.querySelector('#listaCep');

// API ViaCEP
cep.addEventListener('blur', function (e) {
    let cep = e.target.value;
    let script = document.createElement('script');
    script.src = 'https://viacep.com.br/ws/' + cep + '/json/?callback=popularForm';
    document.body.appendChild(script);
})

function popularForm(resposta) {
    if ("erro" in resposta) {
        alert('CEP não encontrado');
        return;
    }
    console.log(resposta)
    rua.value = resposta.logradouro;
    bairro.value = resposta.bairro;
    cidade.value = resposta.localidade;
    estado.value = resposta.uf;
}

// Modal não sei meu CEP
function openModal() {
    document.getElementById("myModal").style.display = "block";
}
function closeModal() {
    document.getElementById("myModal").style.display = "none";
}

rua_modal.value = '';
cidade_modal.value = '';
estado_modal.value = '';

btnBuscarCepModal.addEventListener('click', function (e) {
    e.preventDefault(); // não recarregar a página

    let urlBase = 'https://viacep.com.br/ws/';
    let parametros = estado_modal.value + '/' + cidade_modal.value + '/' + rua_modal.value + '/json/';
    let callback = '?callback=popularNaoSeiMeuCep';

    let script = document.createElement('script');
    script.src = urlBase + parametros + callback;
    document.body.appendChild(script);
});

function popularNaoSeiMeuCep(resposta) {
    console.log(resposta)
    if (!Array.isArray(resposta)) {
        alert('O retorno não é uma lista de CEPs');
        return;
    }

    listaCep.innerHTML = ''; // Limpar a lista antes de adicionar novos itens

    resposta.forEach(function (i) {
        let li = document.createElement('li');
        let endereco = i.logradouro + ' | ' + i.localidade + ' | ' + i.uf + ' | ' + i.cep.bold();
        li.innerHTML = endereco;
        li.setAttribute('onclick', 'exibirCep("' + i.cep.replace('-', '') + '")');
        listaCep.appendChild(li);
    });
}

function exibirCep(cep_modal) {
    mascaraCEP(cep_modal)
    // cep.value = cep_modal;
    listaCep.innerHTML = '';
    closeModal()
}

function openModal() {
    document.getElementById("myModal").style.display = "block";
}

function closeModal() {
    document.getElementById("myModal").style.display = "none";
}

// Fechar o modal se clicar fora dele
window.onclick = function(event) {
    const modal = document.getElementById('myModal');
    if (event.target === modal) {
      modal.style.display = 'none';
    }
  }

function btnBuscarCep() {
    let urlBase = 'https://viacep.com.br/ws/';
    let parametro = searchInput.value.trim() + '/json/';
    let script = document.createElement('script');
    script.src = urlBase + parametro + '?callback=popularNaoSeiMeuCep';
    document.body.appendChild(script);
}

// Validar Senha
function salvar() {
    var senha = document.getElementById("senha").value;
    var confirmarSenha = document.getElementById("confirmarSenha").value;

    if (senha === confirmarSenha) {
        alert("Senhas conferem!");
    } else {
        alert("As senhas não conferem. Por favor, tente novamente.");
    }
}

// Máscara CPF
function mascaraCPF(cpf) {
    cpf = cpf.replace(/\D/g, "");
    cpf = cpf.replace(/(\d{3})(\d)/, "$1.$2");
    cpf = cpf.replace(/(\d{3})(\d)/, "$1.$2");
    cpf = cpf.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

    document.getElementById("cpf").value = cpf;
}

document.getElementById("cpf").addEventListener("input", function () {
    mascaraCPF(this.value);
});

// Máscara Telefone
function mascaraTelefone(telefone) {
    telefone = telefone.replace(/\D/g, "");
    telefone = telefone.slice(0, 11);

    if (telefone.length <= 10) {
        telefone = telefone.replace(/(\d{2})(\d)/, "($1) $2");
        telefone = telefone.replace(/(\d{4})(\d)/, "$1-$2");
    } else {
        telefone = telefone.replace(/(\d{2})(\d)/, "($1) $2");
        telefone = telefone.replace(/(\d{5})(\d)/, "$1-$2");
    }

    document.getElementById("telefone").value = telefone;
}

document.getElementById("telefone").addEventListener("input", function () {
    mascaraTelefone(this.value);
});


// Máscara CEP
function mascaraCEP(cep) {
    cep = cep.replace(/\D/g, "");
    cep = cep.slice(0, 8);
    cep = cep.replace(/(\d{5})(\d)/, "$1-$2");
    document.getElementById("cep").value = cep;
}

document.getElementById("cep").addEventListener("input", function () {
    mascaraCEP(this.value);
});