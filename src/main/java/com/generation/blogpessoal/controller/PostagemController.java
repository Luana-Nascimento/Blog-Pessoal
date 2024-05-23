package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController //anotação que diz para spring que essa é uma controladora de rotas e acesso aos metodos
@RequestMapping("/postagens") //rota para chegar nessa classe (insomnia)
@CrossOrigin(origins = "*", allowedHeaders = "*") // liberar o acesso a outras máquinas
public class PostagemController {

	@Autowired //injeção de dependências - mesma coisa de instanciar a classe PostagemRepository
	private PostagemRepository postagemRepository; 
	
	@Autowired //Injeção de Dependência do Recurso Tema
	private TemaRepository temaRepository; 
	
	@GetMapping //define o verbo http que atende esse método
	public ResponseEntity<List<Postagem>> getAll (){
		//ResponseEntity: classe que permite que a resposta seja http, formata o dado
		return ResponseEntity.ok(postagemRepository.findAll());
		//SELECT * FROM tb_postagens
	
	}
	//O id está entre chaves, pois não será inserido no local host diretamente o id e sim o que foi atribuido a ele. Ex: localhost:8080/postagens/2  
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id){
		// findById = SELECT * FROM tb_postagens WHERE id = 1;
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	//INSERT INTO tb_postagens (titulo, texto, data) VALUES ("Título", "Texto", "2024-12-31 14:05:01");
		@PostMapping
		public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){
			//retorno em formato ResponseEntity
			if (temaRepository.existsById(postagem.getTema().getId()))
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(postagemRepository.save(postagem));
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
	}
		//atualizando dado do banco de dados através do Id
		@PutMapping
		 //o nome do método como put foi definido, mas poderia ser qualquer outra palavra.
		public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem){
			if (postagemRepository.existsById(postagem.getId())) {
				if(temaRepository.existsById(postagem.getTema().getId()))
					return ResponseEntity.status(HttpStatus.OK)
						.body(postagemRepository.save(postagem));
				
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);			
			}
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
		

		//DELETE FROM tb_postagens WHERE id = id;
		@DeleteMapping("/{id}")
		public void delete(@PathVariable Long id) {
			Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		postagemRepository.deleteById(id);
	}
}
		
		// Get - recuperar as informações do banco de dados
		// Post - insert no banco de dados
		// Put - update no banco de dados
		// Delete - deleta algum registro do banco de dados




