import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> implements IMedicao {

	private Celula<E> primeiro;
	private Celula<E> ultimo;
	private int tamanho;
	private long comparacoes;
	private long inicio;
	private long termino;
	
	public Lista() {
		
		Celula<E> sentinela = new Celula<>();
		
		this.primeiro = this.ultimo = sentinela;
		this.tamanho = 0;
	}
	
	public boolean vazia() {
		
		return (this.primeiro == this.ultimo);
	}
	
	public void inserir(E novo, int posicao) {
		
		Celula<E> anterior, novaCelula, proximaCelula;
		
		if ((posicao < 0) || (posicao > this.tamanho))
			throw new IndexOutOfBoundsException("Não foi possível inserir o item na lista: "
					+ "a posição informada é inválida!");
		
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
				
		novaCelula = new Celula<>(novo);
			
		proximaCelula = anterior.getProximo();
			
		anterior.setProximo(novaCelula);
		novaCelula.setProximo(proximaCelula);
			
		if (posicao == this.tamanho)  // a inserção ocorreu na última posição da lista
			this.ultimo = novaCelula;
			
		this.tamanho++;		
	}
	
	public void inserir(E novo) {
		
		Celula<E> novaCelula = new Celula<E>(novo);
		
		ultimo.setProximo(novaCelula);
		ultimo = ultimo.getProximo();
		tamanho++;
	}
	
	private E removerProxima(Celula<E> anterior) {
		
		Celula<E> celulaRemovida, proximaCelula;
		
		celulaRemovida = anterior.getProximo();
		
		proximaCelula = celulaRemovida.getProximo();
				
		anterior.setProximo(proximaCelula);
		celulaRemovida.setProximo(null);
				
		if (celulaRemovida == this.ultimo)
			this.ultimo = anterior;
				
		this.tamanho--;
				
		return (celulaRemovida.getItem());	
	}
	
	public E remover(int posicao) {
		
		Celula<E> anterior;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		
		if ((posicao < 0) || (posicao >= this.tamanho ))
			throw new IndexOutOfBoundsException("Não foi possível remover o item da lista: "
					+ "a posição informada é inválida!");
			
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
				
		return (removerProxima(anterior));
	}
	
	public E remover(E elemento) {
		
		Celula<E> anterior;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		
		anterior = this.primeiro;
		while ((anterior.getProximo() != null) && !(anterior.getProximo().getItem().equals(elemento)))
			anterior = anterior.getProximo();
		
		if (anterior.getProximo() == null)
			throw new NoSuchElementException("Item não encontrado!");
		else {
			return (removerProxima(anterior));
		}
	}
	
	public E pesquisar(E procurado) {
		
		Celula<E> aux;
		comparacoes = 0;
		inicio = System.nanoTime();
		
		aux = this.primeiro.getProximo();
		
		while (aux != null) {
			comparacoes++;
			if (aux.getItem().equals(procurado)) {
				termino = System.nanoTime();
				return aux.getItem();
			}
			aux = aux.getProximo();
		}
		
		throw new NoSuchElementException("Item não encontrado!");
	}
	
	@Override
	public String toString() {
		
		Celula<E> aux;
		String listaString = "A lista está vazia!\n";
		
		if (!vazia()) {
			listaString = "";
		
			aux = this.primeiro.getProximo();
		
			while (aux != null) {
				listaString += aux.getItem() + "\n";
				aux = aux.getProximo();
			}
		}
		return listaString;
	}
	
	/**
     * Conta quantos elementos da lista atendem à condição estabelecida pelo predicado.
     * @param condicional Predicado com a condição para verificação de elementos da lista
     * @return inteiro com a quantidade de elementos que atendem ao predicado (inteiro não-negativo)
     */
    public int contarRepeticoes(Predicate<E> condicional){
        
    	int repeticoes = 0;
    	Celula<E> aux = primeiro.getProximo();
    	
    	while (aux != null) {
    		if (condicional.test(aux.getItem())) {
    			repeticoes++;
    		}
    		aux = aux.getProximo();
    	}
    	return repeticoes;
	}
    
    /**
   	 * Calcula e retorna o valor total de um determinado atributo dos elementos da lista,
   	 * utilizando uma função de extração fornecida.
   	 * @param extrator uma função que extrai um valor numérico (Double) de cada elemento da lista.
   	 * @return o valor total dos atributos extraídos dos elementos.
   	 */
   	public double calcularValorTotal(Function<E, Double> extrator) {
   	
   		Celula<E> aux;
   		double soma = 0;
   		
   		if (vazia())
			throw new IllegalStateException("A lista está vazia!");
		
   		aux = primeiro.getProximo();
   		while (aux != null) {
   			soma += extrator.apply(aux.getItem());
   			aux = aux.getProximo();
   		}
   		return (soma);
   	}
   	
   	/**
     * Busca na lista o primeiro elemento correspondente ao item informado como parâmetro,
     * utilizando o critério de comparação especificado.
     * O método deve percorrer sequencialmente os elementos da lista e
     * utilizar o {@code Comparator} informado como parâmetro para determinar se
     * um elemento da lista é equivalente ao item procurado.
     * @param criterioDeBusca Comparador com o critério utilizado para comparar os elementos
     *                   da lista com o item procurado
     * @param item Elemento que será buscado na lista
     * @return O primeiro elemento encontrado na lista que satisfaz o critério
     *         de comparação fornecido, caso exista; retorna {@code null} caso nenhum elemento
     *         correspondente seja encontrado
     */
    public E buscarPor(Comparator<E> criterioDeBusca, E item) {
    
    	Celula<E> aux;
    	
    	aux = primeiro.getProximo();
    	while (aux != null) {
    		if (criterioDeBusca.compare(aux.getItem(), item) == 0) {
    			return aux.getItem();
    		}
    		aux = aux.getProximo();
    	}
    	return null;
    }
    
    /**
     * Calcula o somatório do produto entre dois valores extraídos de cada elemento armazenado na lista.
     * Para cada elemento da lista, o método deve:
     * 		- extrair um valor numérico do tipo {@code double};
     *  	- extrair um valor inteiro utilizado como fator multiplicador;
     *  	- multiplicar os valores obtidos;
     *   	- acumular o resultado no somatório final.
     * O resultado corresponde ao somatório do produto entre os valores
     * extraídos de cada elemento.
     * @param extratorValor Função responsável por extrair o valor numérico do elemento
     * @param extratorFator Função responsável por extrair o fator multiplicador do elemento
     * @return O somatório dos produtos calculados para os elementos da lista
     * @throws IllegalStateException caso a lista esteja vazia
     */
    public double somarMultiplicacoes(Function<E, Double> extratorValor, Function<E, Integer> extratorFator) {
		
		double soma = 0;
		Celula<E> aux = primeiro.getProximo();
		
		if (vazia()) {
			throw new IllegalStateException("A lista está vazia!");
		}
		
		while (aux != null) {
			soma += (extratorValor.apply(aux.getItem()) * extratorFator.apply(aux.getItem()));
			aux = aux.getProximo();
		}
		
		return (soma);
	}

    /**
	 * Retorna uma nova lista contendo os elementos da lista original que satisfazem
	 * uma condição específica.
	 *
	 * @param condicional Uma função (predicado) que testa se um elemento deve ser incluído na nova lista.
	 * @return Uma nova lista contendo os elementos que satisfazem a condição especificada
	 * @throws IllegalStateException caso a lista esteja vazia
     */
	public Lista<E> filtrar(Predicate<E> condicional) {
		
		Celula<E> aux = primeiro.getProximo();
		Lista<E> subLista = new Lista<>();
		
		if (vazia()) {
			throw new IllegalStateException("A lista está vazia!");
		}
		
		while (aux != null) {
			if (condicional.test(aux.getItem())) {
				subLista.inserir(aux.getItem(), subLista.tamanho());
			}
			aux = aux.getProximo();
		}
		
		return subLista;
	}	

	public int tamanho() {
		return tamanho;
	}

	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000;
	}
}
