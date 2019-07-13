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
		// boa pr�tica: contexto de transa��o (em testes com BD)
		session.beginTransaction();
	}

	@After
	public void closeSession() {
		// boa pr�tica: damos um rollback no BD para limpar os dados inseridos no teste
		session.getTransaction().rollback();
		// fechando conex�o do BD
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEEmail() {
		// criando um usuario e salvando antes de
		// chamar o m�todo porNomeEEmail
		Usuario novoUsuario = new Usuario("Jo�o da Silva", "joao@dasilva.com.br");
		usuarioDao.salvar(novoUsuario);

		// agora buscamos no banco
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("Jo�o da Silva", "joao@dasilva.com.br");

		assertEquals("Jo�o da Silva", usuarioDoBanco.getNome());
		assertEquals("joao@dasilva.com.br", usuarioDoBanco.getEmail());
		// lembrar de executar a classe "CriaTabelas" antes de executar o teste
	}

	@Test
	public void deveRetornarNullSeUsuarioNaoExistir() {
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("Joaquim Peixoto", "joaquinzeira@email.com");

		assertNull(usuarioDoBanco);
	}
}
