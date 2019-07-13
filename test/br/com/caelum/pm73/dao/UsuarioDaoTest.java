package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {

	private Session session;
	private UsuarioDao usuarioDao;

	@Before
	public void setUp() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
	}

	@After
	public void closeSession() {
		// fechando conexão do BD
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEEmail() {
		// criando um usuario e salvando antes de
		// chamar o método porNomeEEmail
		Usuario novoUsuario = new Usuario("João da Silva", "joao@dasilva.com.br");
		usuarioDao.salvar(novoUsuario);

		// agora buscamos no banco
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");

		assertEquals("João da Silva", usuarioDoBanco.getNome());
		assertEquals("joao@dasilva.com.br", usuarioDoBanco.getEmail());
		// lembrar de executar a classe "CriaTabelas" antes de executar o teste
	}

	@Test
	public void deveRetornarNullSeUsuarioNaoExistir() {
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("Joaquim Peixoto", "joaquinzeira@email.com");

		assertNull(usuarioDoBanco);
	}
}
