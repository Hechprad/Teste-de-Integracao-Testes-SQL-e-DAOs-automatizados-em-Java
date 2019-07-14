package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.builder.LeilaoBuilder;
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
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilão ativo
		Leilao ativo = new LeilaoBuilder()
				.comDono(hech)
				.constroi();
		// criando leilão encerrado
		Leilao encerrado = new LeilaoBuilder()
				.comDono(hech)
				.encerrado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		// pegando o total de leilões ativos com o DAO
		long total = leilaoDao.total();
		// verificando a quantidade de leilões ativos da lista 'total'
		assertEquals(1L, total);
	}
	
	@Test
	public void deveRetornarZeroCasoNaoHajaLeiloesNovos() {
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilões encerrados
		Leilao encerrado1 = new LeilaoBuilder()
				.comDono(hech)
				.comNome("Geladeira nova")
				.encerrado()
				.constroi();
		Leilao encerrado2 = new LeilaoBuilder()
				.comDono(hech)
				.comNome("XBox")
				.comValor(700.0)
				.encerrado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(encerrado1);
		leilaoDao.salvar(encerrado2);
		// pegando o total de leilões ativos com o DAO
		long total = leilaoDao.total();
		// verificando a quantidade de leilões ativos da lista 'total'
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

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");

		Leilao leilao1 = new Leilao("Geladeira nova", 1500.0, hech, false);
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		leilao1.setDataAbertura(dataDoLeilao1);
		
		Leilao leilao2 = new Leilao("XBox", 700.0, hech, false);
		Calendar dataDoLeilao2 = Calendar.getInstance();
		dataDoLeilao2.add(Calendar.DAY_OF_MONTH, -20);
		leilao2.setDataAbertura(dataDoLeilao2);
		
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);
		
		List<Leilao> leiloesPorPeriodo = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertEquals(1, leiloesPorPeriodo.size());
		assertEquals("Geladeira nova", leiloesPorPeriodo.get(0).getNome());
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");

		Leilao leilao1 = new Leilao("Geladeira nova", 1500.0, hech, false);
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		leilao1.setDataAbertura(dataDoLeilao1);
		leilao1.encerra();
		
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilao1);
		
		List<Leilao> leiloesPorPeriodo = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertEquals(0, leiloesPorPeriodo.size());
	}
}
