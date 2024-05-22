package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;

//JpaRepository - classe do JPA (dependência adicionada), dentro dela contém métodos que vão realizar Query no banco. Significa que a JPA assume o controle de criar automaticamente o SELECT * FROM tb_postagem. 
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
// entre o diamante se coloca a classe (postagem) que está puxando da model e o tipo (long). 
	// a JPA faz a ligação para buscar no banco de dados
	
	//SELECT * FROM tb_postagens WHERE titulo LIKE "%POST%"; = findAllByTituloContainingIgnoreCase
	public List <Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo);


}
