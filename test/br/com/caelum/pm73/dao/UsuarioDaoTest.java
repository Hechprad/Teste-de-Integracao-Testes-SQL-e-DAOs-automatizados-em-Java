package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

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
		
		String sql = "from Usuario u where u.nome = :nome and u.email = :email";
		
		Mockito.when(session.createQuery(sql)).thenReturn(query);
		Mockito.when(query.uniqueResult()).thenReturn(usuario);
		Mockito.when(query.setParameter("nome", "João da Silva")).thenReturn(query);
		Mockito.when(query.setParameter("email", "joao@dasilva.com.br")).thenReturn(query);
		
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");
		
		assertEquals(usuario.getNome(), usuarioDoBanco.getNome());
		assertEquals(usuario.getEmail(), usuarioDoBanco.getEmail());
	}
}
