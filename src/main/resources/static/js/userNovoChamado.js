function limparDados() {
    let inputs = document.querySelectorAll('.novoChamado input');
    inputs.forEach(function (input) {
        if (input.type !== 'button' && !input.readOnly) {
            input.value = '';
        }
    });
}
