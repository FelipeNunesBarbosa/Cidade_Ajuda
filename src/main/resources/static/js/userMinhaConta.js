// Função para abrir o modal
function openModal() {
    document.getElementById('modalAlterarSenha').style.display = 'block';
  }
  
  // Função para fechar o modal
  function closeModal() {
    document.getElementById('modalAlterarSenha').style.display = 'none';
  }
  
  // Fechar o modal se clicar fora dele
  window.onclick = function(event) {
    const modal = document.getElementById('modalAlterarSenha');
    if (event.target === modal) {
      modal.style.display = 'none';
    }
  }