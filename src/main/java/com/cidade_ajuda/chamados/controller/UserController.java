package com.cidade_ajuda.chamados.controller;

import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cidade_ajuda.chamados.entity.ChatEntity;
import com.cidade_ajuda.chamados.entity.UserEntity;
import com.cidade_ajuda.chamados.repository.ChamadoRepository;
import com.cidade_ajuda.chamados.repository.ChatRepository;
import com.cidade_ajuda.chamados.repository.UserRepository;

@Controller
@RequestMapping("/usuario")
public class UserController {

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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            System.out.println(filtro);

            chamados = chamadoRepository.findByUsuarioId(user.getId_pessoa());

            long totalChamados = chamados.size();

            long totalAberto = chamados.stream()
                    .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Aberto).count();
            long totalEncerrado = chamados.stream()
                    .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Encerrado).count();
            long totalAndamento = chamados.stream()
                    .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Andamento).count();

            model.addAttribute("totalChamados", totalChamados);
            model.addAttribute("totalAberto", totalAberto);
            model.addAttribute("totalEncerrado", totalEncerrado);
            model.addAttribute("totalAndamento", totalAndamento);


            switch (filtro) {
                case "Aberto":
                    chamados = chamados.stream()
                            .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Aberto)
                            .collect(Collectors.toList());
                    break;
                case "Encerrado":
                    chamados = chamados.stream()
                            .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Encerrado)
                            .collect(Collectors.toList());
                    break;
                case "Andamento":
                    chamados = chamados.stream()
                            .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Andamento)
                            .collect(Collectors.toList());
                    break;
                case "Concluido":
                    chamados = chamados.stream()
                            .filter(chamado -> chamado.getStatusChamado() == ChamadoEntity.Status.Concluido)
                            .collect(Collectors.toList());
                    break;
                default:
                    chamados = chamadoRepository.findAll();
                    break;
            }

            model.addAttribute("id_pessoa", user.getId_pessoa());
            model.addAttribute("chamados", chamados);

            return "usuario/index";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "usuario/index";
        }
    }

    @GetMapping("/chamado/{id_chamado}")
    public String chamado(@PathVariable Long id_chamado, Model model) {
        ChamadoEntity chamado = chamadoRepository.findById(id_chamado).get();
        List<ChatEntity> chats = chatRepository.findAllByChamadoId(id_chamado);

        model.addAttribute("numero", chamado.getId_chamado());
        model.addAttribute("id_user", chamado.getUsuario().getId_pessoa());

        model.addAttribute("usuario", chamado.getUsuario().getNome() + ' ' + chamado.getUsuario().getSobrenome());
        model.addAttribute("telefone", chamado.getUsuario().getTelefone());
        model.addAttribute("email", chamado.getUsuario().getEmail());
        model.addAttribute("statusChamado", chamado.getStatusChamado());
        String atendenteNome = "";
        if (chamado.getAtendente() != null) {
            atendenteNome = chamado.getAtendente().getNome() + ' ' + chamado.getAtendente().getSobrenome();
        }
        model.addAttribute("atendente", atendenteNome);
        model.addAttribute("dataChamado", chamado.getData_chamado());
        model.addAttribute("dataAtualização", chamado.getData_atualizacao());
        model.addAttribute("titulo", chamado.getTitulo());
        model.addAttribute("descricao", chamado.getDescricao());
        model.addAttribute("chats", chats);

        return "usuario/chamado";
    }

    @PostMapping("/nova/conversa")
    public String chat(@RequestParam String novaConversa,
            @RequestParam Long id_chamado,
            @RequestParam Long id_user,
            @RequestParam String acao,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        String message;

        if (acao.equals("enviar")) {
            try {

                if (novaConversa == null || novaConversa.trim().isEmpty()) {
                    message = "Erro, campo nova conversa em branco!";
                    return "redirect:/usuario/chamado/" + id_chamado + "?erro=" + message;
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
                message = "Conversa enviada com sucesso!";
                
                return "redirect:/usuario/chamado/" + id_chamado + "?success=" + message;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                message = "Erro ao enviar conversa!";
                return "redirect:/usuario/chamado/" + id_chamado + "?erro=" + message;
            }
        } else {
            try {

                ChatEntity chatEntity = new ChatEntity();

                ChamadoEntity chamado = chamadoRepository.findById(id_chamado).get();
                UserEntity user = userRepository.findById(id_user).get();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                chamado.setStatusChamado(ChamadoEntity.Status.Encerrado);
                chamadoRepository.save(chamado);
                chatEntity.setConversa("Chamado encerrado por " + user.getNome());
                chatEntity.setId_chamado(chamado);
                chatEntity.setId_user(user);
                chatEntity.setData_chat(LocalDate.now().toString());
                chatEntity.setHora_chat(LocalTime.now().format(timeFormatter).toString());

                chatRepository.save(chatEntity);

                message = "Chamado encerrado com sucesso!";

                return "redirect:/usuario/chamado/" + id_chamado + "?success=" + message;
            } catch (Exception e) {
                System.out.println("Erro ao encerrar chamado: " + e.getMessage());
                message = "Erro ao encerrar chamado!";
                return "redirect:/usuario/chamado/" + id_chamado + "?erro=" + message;
            }
        }
    }

    @GetMapping("/novoChamado")
    public String novoChamado(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        UserEntity user = userRepository.findByEmail(currentUserName);

        model.addAttribute("usuario", user.getNome());
        model.addAttribute("telefone", user.getTelefone());
        model.addAttribute("email", user.getEmail());
        return "usuario/novoChamado";
    }

    @PostMapping("/novo/chamado")
    public String chat(@RequestParam String titulo,
            @RequestParam String descricao,
            RedirectAttributes redirectAttributes,
            Model model) {

        String message;

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity user = userRepository.findByEmail(authentication.getName());

            ChamadoEntity chamadoEntity = new ChamadoEntity();

            chamadoEntity.setTitulo(titulo);
            chamadoEntity.setDescricao(descricao);
            chamadoEntity.setData_chamado(LocalDate.now().toString());
            chamadoEntity.setAtendente(null);
            chamadoEntity.setData_atualizacao(LocalDate.now().toString());
            chamadoEntity.setStatusChamado(ChamadoEntity.Status.Aberto);
            chamadoEntity.setUsuario(user);

            System.out.println(chamadoEntity);

            chamadoRepository.save(chamadoEntity);

            message = "Chamado criado com sucesso!";
            return "redirect:/usuario/index?success=" + message;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = "Erro ao criar chamado!";
            return "redirect:/usuario/novoChamado?erro=" + message;
        }
    }

    @GetMapping("/avaliarChamado")
    public String avaliarChamado(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            UserEntity user = userRepository.findByEmail(currentUserName);

            List<ChamadoEntity> chamados = chamadoRepository.findByUsuarioId(user.getId_pessoa());
            model.addAttribute("chamados", chamados);
            return "usuario/avaliarChamado";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "usuario/avaliarChamado";
        }
    }

    @GetMapping("/minhaConta")
    public String minhaConta(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            UserEntity user = userRepository.findByEmail(currentUserName);

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

            return "usuario/minhaConta";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "usuario/minhaConta";
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

            return "usuario/alterarConta";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "usuario/alterarConta";
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
                return "redirect:/usuario/minhaConta?success=" + message;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = "Erro ao alterar senha!";
            return "redirect:/usuario/minhaConta?erro=" + message;
        }
    }

    @PostMapping("/alterar/conta")
    public String alterarConta(
            @RequestParam Long id,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String sobrenome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nascimento,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) String rua,
            @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String cidade,
            Model model) {
        String message;
        try {
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
            return "redirect:/usuario/minhaConta?success=" + message;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            message = "Erro ao alterar perfil";
            return "redirect:/usuario/alterarConta?erro=" + message;
        }
    }
}
