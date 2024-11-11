//Botão header mobile
const btnMobile = document.getElementById('btn-mobile')

function toggleMenu(event) {
    if (event.type === 'touchstart') event.preventDefault()
    const nav = document.getElementById('nav')
    nav.classList.toggle('active')
    const active = nav.classList.contains('active')
    event.currentTarget.setAtribute('aria-expanded', active)
    if (active) {
        event.currentTarget.setAtribute('aria-label', 'Fechar Menu')
    } else {
        event.currentTarget.setAtribute('aria-label', 'Abrir Menu')
    }
}

btnMobile.addEventListener('click', toggleMenu)
btnMobile.addEventListener('touchstart', toggleMenu)

//Navegação
function navigation(navigation) {
    window.location = navigation
}

document.querySelector('.navLi').addEventListener('click', function(event) {
    event.preventDefault();
    document.getElementById('logoutForm').submit();
});

// Ordenar tabela pelo cabeçalho
function sortTable(n) {
    var rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
    switching = true;
    dir = "asc";
    while (switching) {
        switching = false;
        rows = document.getElementById("lista").rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            x = rows[i].getElementsByTagName("td")[n];
            y = rows[i + 1].getElementsByTagName("td")[n];
            if (dir == "asc") {
                if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            } else if (dir == "desc") {
                if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchcount++;
        } else {
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

// Buscar na tabela
function searchTable() {
    var tr, td, i, txtValue;
    tr = document.getElementById("lista").getElementsByTagName("tr");
    for (i = 0; i < tr.length; i++) {
        td = tr[i].getElementsByTagName("td");
        for (var j = 0; j < td.length; j++) {
            var cell = td[j];
            if (cell) {
                txtValue = cell.textContent || cell.innerText;
                if (txtValue.toUpperCase().indexOf(document.getElementById("searchInput").value.toUpperCase()) > -1) {
                    tr[i].style.display = "";
                    break;
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
}

// Pega mensagem do link
document.addEventListener("DOMContentLoaded", function() {
    // Função para obter o valor de um parâmetro da URL
    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    // Obtém as mensagens dos parâmetros da URL
    var successMessage = getParameterByName("success");
    var errorMessage = getParameterByName("erro");

    // Exibe a mensagem apropriada em um alert
    if (successMessage) {
        alert(successMessage);
    } else if (errorMessage) {
        alert(errorMessage);
    }

    // Remove os parâmetros da URL para evitar a repetição do alerta
    if (successMessage || errorMessage) {
        history.replaceState(null, "", location.pathname);
    }
});