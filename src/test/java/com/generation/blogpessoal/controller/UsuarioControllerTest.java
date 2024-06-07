package com.generation.blogpessoal.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // o spring avisa que essa classe é teste, e o random roda em uma porta aleatória, podendo ser 8080 ou outra. 
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Roda a classe inteira

// as duas anotações acima são padronizadas para qualquer teste unit. 

public class UsuarioControllerTest {
	
	//mesmas injeções de dependências da UsuarioController (a classe original)
	@Autowired
	private UsuarioRepository usuarioRepository; //poder ter acesso ao banco de dados
	
	@Autowired
	private UsuarioService usuarioService; // acesso às funções do service

	//injeção padrão de testes - faz o serviço do insomnia
	@Autowired
	private TestRestTemplate testRestTemplate; 
		
	@BeforeAll // anotação com diogo: garante que esse bloco vai rodar antes de tudo, de todos os testes
	void start() { 
		usuarioRepository.deleteAll(); //para startar ele primeiro deleta todos os usuários para garantir que há apenas 1 e dps incrementa o id.
		usuarioService.cadastrarUsuario(new Usuario(0L, "root", "root@root.com", "rootroot", "")); // Após garantir que não tem nenhum usuário no banco e depois cria o usuário root (ex). 
	}
	@Test
	@DisplayName("Deve cadastrar um novo usuário.") // como quero que o teste apareça no relatório
	public void deveCriarUmNovoUsuario() {
		//http -> nada mais é que um objeto sendo passado pelo json
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario (0L, "Thiago", "thiago@email.com", "12345678", ""));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange(
				"/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class
				);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário.")
	public void naoDeveDuplicarUsuario() {
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "123456789", "-"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario (0L, "Maria da Silva", "maria_silva@email.com.br", "123456789", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class); //exchange é como clicar no send do insomnia, envia os dados para post
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode()); //avalia se o status retorno/resposta é igual o retorno esperado
	}

	
	@Test
	@DisplayName("Atualizar um usuário.")
	public void DeveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario (
				0L, "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
				"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate); 
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	
}
	@Test
	@DisplayName("Listar todos os usuários.")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	  }
	}
