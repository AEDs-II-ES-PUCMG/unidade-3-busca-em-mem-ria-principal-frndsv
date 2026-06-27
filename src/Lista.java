
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> implements IMedicao{

	private Celula<E> primeiro;
	private Celula<E> ultimo;
	private int tamanho;

	private int operacoes;
	private double tempo;

    /** Cria uma lista vazia com elemento sentinela */
	public Lista() {
		Celula<E> sentinela = new Celula<>();
		
		this.primeiro = this.ultimo = sentinela;
		this.tamanho = 0;
	}
	
    /**
     * Indica se a lista está vazia ou não
     * @return TRUE/FALSE conforme a lista esteja vazia ou não 
     */
	public boolean vazia() {
		return (this.primeiro == this.ultimo);
	}
	
    /**
     * Insere um elemento na posição final da lista.
     * @param elemento Elemento a ser inserido.
     */
    public void inserir(E elemento) {
        inserir(elemento, tamanho);
    }

    /**
     * Insere um novo elemento na posição indicada. A posição máxima é o tamanho da lista e a mínima é 0.
     * @param novo Elemento a ser inserido.
     * @param posicao Posição de referência para inserção.
     * @throws IndexOutOfBoundsException em caso de posição inválida
     */
	public void inserir(E novo, int posicao) {
		LocalDateTime inicio = LocalDateTime.now();
		operacoes = 0;

		Celula<E> anterior, novaCelula, proximaCelula;
		
		if ((posicao < 0) || (posicao > this.tamanho))
			throw new IndexOutOfBoundsException("Não foi possível inserir o item na lista: "
					+ "a posição informada é inválida!");
		
		anterior = this.primeiro;
		
		for (int i = 0; i < posicao; i++){
			anterior = anterior.getProximo();
			operacoes++;
		}		
		
		novaCelula = new Celula<>(novo);
		
		operacoes++;
		proximaCelula = anterior.getProximo();
		anterior.setProximo(novaCelula);
		novaCelula.setProximo(proximaCelula);
			
		if (posicao == this.tamanho)  // a inserção ocorreu na última posição da lista
			this.ultimo = novaCelula;
		
		LocalDateTime fim = LocalDateTime.now();
		tempo = Duration.between(inicio, fim).toNanos();
		
		this.tamanho++;		
	}

    /**
     * Remove o último elemento da lista.
     * @return O elemento removido
     * @throws IllegalStateException em caso de lista vazia
     */
	public E remover() {
        return remover(tamanho-1);
     }
 
     
     /**
      * Remove um elemento da posição indicada. Se a lista estiveer vazia ou a posição for inválida (<0 ou >=tamanho),
      * gera exceções. O primeiro elemento da lista é considerado como posição 0.
      * @param posicao Posição do elemento a ser retirado (>=0 e < tamanho)
      * @return Elemmento removido da lista
      * @throws IllegalStateException se a lista estiver vazia
      * @throws IndexOutOfBoundsException em caso de posição inválida
      */
	public E remover(int posicao) {
		LocalDateTime inicio = LocalDateTime.now();
		operacoes = 0;

		Celula<E> anterior, celulaRemovida, proximaCelula;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		
		if ((posicao < 0) || (posicao >= this.tamanho ))
			throw new IndexOutOfBoundsException("Não foi possível remover o item da lista: "
					+ "a posição informada é inválida!");
			
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++){
			anterior = anterior.getProximo();
			operacoes++;
		}
		
		operacoes++;
		celulaRemovida = anterior.getProximo();
		proximaCelula = celulaRemovida.getProximo();
		anterior.setProximo(proximaCelula);
		celulaRemovida.setProximo(null);
				
		if (celulaRemovida == this.ultimo)
			this.ultimo = anterior;
				
		this.tamanho--;
		LocalDateTime fim = LocalDateTime.now();
		tempo = Duration.between(inicio, fim).toNanos();
		
		return (celulaRemovida.getItem());	
	}

	/**
	 * Localiza e remove um elemento da lista. A localização é feita
	 * utilizando-se o método equals do objeto armazenado pela lista. 
	 * O método retorna o objeto encontrado na lista, causando exceções
	 * se a lista estiver vazia ou se o objeto não existir na lista.
	 * @param elemento Objeto a ser procurado na lista
	 * @return O objeto removido da lista
	 * @throws IllegalStateException caso a lista esteja vazia
	 * @throws NoSuchElementException caso o elemento procurado não exista na lista.
	 */
	public E remover(E elemento) {
		LocalDateTime inicio = LocalDateTime.now();
		
		operacoes = 0;
		Celula<E> anterior;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		

		anterior = this.primeiro;
		while ((anterior.getProximo() != null) && !(anterior.getProximo().getItem().equals(elemento))){
			anterior = anterior.getProximo();
			operacoes++;
		}
		
		if (anterior.getProximo() == null)
			throw new NoSuchElementException("Item não encontrado!");
		else {
			LocalDateTime fim = LocalDateTime.now();
			tempo = Duration.between(inicio, fim).toNanos();
			return (removerProxima(anterior));
		}
	}

	/**
	 * Método privado que encapsula a remoção de um elemento da lista,
	 * tomando como referência o elemento anterior
	 * @param anterior A célula anterior à que será removida da lista
	 * @return O elemento contido na célula removida da lista
	 */
	private E removerProxima(Celula<E> anterior) {
		
		Celula<E> celulaRemovida, proximaCelula;
		
		operacoes++;
		celulaRemovida = anterior.getProximo();

		operacoes++;
		proximaCelula = celulaRemovida.getProximo();
				
		operacoes+=2;
		anterior.setProximo(proximaCelula);
		celulaRemovida.setProximo(null);
		
		operacoes++;
		if (celulaRemovida == this.ultimo)
			this.ultimo = anterior;
				
		this.tamanho--;
				
		return (celulaRemovida.getItem());	
	}

    /**
     * Retorna, sem retirar, um elemento na posição indicada pelo parâmetro. A primeira posição da lista é 
     * considerada posição 0 e, assim, a última é (tamanho-1). Lança exceções para lista vazia ou posições inválidas.
     * @param posicao Posição do elemento a ser consultado (>=0 e < tamanho)
     * @return O elemento da posição indicada na lista (consulta sem retirada)
     * @throws IllegalStateException se a lista estiver vazia
     * @throws IndexOutOfBoundsException em caso de posição inválida 
     */
    public E elementoNaPosicao(int posicao) {
		LocalDateTime inicio = LocalDateTime.now();
		operacoes=0;
		Celula<E> aux;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível consultar o item da lista: "
					+ "a lista está vazia!");
		
		if ((posicao < 0) || (posicao >= this.tamanho ))
			throw new IndexOutOfBoundsException("Não foi possível consultar o item da lista: "
					+ "a posição informada é inválida! Tamaho da lista: "+tamanho);
			
		aux = primeiro.getProximo();

		for (int i = 0; i < posicao; i++){
			operacoes++;
			aux = aux.getProximo();
		}
		LocalDateTime fim = LocalDateTime.now();
		tempo = Duration.between(inicio, fim).toNanos();
				
		return (aux.getItem());	
	}

    /**
     * Localiza um elemento na lista de acordo com um Predicado. Retorna o primeiro elemento na lista
     * que atende àquele predicado ou nulo caso não exista. Causa exceções em listas vazias ou no caso
	 * de não haver elementos satisfazendo o predicado.
     * @param condicional Predicado com a condição para encontrar um elemento.
     * @return O primeiro elemento encontrado que atenda à condição ou nulo, caso não haja.
     * @throws IllegalStateException se a lista estiver vazia
	 * @throws NoSuchElementException se não houver nenhum elemento satisfazendo a condição.
     */
    public E localizar(Predicate<E> condicional) {	
		LocalDateTime inicio = LocalDateTime.now();
		operacoes=0;
		Celula<E> aux;
		E localizado = null;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		
        aux = primeiro.getProximo();
        while (aux != null && localizado == null) {
			operacoes++;
			if (condicional.test(aux.getItem()))
				localizado = aux.getItem();
			operacoes++;
			aux = aux.getProximo();
		}
		if(localizado == null)
			throw new NoSuchElementException("Item não encontrado!");
		LocalDateTime fim = LocalDateTime.now();
		tempo = Duration.between(inicio, fim).toNanos();
		return localizado;	
	}

	/**
	 * Pesquisa um elemento na lista, utilizando o método equals de Object.
	 * @param procurado Elemento a ser procurado
	 * @return O objeto contido na lista que, segundo equals, é igual ao procurado
	 * @throws NoSuchElementException se não existe elemento na lista igual ao procurado
	 */
	public E pesquisar(E procurado) {
		LocalDateTime inicio = LocalDateTime.now();
		
		operacoes = 0;
		Celula<E> aux;
		E localizado = null;
		operacoes++;
		aux = this.primeiro.getProximo();
		
		while (aux != null && localizado == null) {
			operacoes++;
			if (aux.getItem().equals(procurado))
				localizado = aux.getItem();
			operacoes++;
			aux = aux.getProximo();
		}
		if(localizado==null)
			throw new NoSuchElementException("Item não encontrado!");
		LocalDateTime fim = LocalDateTime.now();
		tempo = Duration.between(inicio, fim).toNanos();
		return localizado;
	}
    /**
     * Conta quantos elementos na lista atendem à condição estabelecida pelo predicado.
     * @param condicional Predicado com a condição para verificação de elementos na lista
     * @return inteiro com a quantidade de elementos que atendem ao predicado (inteiro não-negativo)
     */
    public int contarRepeticoes(Predicate<E> condicional){
        int contador = 0;
        Celula<E> aux;
					
        aux = primeiro.getProximo();
        while (aux != null) {
            if(condicional.test(aux.getItem()))
                contador++;
             aux = aux.getProximo();
        }
        return contador;
	}

    /**
	 * Calcula e retorna o valor médio de um determinado atributo dos elementos da fila,
	 * utilizando uma função de extração fornecida.
	 *
	 * @param extrator uma função que extrai um valor numérico (Double) de cada elemento da fila.
	 * @param quantidade a quantidade de elementos a serem considerados para o cálculo da média;
	 *                   se for maior que o tamanho da fila, será ajustada para o tamanho da fila.
	 * @return o valor médio dos atributos extraídos dos elementos.
	 */
	public double calcularValorTotal(Function<E, Double> extrator) {
		
		double soma = 0;
		Celula<E> aux = primeiro.getProximo();
		
		if (vazia()) {
			throw new IllegalStateException("A lista está vazia!");
		}
		
		while (aux != null) {
			soma += extrator.apply(aux.getItem());
			aux = aux.getProximo();
		}
		return soma;
	}

    /**
     * Retorna a quantidade atual de elementos na lista.
     * @return Inteiro não negativo com a quantidade atual de elementos na lista.
     */
    public int tamanho(){
        return tamanho;
    }

    /**
     * Retorna uma string com informação detalhada de cada elemento da lista. 
     * A string indica as posições dos elementos, iniciando-se em 0.
     * @return Uma string com as informações de cada elemento da lista
     */
    @Override
	public String toString() {
		Celula<E> aux;
		StringBuilder listaString = new StringBuilder("A Lista está vazia!");
		
	    if(!vazia()){
			listaString = new StringBuilder();
            int contador = 0;
			aux = primeiro.getProximo();
			while (aux != null) {
                aux = this.primeiro.getProximo(); 
				//listaString.append("de "+ aux.getItem().getClass().getName());
				while (aux != null) {
					listaString.append("\n"+contador+" - ");
					listaString.append(aux.getItem());
					aux = aux.getProximo();
					contador++;
				}
			}
			listaString.append("\n");
		} 	
        return listaString.toString();
	}

	@Override
	public long getComparacoes() {
		return operacoes;	
	}

	@Override
	public double getTempo() {
		return tempo;	
	}
}
