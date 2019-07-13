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
		
		// boa prática: contexto de transação (em testes com BD)
		session.beginTransaction();
	}
	
	@After
	public void closeSession() {
		// boa prática: damos um rollback no BD para limpar os dados inseridos no teste
		session.getTransaction().rollback();
		// fechando conexão do BD
		session.close();
	}
	
	@Test
	public void deveContarLeiloesNaoEncerrados() {
		Usuario mario = new Usuario("Mario Costa", "mario@costa.com.br");
		
		Leilao ativo = new Leilao("Geladeira nova", 1500.0, mario, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mario, false);
		encerrado.encerra();
		
		// em um banco de verdade, esses dados seriam inceridos e continuariam ali
		usuarioDao.salvar(mario);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		long total = leilaoDao.total();
		
		assertEquals(1, total);
	}
	
	@Test
	public void deveRetornarZeroCasoNaoHajaLeiloesNovos() {
		Usuario mario = new Usuario("Mario Costa", "mario@costa.com.br");

		Leilao encerrado1 = new Leilao("Geladeira nova", 1500.0, mario, false);
		Leilao encerrado2 = new Leilao("XBox", 700.0, mario, false);
		
		usuarioDao.salvar(mario);
		encerrado1.encerra();
		encerrado2.encerra();
		
		long total = leilaoDao.total();
		
		assertEquals(0, total);
	}
	
	@Test
	public void deveRetornarApenasLeiloesNovos() {
		
	}
}
