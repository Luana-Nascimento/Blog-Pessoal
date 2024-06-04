package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

// objetivo: informar para o security os dados de acesso da API

public class UserDetailsImpl implements UserDetails{
	
	//versão da classe que estamos trabalhando
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;
	
	//classe segurança que trás autorizações de acesso que meu usuário tem
	private List<GrantedAuthority> authorities;

	public UserDetailsImpl(Usuario user) {
		this.userName = user.getUsuario();
		this.password = user.getSenha();
	}
	
	public UserDetailsImpl() {}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// autorizações de acesso do usuário
		return authorities;
	}

	@Override
	public String getPassword() {
		// retorna a senha do usuário
		return password;
	}

	@Override
	public String getUsername() {
		// retorna o usuário 
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		// se a conta não expirou ele dá acesso - true 
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// se a conta não está bloqueada - true 
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		//se a credencial não estiver expirada - true
		return true;
	}

	@Override
	public boolean isEnabled() {
		//se o usuário esta habilitado - true
		return true;
	}

}
