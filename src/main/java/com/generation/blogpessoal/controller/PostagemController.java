package com.generation.blogpessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

@RestController //anotação que diz para spring que essa é uma controladora de rotas e acesso aos metodos
@RequestMapping("/postagens") //rota para chegar nessa classe (insomnia)
@CrossOrigin(origins = "*", allowedHeaders = "*") // liberar o acesso a outras máquinas
public class PostagemController {

	@Autowired //injeção de dependências - mesma coisa de instanciar a classe PostagemRepository
	private PostagemRepository postagemRepository; 
	
	@GetMapping //define o verbo http que atende esse método
	public ResponseEntity<List<Postagem>> getAll (){
		//ResponseEntity: classe que permite que a resposta seja http, formata o dado
		return ResponseEntity.ok(postagemRepository.findAll());
		//SELECT * FROM tb_postagens
	
	}


}
