package br.com.caelum.pm73.builder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.caelum.pm73.dominio.Lance;
import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoBuilder {
	private Usuario dono;
    private double valor;
    private String nome;
    private boolean usado;
    private Calendar dataAbertura;
    private boolean encerrado;
	private List<Lance> lances;
	private List<Calendar> datasDosLances;
	private List<Usuario> usuariosDosLances;
	private List<Double> valoresDosLances;

    public LeilaoBuilder() {
        this.dono = new Usuario("Jorge Hecherat", "hech@email.com.br");
        this.valor = 1500.0;
        this.nome = "Caneta";
        this.usado = false;
        this.dataAbertura = Calendar.getInstance();
        this.lances = new ArrayList<Lance>();
        this.datasDosLances = new ArrayList<Calendar>();
    	this.usuariosDosLances = new ArrayList<Usuario>();
    	this.valoresDosLances = new ArrayList<Double>();
    }

    public LeilaoBuilder comDono(Usuario dono) {
        this.dono = dono;
        return this;
    }

    public LeilaoBuilder comValor(double valor) {
        this.valor = valor;
        return this;
    }

    public LeilaoBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }
    
    public LeilaoBuilder comLance(Calendar dataDoLance, Usuario usuarioDoLance, double valorDolance) {
    	this.datasDosLances.add(dataDoLance);
    	this.usuariosDosLances.add(usuarioDoLance);
    	this.valoresDosLances.add(valorDolance);
    	return this;
	}

    public LeilaoBuilder usado() {
        this.usado = true;
        return this;
    }

    public LeilaoBuilder encerrado() {
        this.encerrado = true;
        return this;
    }

    public LeilaoBuilder diasAtras(int dias) {
        Calendar data = Calendar.getInstance();
        data.add(Calendar.DAY_OF_MONTH, -dias);

        this.dataAbertura = data;

        return this;
    }

	private void montaListaDeLances(Leilao leilao) {
		if(valoresDosLances.size() > 1 && valoresDosLances.get(0) != null) {
    		int i = 0;
    		for (Double valor : valoresDosLances) {
    			lances.add(new Lance(datasDosLances.get(i), 
    					usuariosDosLances.get(i), 
    					valor, 
    					leilao));
    		}
    	}
	}

	private void adicionaLanceAoLeilao(Leilao leilao) {
		if(lances.size() > 1 && lances.get(0) != null) {
			for (Lance lance : lances) {
				leilao.adicionaLance(lance);
			}
		}
	}

	private void propoeLances(Leilao leilao) {
		montaListaDeLances(leilao);
		adicionaLanceAoLeilao(leilao);
	}

	public Leilao constroi() {
    	Leilao leilao = new Leilao(nome, valor, dono, usado);
        leilao.setDataAbertura(dataAbertura);
        propoeLances(leilao);
        if(encerrado) leilao.encerra();

        return leilao;
    }

}
