package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service // Spring estamos tratando aqui regras de negócio
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	// classe do security que tem gestão de autenticação
	// permite acessar metodos que podem entregar ao objeto as suas autoridades concedidas

	// primeira regra de negócio / vamos definir as regras para permitir o cadastro de um usuário
	public Optional<Usuario> cadastrarUsuario(Usuario usuario){
		//nome | usuario(email) | senha | foto | ingrid@gmail.com
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) // verifica se o usuário já existe 
			return Optional.empty(); //meu objeto está vazio, empty 
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}
	//método que vai tratar para a senha ser criptografada antes de ser persistida no banco
	private String criptografarSenha(String senha) {
		//classe que trata a criptografia
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
		return encoder.encode(senha); //metodo encoder sendo aplicado na senha
	}
	
	/*
	 * segundo problema
	 * objetivo: evitar que haja dois usuários com o mesmo email na hora do update
	 * nome | usuario (email) | senha | foto ti.jacque@gmail.com -> ingrid@gmail.com
	 */
	
	public Optional<Usuario> atualizarUsuario(Usuario usuario){
		//validando se o id passado existe no banco de dados
		if(usuarioRepository.findById(usuario.getId()).isPresent()){
			//Objeto optional pq pode existir ou não
			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
			//3 | jacqueline | ingrid@gmail.com | 123456789 | ""
			//perquisei no banco ingrid@gmail.com - 2 | ingrid | ingrid@gmail.com | 123456789 | ""
			if (buscaUsuario.isPresent() && (buscaUsuario.get().getId() ) != usuario.getId())
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuario));	
		}
		return Optional.empty();
	}
	
	//Garantir as regras de negócio para o login
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
        // Gera o Objeto de autenticação - que o usuário tenta logar
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(), 
				usuarioLogin.get().getSenha());
		
        // Autentica o Usuario - tiver esse usuario e senha

		Authentication authentication = authenticationManager.authenticate(credenciais);
        
        // Se a autenticação foi efetuada com sucesso
		if (authentication.isAuthenticated()) {

            // Busca os dados do usuário
			Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

            // Se o usuário foi encontrado
			if (usuario.isPresent()) {

                // Preenche o Objeto usuarioLogin com os dados encontrados - passando os dados do objeto retornado do banco de dados para o UsuarioLogin

			    usuarioLogin.get().setId(usuario.get().getId());
                usuarioLogin.get().setNome(usuario.get().getNome());
                usuarioLogin.get().setFoto(usuario.get().getFoto());
                usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
                usuarioLogin.get().setSenha("");
				
                 // Retorna o Objeto preenchido
			   return usuarioLogin;
			}
        } 
		return Optional.empty();
    }
	// metodo que vai lá na jwt e gera o token do usuário
	private String gerarToken(String usuario) {
		return "Bearer " +jwtService.generateToken(usuario);
	}
}