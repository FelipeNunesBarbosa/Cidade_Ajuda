package com.cidade_ajuda.chamados.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cidade_ajuda.chamados.entity.ChamadoEntity;
import com.cidade_ajuda.chamados.entity.UserEntity;
import com.cidade_ajuda.chamados.entity.ChatEntity;
import com.cidade_ajuda.chamados.repository.ChamadoRepository;
import com.cidade_ajuda.chamados.repository.ChatRepository;
import com.cidade_ajuda.chamados.repository.UserRepository;

@Controller
@RequestMapping("/atendente")
public class AtendenteController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/index")
    public String index(@RequestParam(value = "filtro", required = false, defaultValue = "todos") String filtro,
            Model model) {
        try {
            List<ChamadoEntity> chamados;
            System.out.println("Filtrar por:");
            System.out.println(filtro);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            model.addAttribute("id_pessoa", user.getId_pessoa());

            switch (filtro) {
                case "meusChamados":
                    chamados = chamadoRepository.findByUsuarioId(user.getId_pessoa());
                    break;
                case "Aberto":
                    chamados = chamadoRepository.findByStatus(ChamadoEntity.Status.Aberto);
                    break;
                case "Encerrado":
                    chamados = chamadoRepository.findByStatus(ChamadoEntity.Status.Encerrado);
                    break;
                case "Andamento":
                    chamados = chamadoRepository.findByStatus(ChamadoEntity.Status.Andamento);
                    break;
                case "Concluido":
                    chamados = chamadoRepository.findByStatus(ChamadoEntity.Status.Concluido);
                    break;
                default:
                    chamados = chamadoRepository.findAll();
                    break;
            }
            model.addAttribute("chamados", chamados);
            model.addAttribute("filtro", filtro);
            return "atendente/index";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "atendente/index";
        }
    }

    @GetMapping("/chamado/{id_chamado}")
    public String chamado(@PathVariable Long id_chamado, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userRepository.findByEmail(authentication.getName());
        ChamadoEntity chamado = chamadoRepository.findById(id_chamado).get();
        List<ChatEntity> chats = chatRepository.findAllByChamadoId(id_chamado);
        List<UserEntity> atendentes = userRepository.findByPerfilPessoa(UserEntity.Perfil.ATENDENTE);

        model.addAttribute("id_pessoa", user.getId_pessoa());
        model.addAttribute("numero", chamado.getId_chamado());
        model.addAttribute("id_user", chamado.getUsuario().getId_pessoa());
        model.addAttribute("usuario", chamado.getUsuario().getNome() + ' ' + chamado.getUsuario().getSobrenome());
        model.addAttribute("telefone", chamado.getUsuario().getTelefone());
        model.addAttribute("email", chamado.getUsuario().getEmail());
        model.addAttribute("statusChamado", chamado.getStatusChamado());
        model.addAttribute("id_atendente", chamado.getAtendente().getId_pessoa());
        model.addAttribute("atendente", chamado.getAtendente().getNome() + ' ' + chamado.getAtendente().getSobrenome());
        model.addAttribute("dataChamado", chamado.getData_chamado());
        model.addAttribute("dataAtualização", chamado.getData_atualizacao());
        model.addAttribute("titulo", chamado.getTitulo());
        model.addAttribute("descricao", chamado.getDescricao());
        model.addAttribute("chats", chats);
        model.addAttribute("atendentes", atendentes);

        return "atendente/chamado";
    }

    @PostMapping("/alterar/chamado")
    public String alterarChamado(@RequestParam String acao, @RequestParam Long id_chamado,
            @RequestParam Long id_atendente, @RequestParam String dataAtualização,
            RedirectAttributes redirectAttributes, Model model) {

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());
            ChamadoEntity chamadoEntity = chamadoRepository.findById(id_chamado).get();
            UserEntity atendente = userRepository.findById(id_atendente).get();
            ChatEntity chatEntity = new ChatEntity();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String resposta;

            if (acao.equals("salvar") && id_atendente != 1) {

                chamadoEntity.setStatusChamado(ChamadoEntity.Status.Andamento);
                chatEntity
                        .setConversa("Chamado atribuído para " + atendente.getNome() + " " + atendente.getSobrenome());
                resposta = "Alteração salva com sucesso!";
            } else if (acao.equals("salvar") && id_atendente == 1) {

                chamadoEntity.setStatusChamado(ChamadoEntity.Status.Aberto);
                chatEntity
                        .setConversa("Chamado atribuído para " + atendente.getNome() + " " + atendente.getSobrenome());
                resposta = "Alteração salva com sucesso!";
            } else if (acao.equals("encerrar")) {

                chamadoEntity.setStatusChamado(ChamadoEntity.Status.Encerrado);
                chatEntity.setConversa("Chamado encerrado por " + user.getNome() + " " + user.getSobrenome());
                resposta = "Chamado encerrado com sucesso!";
            } else {
                resposta = "";
            }

            chamadoEntity.setAtendente(atendente);
            chamadoEntity.setData_atualizacao(dataAtualização);

            chatEntity.setId_chamado(chamadoEntity);
            chatEntity.setId_user(user);
            chatEntity.setData_chat(LocalDate.now().toString());
            chatEntity.setHora_chat(LocalTime.now().format(timeFormatter).toString());

            chamadoRepository.save(chamadoEntity);
            chatRepository.save(chatEntity);

            redirectAttributes.addFlashAttribute("message", resposta);

        } catch (Exception e) {
            System.out.println("Erro ao alterar chamado: " + e.getMessage());
            return "redirect:/atendente/chamado/" + id_chamado;
        }
        return "redirect:/atendente/chamado/" + id_chamado;
    }

    @PostMapping("/nova/conversa")
    public String chat(@RequestParam String novaConversa,
            @RequestParam Long id_chamado,
            @RequestParam Long id_user,
            @RequestParam String acao,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (acao.equals("enviar")) {
            try {

                if (novaConversa == null || novaConversa.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "A conversa não pode ser vazia.");
                    return "redirect:/atendente/chamado/" + id_chamado;
                }
                System.out.println(novaConversa);

                ChatEntity chatEntity = new ChatEntity();

                chatEntity.setConversa(novaConversa);

                ChamadoEntity chamado = chamadoRepository.findById(id_chamado).get();
                chatEntity.setId_chamado(chamado);

                UserEntity user = userRepository.findById(id_user).get();
                chatEntity.setId_user(user);

                chatEntity.setData_chat(LocalDate.now().toString());

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                chatEntity.setHora_chat(LocalTime.now().format(timeFormatter).toString());

                chatRepository.save(chatEntity);
                redirectAttributes.addFlashAttribute("message", "Conversa salva com sucesso!");

                return "redirect:/atendente/chamado/" + id_chamado;
            } catch (Exception e) {
                System.out.println("Erro ao alterar chamado: " + e.getMessage());
                return "redirect:/atendente/chamado/" + id_chamado;
            }
        }
        return acao;
    }

    @GetMapping("/novoChamado")
    public String novoChamado(Model model) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            model.addAttribute("usuario", user.getNome() + " " + user.getSobrenome());
            model.addAttribute("telefone", user.getTelefone());
            model.addAttribute("email", user.getEmail());

            return "atendente/novoChamado";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "atendente/novoChamado";
        }
    }

    @PostMapping("/novo/chamado")
    public String chat(@RequestParam String titulo, @RequestParam String descricao,
            RedirectAttributes redirectAttributes, Model model) {

        String message;
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            ChamadoEntity chamadoEntity = new ChamadoEntity();

            chamadoEntity.setTitulo(titulo);
            chamadoEntity.setDescricao(descricao);
            chamadoEntity.setAtendente(userRepository.findByEmail("cidade.ajuda@gmail.com"));
            chamadoEntity.setData_chamado(LocalDate.now().toString());
            chamadoEntity.setData_atualizacao(LocalDate.now().toString());
            chamadoEntity.setStatusChamado(ChamadoEntity.Status.Aberto);
            chamadoEntity.setUsuario(user);

            chamadoRepository.save(chamadoEntity);

            message = "Chamado criado com sucesso!";

            return "redirect:/atendente/index?success=" + message;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = "Erro ao criar chamado!";
            return "redirect:/atendente/novoChamado?erro=" + message;
        }
    }

    @GetMapping("/pessoas")
    public String pessoas(Model model) {
        try {
            List<UserEntity> pessoas = userRepository.findAll();
            model.addAttribute("pessoas", pessoas);
            return "atendente/pessoas";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "atendente/pessoas";
        }
    }

    @PostMapping("/alterar/perfil")
    public String alterarPerfil(@RequestParam Long id_user, @RequestParam String opcoes, @RequestParam String acao,
            RedirectAttributes redirectAttributes, Model model) {
        String message;
        try {

            UserEntity user = userRepository.findById(id_user).get();

            if (acao.equals("salvar")) {
                if (opcoes.equals("opcao1")) {
                    user.setPerfilPessoa(UserEntity.Perfil.ATENDENTE);
                } else {
                    user.setPerfilPessoa(UserEntity.Perfil.USUARIO);
                }
                message = "Perfil do  " + user.getNome().toString() + " alterado para com sucesso!";
                userRepository.save(user);
            } else if (acao.equals("deletar")) {
                userRepository.delete(user);
                message = user.getNome().toString() + " deletado com sucesso!";
            } else {
                message = "";
            }

            return "redirect:/atendente/pessoas?success=" + message;
        } catch (Exception e) {
            message = "Erro ao alterar perfil!";
            System.out.println(e.getMessage());
            return "redirect:/atendente/pessoas?erro=" + message;
        }
    }

    @GetMapping("/minhaConta")
    public String minhaConta(Model model) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            model.addAttribute("nome", user.getNome());
            model.addAttribute("sobrenome", user.getSobrenome());
            model.addAttribute("cpf", user.getCpf());
            model.addAttribute("telefone", user.getTelefone());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("nascimento", user.getNascimento());
            model.addAttribute("cep", user.getCep());
            model.addAttribute("rua", user.getRua());
            model.addAttribute("numero", user.getNumero());
            model.addAttribute("bairro", user.getBairro());
            model.addAttribute("estado", user.getEstado());
            model.addAttribute("cidade", user.getCidade());

            return "atendente/minhaConta";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "atendente/minhaConta";
        }
    }

    @PostMapping("/alterar/senha")
    public String postMethodName(@RequestParam String senhaAntiga, @RequestParam String novaSenha,
            @RequestParam String confirmarSenha, RedirectAttributes redirectAttributes, Model model) {

        String message;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            if (!passwordEncoder.matches(senhaAntiga, user.getSenha()) || !novaSenha.equals(confirmarSenha)) {
                message = "Senhas não correspondem!";
                throw new IllegalArgumentException(message);
            } else {
                user.setSenha(passwordEncoder.encode(novaSenha));
                userRepository.save(user);
                message = "Senha alterada!";
                return "redirect:/atendente/minhaConta?success=" + message;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = "Erro ao alterar senha!";
            return "redirect:/atendente/minhaConta?erro=" + message;
        }
    }

    @GetMapping("/alterarConta")
    public String alterarConta(Model model) {
        try {

            System.out.println("Aqui");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            UserEntity user = userRepository.findByEmail(currentUserName);

            model.addAttribute("id_pessoa", user.getId_pessoa());
            model.addAttribute("nome", user.getNome());
            model.addAttribute("sobrenome", user.getSobrenome());
            model.addAttribute("cpf", user.getCpf());
            model.addAttribute("telefone", user.getTelefone());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("nascimento", user.getNascimento());
            model.addAttribute("cep", user.getCep());
            model.addAttribute("rua", user.getRua());
            model.addAttribute("numero", user.getNumero());
            model.addAttribute("bairro", user.getBairro());
            model.addAttribute("estado", user.getEstado());
            model.addAttribute("cidade", user.getCidade());

            return "atendente/alterarConta";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "atendente/alterarConta";
        }
    }

    @PostMapping("/alterar/conta")
    public String alterarConta(@RequestParam Long id, @RequestParam(required = false) String nome,
            @RequestParam(required = false) String sobrenome, @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String telefone, @RequestParam(required = false) String email,
            @RequestParam(required = false) String nascimento, @RequestParam(required = false) String cep,
            @RequestParam(required = false) String rua, @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) String bairro, @RequestParam(required = false) String estado,
            @RequestParam(required = false) String cidade, Model model) {

        String message;
        try {
            System.out.println(nome);
            UserEntity userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (nome != null)
                userEntity.setNome(nome);
            if (sobrenome != null)
                userEntity.setSobrenome(sobrenome);
            if (cpf != null)
                userEntity.setCpf(cpf);
            if (telefone != null)
                userEntity.setTelefone(telefone);
            if (email != null)
                userEntity.setEmail(email);
            if (nascimento != null)
                userEntity.setNascimento(nascimento);
            if (cep != null)
                userEntity.setCep(cep);
            if (rua != null)
                userEntity.setRua(rua);
            if (numero != null)
                userEntity.setNumero(numero);
            if (bairro != null)
                userEntity.setBairro(bairro);
            if (estado != null)
                userEntity.setEstado(estado);
            if (cidade != null)
                userEntity.setCidade(cidade);

            userRepository.save(userEntity);

            message = "Perfil alterado com sucesso!";
            return "redirect:/atendente/minhaConta?success=" + message;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = "Erro ao alterar perfil";
            return "redirect:/atendente/alterarConta?erro=" + message;
        }
    }
}
