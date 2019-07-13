package br.com.caelum.pm73.dao;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		leilaoDao.total();
	}
}
