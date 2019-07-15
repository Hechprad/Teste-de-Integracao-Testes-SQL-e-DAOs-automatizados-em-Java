package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

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
		assertEquals(0L, total);
	}
	
	@Test
	public void deveRetornarApenasLeiloesNovos() {
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilões encerrados
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
		// pegando lista de leilões novos com o DAO
		List<Leilao> listaDeItensNovos = leilaoDao.novos();
		// verificando quantidade de leilões e nome do leilão na lista
		assertEquals(1L, listaDeItensNovos.size());
		assertEquals("Caneta", listaDeItensNovos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarLeiloesCriadosHaMaisDeUmaSemana() {
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilão com data de dez dias atrás
		Leilao leilaoAntigo = new LeilaoBuilder()
				.comDono(hech)
				.diasAtras(10)
				.constroi();
		// criando leilão com a data de hoje
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
		// pegando lista de leilões antigos com o DAO
		List<Leilao> listaDeLeiloesAntigos = leilaoDao.antigos();
		// verificando quantidade de leilões e nome do leilão na lista
		assertEquals(1L, listaDeLeiloesAntigos.size());
		assertEquals("Caneta", listaDeLeiloesAntigos.get(0).getNome());
	}
	
	@Test
	public void retornaLeilaoCriadoAExatamenteSeteDias() {
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilão com data de sete dias atrás
		Leilao leilaoSeteDias = new LeilaoBuilder()
				.comValor(1000.0)
				.comDono(hech)
				.diasAtras(7)
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilaoSeteDias);
		// pegando lista de leilões antigos com o DAO
		List<Leilao> leiloes = leilaoDao.antigos();
		// verificando quantidade de leilões e nome do leilão na lista
		assertEquals(1L, leiloes.size());
		assertEquals("Caneta", leiloes.get(0).getNome());
	}

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {
		// definindo o período do intervalo, dez dias
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilão com data de dois dias atrás
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(hech)
				.diasAtras(2)
				.constroi();
		// criando leilão com data de vinte dias atrás
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
		// pegando lista de leilões com o DAO no período definido
		List<Leilao> leiloesPorPeriodo = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		// verificando quantidade de leilões e nome do leilão na lista
		assertEquals(1L, leiloesPorPeriodo.size());
		assertEquals("Caneta", leiloesPorPeriodo.get(0).getNome());
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {
		// definindo o período do intervalo, dez dias
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		// criando usuário
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		// criando leilão com data de dois dias atrás e encerrado
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(hech)
				.diasAtras(2)
				.encerrado()
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		leilaoDao.salvar(leilao1);
		// pegando lista de leilões com o DAO no período definido
		List<Leilao> leiloesPorPeriodo = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		// verificando quantidade de leilões na lista
		assertEquals(0L, leiloesPorPeriodo.size());
	}

	@Test
	public void deveRetornarLeilõesDisputados() {
		// criando usuarios
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		Usuario alan = new Usuario("Alan R", "alan@email.com.br");
		// criando leilões com 3 lances
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(hech)
				.comLance(Calendar.getInstance(), hech, 1500.0)
				.comLance(Calendar.getInstance(), alan, 1600.0)
				.constroi();
		Leilao leilao2 = new LeilaoBuilder()
				.comDono(alan)
				.comNome("Geladeira")
				.comValor(1000.0)
				.comLance(Calendar.getInstance(), alan, 1000.0)
				.comLance(Calendar.getInstance(), hech, 1100.0)
				.comLance(Calendar.getInstance(), alan, 1200.0)
				.comLance(Calendar.getInstance(), hech, 1300.0)
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		usuarioDao.salvar(alan);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);
		// pegando lista de leilões disputados com o DAO entre os valores definidos
		List<Leilao> leiloesDisputados = leilaoDao.disputadosEntre(900.0, 2000.0);
		// verificando quantidade e valor inicial do leilão na lista
		assertEquals(1L, leiloesDisputados.size());
		assertEquals(1000.0, leiloesDisputados.get(0).getValorInicial(), 0.00001);
	}
	
	@Test
	public void deveRetornarListaDeLeiloesDoUsuarioSemRepeticao() {
		// criando usuarios
		Usuario hech = new Usuario("Jorge Hecherat", "hech@email.com.br");
		Usuario comprador1 = new Usuario("Alan R", "alan@email.com.br");
		Usuario comprador2 = new Usuario("Sr. Peixoto", "peixoto@email.com.br");
		// criando leilões
		Leilao leilao1 = new LeilaoBuilder()
				.comDono(hech)
				.comNome("TV")
				.comValor(800.0)
				.comLance(Calendar.getInstance(), comprador1, 1000.0)
				.comLance(Calendar.getInstance(), comprador2, 1100.0)
				.constroi();
		Leilao leilao2 = new LeilaoBuilder()
				.comDono(hech)
				.comNome("PC")
				.comValor(2800.0)
				.comLance(Calendar.getInstance(), comprador1, 3000.0)
				.constroi();
		// persistindo os dados no banco de dados
		usuarioDao.salvar(hech);
		usuarioDao.salvar(comprador1);
		usuarioDao.salvar(comprador2);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);
		// pegando lista de leilões que o usuário deu pelo menos um lance
		List<Leilao> leiloesComLanceDoUsuarioComprador1 = leilaoDao.listaLeiloesDoUsuario(comprador1);
		List<Leilao> leiloesComLanceDoUsuarioComprador2 = leilaoDao.listaLeiloesDoUsuario(comprador2);
		List<Leilao> leiloesComLanceDoUsuarioHech = leilaoDao.listaLeiloesDoUsuario(hech);
		// verificando quantidade de leilões dos usuários
		assertEquals(2, leiloesComLanceDoUsuarioComprador1.size());
		assertEquals(1, leiloesComLanceDoUsuarioComprador2.size());
		assertEquals(0, leiloesComLanceDoUsuarioHech.size());
	}
	
	@Test
    public void listaDeLeiloesDeUmUsuarioNaoTemRepeticao() throws Exception {
        Usuario dono = new Usuario("Mauricio", "m@a.com");
        Usuario comprador = new Usuario("Victor", "v@v.com");
        Leilao leilao = new LeilaoBuilder()
            .comDono(dono)
            .comLance(Calendar.getInstance(), comprador, 100.0)
            .comLance(Calendar.getInstance(), comprador, 200.0)
            .constroi();
        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador);
        leilaoDao.salvar(leilao);

        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(comprador);
        assertEquals(1, leiloes.size());
        assertEquals(leilao, leiloes.get(0));
    }
	
	@Test
    public void devolveAMediaDoValorInicialDosLeiloesQueOUsuarioParticipou(){
        Usuario dono = new Usuario("Mauricio", "m@a.com");
        Usuario comprador = new Usuario("Victor", "v@v.com");
        Leilao leilao = new LeilaoBuilder()
            .comDono(dono)
            .comValor(50.0)
            .comLance(Calendar.getInstance(), comprador, 100.0)
            .comLance(Calendar.getInstance(), comprador, 200.0)
            .constroi();
        Leilao leilao2 = new LeilaoBuilder()
            .comDono(dono)
            .comValor(250.0)
            .comLance(Calendar.getInstance(), comprador, 100.0)
            .constroi();
        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador);
        leilaoDao.salvar(leilao);
        leilaoDao.salvar(leilao2);

        assertEquals(150.0, leilaoDao.getValorInicialMedioDoUsuario(comprador), 0.001);
    }
}
