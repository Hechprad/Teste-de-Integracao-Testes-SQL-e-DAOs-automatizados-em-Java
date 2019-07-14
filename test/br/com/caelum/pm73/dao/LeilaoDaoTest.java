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
	public void deveContarLeiloesNaoEncerrados() {
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�o ativo
		Leilao ativo = new LeilaoBuilder()
				.comDono(hech)
				.constroi();
		// criando leil�o encerrado
		Leilao encerrado = new LeilaoBuilder()
				.comDono(hech)
				.encerrado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		// pegando o total de leil�es ativos com o DAO
		long total = leilaoDao.total();
		// verificando a quantidade de leil�es ativos da lista 'total'
		assertEquals(1L, total);
	}
	
	@Test
	public void deveRetornarZeroCasoNaoHajaLeiloesNovos() {
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�es encerrados
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
		// pegando o total de leil�es ativos com o DAO
		long total = leilaoDao.total();
		// verificando a quantidade de leil�es ativos da lista 'total'
		assertEquals(0L, total);
	}
	
	@Test
	public void deveRetornarApenasLeiloesNovos() {
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�es encerrados
		Leilao novo = new LeilaoBuilder()
				.comDono(hech)
				.constroi();
		Leilao usado = new LeilaoBuilder()
				.comNome("XBox")
				.comValor(700.0)
				.comDono(hech)
				.usado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(novo);
		leilaoDao.salvar(usado);
		// pegando lista de leil�es novos com o DAO
		List<Leilao> listaDeItensNovos = leilaoDao.novos();
		// verificando quantidade de leil�es e nome do leil�o na lista
		assertEquals(1L, listaDeItensNovos.size());
		assertEquals("Caneta", listaDeItensNovos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarLeiloesCriadosHaMaisDeUmaSemana() {
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�o com data de dez dias atr�s
		Leilao leilaoAntigo = new LeilaoBuilder()
				.comDono(hech)
				.diasAtras(10)
				.constroi();
		// criando leil�o com a data de hoje
		Leilao leilaoNovo = new LeilaoBuilder()
				.comNome("XBox")
				.comValor(700.0)
				.comDono(hech)
				.usado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilaoAntigo);
		leilaoDao.salvar(leilaoNovo);
		// pegando lista de leil�es antigos com o DAO
		List<Leilao> listaDeLeiloesAntigos = leilaoDao.antigos();
		// verificando quantidade de leil�es e nome do leil�o na lista
		assertEquals(1L, listaDeLeiloesAntigos.size());
		assertEquals("Caneta", listaDeLeiloesAntigos.get(0).getNome());
	}
	
	@Test
	public void retornaLeilaoCriadoAExatamenteSeteDias() {
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�o com data de sete dias atr�s
		Leilao leilaoSeteDias = new LeilaoBuilder()
				.comValor(1000.0)
				.comDono(hech)
				.diasAtras(7)
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilaoSeteDias);
		// pegando lista de leil�es antigos com o DAO
		List<Leilao> leiloes = leilaoDao.antigos();
		// verificando quantidade de leil�es e nome do leil�o na lista
		assertEquals(1L, leiloes.size());
		assertEquals("Caneta", leiloes.get(0).getNome());
	}

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {
		// definindo o per�odo do intervalo, dez dias
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�o com data de dois dias atr�s
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(hech)
				.diasAtras(2)
				.constroi();
		// criando leil�o com data de vinte dias atr�s
		Leilao leilao2 = new LeilaoBuilder()
				.comNome("XBox")
				.comValor(700.0)
				.comDono(hech)
				.diasAtras(20)
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);
		// pegando lista de leil�es com o DAO no per�odo definido
		List<Leilao> leiloesPorPeriodo = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		// verificando quantidade de leil�es e nome do leil�o na lista
		assertEquals(1L, leiloesPorPeriodo.size());
		assertEquals("Caneta", leiloesPorPeriodo.get(0).getNome());
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {
		// definindo o per�odo do intervalo, dez dias
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		// criando usu�rio
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leil�o com data de dois dias atr�s e encerrado
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(hech)
				.diasAtras(2)
				.encerrado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilao1);
		// pegando lista de leil�es com o DAO no per�odo definido
		List<Leilao> leiloesPorPeriodo = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		// verificando quantidade de leil�es na lista
		assertEquals(0L, leiloesPorPeriodo.size());
	}
}
