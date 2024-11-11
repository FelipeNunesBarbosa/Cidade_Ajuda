package com.cidade_ajuda.chamados.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cidade_ajuda.chamados.entity.UserEntity;
import com.cidade_ajuda.chamados.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String login() {
        return "login";  // Retorna o template 'login.html'
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";  // Retorna o template 'cadastro.html'
    }

    @PostMapping("/cadastro")
    public String cadastro(
            @RequestParam String nome,
            @RequestParam String sobrenome,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String nascimento,
            @RequestParam String cep,
            @RequestParam String rua,
            @RequestParam Integer numero,
            @RequestParam String bairro,
            @RequestParam String estado,
            @RequestParam String cidade,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            Model model) {
        try {
            if (!senha.equals(confirmarSenha)) {
                model.addAttribute("error", "Senhas não correspondem!");
                throw new IllegalArgumentException("Senhas não correspondem!");
            }

            if (userRepository.findByEmail(email) != null) {
                model.addAttribute("error", "Email ja cadastrado!");
                throw new IllegalArgumentException("Email ja cadastrado!");
            }

            UserEntity userEntity = new UserEntity();

            userEntity.setNome(nome);
            userEntity.setSobrenome(sobrenome);
            userEntity.setCpf(cpf);
            userEntity.setTelefone(telefone);
            userEntity.setEmail(email);
            userEntity.setNascimento(nascimento);
            userEntity.setCep(cep);
            userEntity.setRua(rua);
            userEntity.setNumero(numero);
            userEntity.setBairro(bairro);
            userEntity.setEstado(estado);
            userEntity.setCidade(cidade);
            userEntity.setSenha(passwordEncoder.encode(senha));
            userEntity.setPerfilPessoa(UserEntity.Perfil.USUARIO);

            userRepository.save(userEntity);

            model.addAttribute("message", "Usuário: " + nome + " cadastrado com sucesso!");
            return "cadastro";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao cadastrar pessoa: " + e.getMessage());
            return "cadastro";
        }
    }
}
