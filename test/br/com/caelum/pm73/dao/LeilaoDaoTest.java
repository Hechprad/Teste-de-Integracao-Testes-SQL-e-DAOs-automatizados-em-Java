package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoDaoTest {

	private Session session;
	private UsuarioDao usuarioDao;
	private LeilaoDao leilaoDao;

	@Before
	public void setUp() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		leilaoDao = new LeilaoDao(session);
	}
	
	@After
	public void closeSession() {
		session.close();
	}
	
	@Test
	public void deveContarLeiloesNaoEncerrados() {
		Usuario mario = new Usuario("Mario Costa", "mario@costa.com.br");
		
		Leilao ativo = new Leilao("Geladeira nova", 1500.0, mario, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mario, false);
		encerrado.encerra();
		
		usuarioDao.salvar(mario);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		long total = leilaoDao.total();
		
		assertEquals(1, total);
	}
}
