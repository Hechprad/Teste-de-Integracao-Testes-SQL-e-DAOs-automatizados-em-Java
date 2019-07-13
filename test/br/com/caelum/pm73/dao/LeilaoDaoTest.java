package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

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
		Usuario mario = new Usuario("Mario Costa", "mario@costa.com.br");

		Leilao novo = new Leilao("Geladeira nova", 1500.0, mario, false);
		Leilao usado = new Leilao("XBox", 700.0, mario, true);
		
		usuarioDao.salvar(mario);
		leilaoDao.salvar(novo);
		leilaoDao.salvar(usado);
		
		List<Leilao> listaDeItensNovos = leilaoDao.novos();
		
		assertEquals(1, listaDeItensNovos.size());
		assertEquals("Geladeira nova", listaDeItensNovos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarLeiloesCriadosHaMaisDeUmaSemana() {
			Usuario mario = new Usuario("Mario Costa", "mario@costa.com.br");

			Leilao leilaoAntigo = new Leilao("Geladeira nova", 1500.0, mario, false);
			Calendar dataAntiga = Calendar.getInstance();
			dataAntiga.add(Calendar.DAY_OF_MONTH, -10);
			leilaoAntigo.setDataAbertura(dataAntiga);
			
			Leilao leilaoNovo = new Leilao("XBox", 700.0, mario, true);
			leilaoNovo.setDataAbertura(Calendar.getInstance());
			
			usuarioDao.salvar(mario);
			leilaoDao.salvar(leilaoAntigo);
			leilaoDao.salvar(leilaoNovo);
			
			List<Leilao> listaDeLeiloesAntigos = leilaoDao.antigos();
			
			assertEquals(1, listaDeLeiloesAntigos.size());
			assertEquals(dataAntiga, listaDeLeiloesAntigos.get(0).getDataAbertura());
			assertEquals("Geladeira nova", listaDeLeiloesAntigos.get(0).getNome());
	}
	
	@Test
	public void retornaLeilaoCriadoAExatamenteSeteDias() {
		Usuario mario = new Usuario("Mario Costa", "mario@costa.com.br");
		
		Leilao leilaoSeteDias = new Leilao("Caneta", 1000.0, mario, false);
		Calendar dataDeSeteDiasAtras = Calendar.getInstance();
		dataDeSeteDiasAtras.add(Calendar.DAY_OF_MONTH, -7);
		leilaoSeteDias.setDataAbertura(dataDeSeteDiasAtras);
		
		usuarioDao.salvar(mario);
		leilaoDao.salvar(leilaoSeteDias);
		
		List<Leilao> leiloes = leilaoDao.antigos();
		
		assertEquals(1, leiloes.size());
		assertEquals("Caneta", leiloes.get(0).getNome());
	}
}
