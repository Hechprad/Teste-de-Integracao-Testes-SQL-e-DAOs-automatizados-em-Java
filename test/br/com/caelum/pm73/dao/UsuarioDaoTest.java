package br.com.caelum.pm73.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	@Test
	public void deveEncontrarPeloNomeEEmailMockado() {
		Session session = Mockito.mock(Session.class);
		Query query = Mockito.mock(Query.class);
		UsuarioDao usuarioDao = new UsuarioDao(session);
		
		Usuario usuario = new Usuario("João da Silva", "joao@dasilva.com.br");
		
		Mockito.when(session.createQuery(sql))
	}
}
