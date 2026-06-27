import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class TabelaHash<K, V> implements IMapeamento<K,V> {

	private Lista<Entrada<K, V>>[] tabelaHash; /// tabela que referenciará todas as listas lineares encadeadas.
								      /// Nesse caso, estamos utilizando uma tabela hash com endereçamento em separado,
								      /// ou seja, os itens são armazenados em listas lineares encadeadas.

	private int capacidade; /// tamanho da tabela hash.
	                        /// deve ser um número primo grande para diminuirmos a probabilidade de colisões.

	private int comparacoes;		//contador de operacoes para busca/insercao
	private double tempoExecucao;	//marcação de tempo para busca/insercao
	/**
	 * Construtor da classe.
	 * Esse método é responsável por inicializar a tabela hash que trabalha com endereçamento em separado.
	 * Assim, esse método atribui, ao atributo "capacidade", dessa classe, o valor passado por meio do parâmetro "capacidade".
	 * Esse método também cria um vetor, de tamanho "capacidade", de listas lineares; e o atribui ao atributo "tabelaHash".
	 * Adicionalmente, cada posição do vetor é inicializada com uma lista encadeada vazia.
	 * @param capacidade: quantidade de posições da tabela hash. Cada posição é uma lista encadeada. 
	 * @throws IllegalArgumentException caso a capacidade seja um número não positivo.
	 */
	@SuppressWarnings("unchecked")
	public TabelaHash(int capacidade) {
		if(capacidade < 1)
			throw new IllegalArgumentException("Capacidade não pode ser menor que 1");
		this.capacidade = capacidade;
		this.tabelaHash = (Lista<Entrada<K, V>>[]) new Lista[this.capacidade]; 
		
		for (int i = 0; i < this.capacidade; i++)
			this.tabelaHash[i] = new Lista<>();
	}
	
	/**
	 *  Esse método implementa a função de transformação da tabela hash, 
	 * ou seja, calcula a posição, na tabela hash, em que o item,
	 * que possui a chave informada por meio do parâmetro "chave", deve ser encontrado.
	 * A função de transformação utilizada corresponde ao resto da divisão do hashCode de "chave" pelo tamanho da tabela hash.
	 * @param chave: chave da qual desejamos saber a posição na tabela hash.
	 * @return a posição que o item, cuja chave corresponde a que foi passada como parâmetro para esse método, deve ocupar na tabela hash.
	 */
	private int funcaoHash(K chave) {
		return Math.abs(chave.hashCode() % this.capacidade);
	}
	
	/**
	 * Método responsável por inserir um novo item na tabela hash.
	 * Não é permitido inserir, nessa tabela hash, mais de um item com uma mesma chave. 
	 * @param chave: chave do item que deve ser inserido na tabela hash.
	 * @param item: referência ao item que deve ser inserido na tabela hash.
	 * @return a posição na tabela hash em que o novo item foi inserido.
	 * @throws IllegalArgumentException em caso de um item com a mesma chave já existir na tabela.
	 */
	public int inserir(K chave, V item) {
		LocalDateTime inicio = LocalDateTime.now();
		comparacoes = 1;
		
		/// cálculo da posição da tabela hash em que o novo item deverá ser armazenado.
		int posicao = funcaoHash(chave);
		
		Entrada<K, V> entrada = new Entrada<>(chave, item);
		
		/// pesquisa o item, passado como parâmetro para esse método, na lista encadeada 
		/// associada à posição, da tabela hash, em que esse novo item deverá ser adicionado.
		/// Se o item não for localizado, 
		/// ele é inserido no final da lista encadeada 
		/// associada à posição, da tabela hash, em que esse novo item será localizado. 
		try {
			this.tabelaHash[posicao].pesquisar(entrada);
			comparacoes += tabelaHash[posicao].getComparacoes();
			LocalDateTime fim = LocalDateTime.now();
			tempoExecucao =  Duration.between(inicio, fim).toNanos();
		
			throw new IllegalArgumentException("O item já havia sido inserido anteriormente na tabela hash!");
		} catch (NoSuchElementException excecao) {
			this.tabelaHash[posicao].inserir(entrada);
			comparacoes += tabelaHash[posicao].getComparacoes();
			LocalDateTime fim = LocalDateTime.now();
			tempoExecucao =  Duration.between(inicio, fim).toNanos();
			return posicao;
		}
	}
	
	/**
	 * Método responsável por localizar, na tabela hash, o item
	 * cuja chave corresponde à que foi passada como parâmetro para esse método. 
	 * O método lança uma exceção caso o item não seja localizado na tabela hash.
	 * @param chave: chave do item que deve ser localizado na tabela hash.
	 * @return uma referência ao item encontrado.
     * @throws NoSuchElementException se o elemento não existir (exceção da classe Lista) 
	 */
	public V pesquisar(K chave) {
		LocalDateTime inicio = LocalDateTime.now();
		
		comparacoes = 1;
		/// cálculo da posição da tabela hash em que o item deve estar armazenado.
		int posicao = funcaoHash(chave);
		
		Entrada<K, V> procurado = new Entrada<>(chave, null);
		
		/// pesquisa o item, cuja chave foi passada como parâmetro para esse método,
		/// na lista encadeada associada à posição, da tabela hash, em que esse item deve estar armazenado.
		procurado = this.tabelaHash[posicao].pesquisar(procurado);
		comparacoes += this.tabelaHash[posicao].getComparacoes();
		LocalDateTime fim = LocalDateTime.now();
		tempoExecucao =  Duration.between(inicio, fim).toNanos();
		
		return procurado.getValor();
	}
	
	/**
	 * Método responsável por remover, da tabela hash, o item
	 * cuja chave corresponde à que foi passada como parâmetro para esse método.
	 * O método lança uma exceção caso o item não tenha sido localizado na tabela hash.
	 * @param chave: chave do item que deve ser removido da tabela hash.
	 * @return uma referência ao item removido.
	 * @throws NoSuchElementException se o elemento não existir (exceção da classe Lista) 
	 */
	public V remover(K chave) {
		LocalDateTime inicio = LocalDateTime.now();
		
		comparacoes = 1;
		/// cálculo da posição da tabela hash em que o item deve estar armazenado.
		int posicao = funcaoHash(chave);
		
		Entrada<K, V> procurado = new Entrada<>(chave, null);
		
		/// remove o item, cuja chave foi passada como parâmetro para esse método,
		/// da lista encadeada associada à posição, da tabela hash, em que esse item deve estar armazenado.	
		procurado = this.tabelaHash[posicao].remover(procurado);
		comparacoes += this.tabelaHash[posicao].getComparacoes();
		LocalDateTime fim = LocalDateTime.now();
		tempoExecucao =  Duration.between(inicio, fim).toNanos();
		
		return procurado.getValor();
	}
	
	
	//#region Herança Object
	@Override
	public String toString(){
		return percorrer();
	}
	//#endregion

	//#region Interface IMapeamento
	@Override

	/**
	 * Retorna o tamanho da tabela hash. O tamanho é a quantidade de itens efetivamente
	 * armazenados no momento, ou seja, pode ser um valor inclusive maior do que a sua 
	 * capacidade inicial, dado o tratamento de colisões por lista encadeada.
	 * @return Inteiro, não negativo, com a quantidade de itens armazenados na tabela.
	 */
	public int tamanho() {
		int tamanho = 0;
		for (Lista<Entrada<K,V>> lista : tabelaHash) {
			tamanho += lista.tamanho();
		}
		return tamanho;
	}

	/**
	 * Método responsável por percorrer todo o conteúdo da tabela hash e retornar
	 * sua representação em string.
	 * A string inclui o índice da tabela hash e seu correspondente conteúdo.
	 * Se a posição da tabela hash estiver vazia, é incluída uma mensagem explicativa.
	 * Caso contrário, para todos os itens, armazenados na lista encadeada 
	 * associada a uma posição da tabela hash, são incluídos seus dados, sempre usando
	 * o polimorfismo do toString.
	 */
	@Override
	public String percorrer(){
		StringBuilder conteudo = new StringBuilder("Tabela com "+capacidade+" posições e "+tamanho()+" itens");
		for (int i = 0; i < this.capacidade; i++) {
			conteudo.append("\nPosição[" + i + "]: ");
			if (this.tabelaHash[i].vazia())
			 	conteudo.append("vazia\n");
			 else
				conteudo.append(tabelaHash[i]+"\n");
		}
		return conteudo.toString();
	}

	//#region IMedicao
	@Override
	public long getComparacoes() {
		return comparacoes;	
	}

	@Override
	public double getTempo() {
		return tempoExecucao;	
	}
	//#endregion

	//#endregion

	
}
