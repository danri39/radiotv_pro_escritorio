package br.com.drs.radiotv_pro_escritorio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    // O Spring Boot injeta automaticamente baseado no seu application.properties
    private final JavaMailSender mailSender;

    public void enviarEmailCadastro(String destinatario, String chaveUsuario, String chavePrimeiroAcesso) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setFrom("drssistemascontrole@gmail.com");
        mensagem.setTo(destinatario);
        mensagem.setSubject("Bem-vindo ao Sistema - Chaves de Acesso");

        String corpo = String.format(
                "Olá!\n\n" +
                        "Seu cadastro foi realizado com sucesso.\n\n" +
                        "Sua Chave de Usuário é: %s\n" +
                        "Sua Chave de Primeiro Acesso é: %s\n\n" +
                        "Use estes dados para criar sua senha no primeiro acesso.",
                chaveUsuario, chavePrimeiroAcesso
        );
        mensagem.setText(corpo);

        mailSender.send(mensagem);
    }

    public void enviarEmailTrocaSenha(String destinatario, String chaveTrocaSenha) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setFrom("drssistemascontrole@gmail.com");
        mensagem.setTo(destinatario);
        mensagem.setSubject("Recuperação de Senha");

        String corpo = String.format(
                "Olá!\n\n" +
                        "Foi solicitada uma troca de senha para sua conta.\n" +
                        "Use a seguinte chave para cadastrar a nova senha: %s\n\n" +
                        "Se você não solicitou isso, desconsidere este e-mail.",
                chaveTrocaSenha
        );
        mensagem.setText(corpo);

        mailSender.send(mensagem);
    }

    public void enviarEmailErro(String para, String nomeVendedor, String nomeArquivo, String detalheErro) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sistema-radio@seuprovedor.com");
        message.setTo(para);
        message.setSubject("Erro na Importação do Mapa de Irradiação: " + nomeArquivo);

        String corpo = String.format(
                "Olá %s,\n\n" +
                        "Identificamos um erro ao processar o mapa de irradiação automatizado.\n" +
                        "Arquivo: %s\n\n" +
                        "Detalhes do erro:\n%s\n\n" +
                        "Por favor, corrija as informações no arquivo e envie-o novamente para a pasta física correspondente.\n\n" +
                        "Atenciosamente,\nSistema de Irradiação Rádio/TV",
                nomeVendedor, nomeArquivo, detalheErro
        );

        message.setText(corpo);
        mailSender.send(message);
    }
}