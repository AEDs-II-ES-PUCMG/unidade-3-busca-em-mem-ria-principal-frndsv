import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V>{

	private No<K, V> raiz; // referência à raiz da árvore.
	private Comparator<K> comparador; //comparador empregado para definir "menores" e "maiores".
	private int tamanho;

    private long comparacoes;
    private double tempo;
	
	/**
	 * Método auxiliar para inicialização da árvore binária de busca.
	 * 
	 * Este método define a raiz da árvore como {@code null} e seu tamanho como 0.
	 * Se o comparador fornecido for {@code null}, o comparador padrão de ordem natural
	 * será utilizado.
	 * 
	 * @param comparador o comparador para organizar os elementos da árvore.
	 */
	@SuppressWarnings("unchecked")
	private void init(Comparator<K> comparador) {
		raiz = null;
		tamanho = 0;
		if (comparador == null) {
			comparador = (Comparator<K>) Comparator.naturalOrder();
		}
		this.comparador = comparador;
	}
	
	/**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária de busca vazia. Para isso, esse método atribui null à raiz da árvore.
     */
    public ABB() {
        init(null);
    }

    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária de busca vazia utilizando o
     * comparador fornecido para definir a organização dos elementos na árvore.
     * Para isso, esse método atribui null à raiz da árvore.
     *  
     * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.  
     */
    public ABB(Comparator<K> comparador) {
        init(comparador);
    }
    
    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária a partir de uma outra árvore binária de busca,
     * com os mesmos itens, mas usando uma nova chave.
     * @param original a árvore binária de busca original.
     * @param funcaoChave a função que irá extrair a nova chave de cada item para a nova árvore.
     */
    public ABB(ABB<?,V> original, Function<V,K> funcaoChave, Comparator<K> comparador) {
        ABB<K,V> nova = new ABB<>();
        nova = copiarArvore(original.raiz, funcaoChave, nova);
        this.raiz = nova.raiz;
        this.comparador = comparador;
    
    }
    
    /**
     * Recursivamente, copia os elementos da árvore original para esta, num processo análogo ao caminhamento em ordem.
     * @param <T> Tipo da nova chave.
     * @param raizArvore raiz da árvore original que será copiada.
     * @param funcaoChave função extratora da nova chave para cada item da árvore.
     * @param novaArvore Nova árvore. Parâmetro usado para permitir o retorno da recursividade.
     * @return A nova árvore com os itens copiados e usando a chave indicada pela função extratora.
     */
    private <T> ABB<T,V> copiarArvore(No<?,V> raizArvore, Function<V,T> funcaoChave, ABB<T,V> novaArvore) {
    	
        if (raizArvore != null) {
    		novaArvore = copiarArvore(raizArvore.getEsquerda(), funcaoChave, novaArvore);
            V item = raizArvore.getItem();
            T chave = funcaoChave.apply(item);
    		novaArvore.inserir(chave, item);
    		novaArvore = copiarArvore(raizArvore.getDireita(), funcaoChave, novaArvore);
    	}
        return novaArvore;
    }

    /**
     * Método booleano que indica se a árvore está vazia ou não.
     * @return
     * verdadeiro: se a raiz da árvore for null, o que significa que a árvore está vazia.
     * falso: se a raiz da árvore não for null, o que significa que a árvore não está vazia.
     */
    public Boolean vazia() {
        return (this.raiz == null);
    }
    
    @Override
    /**
     * Método que encapsula a pesquisa recursiva de itens na árvore.
     * @param chave a chave do item que será pesquisado na árvore.
     * @return o valor associado à chave.
     */
	public V pesquisar(K chave) {	
        comparacoes = 0;
        LocalDateTime inicio = LocalDateTime.now();
        V item = pesquisar(raiz, chave);
        LocalDateTime fim = LocalDateTime.now();
        tempo = Duration.between(inicio, fim).toNanos();
    	return item; 
    }
    
    private V pesquisar(No<K, V> raizArvore, K procurado) {
    	
    	int comparacao;
    	
        comparacoes++;
    	if (raizArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(procurado, raizArvore.getChave());
    	
        comparacoes++;   
    	if (comparacao == 0)
    		/// O item procurado foi encontrado.
    		return raizArvore.getItem();
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		return pesquisar(raizArvore.getEsquerda(), procurado);
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		return pesquisar(raizArvore.getDireita(), procurado);
    }
    
    @Override
    /**
     * Método que encapsula a adição recursiva de itens à árvore, associando-o à chave fornecida.
     * @param chave a chave associada ao item que será inserido na árvore.
     * @param item o item que será inserido na árvore.
     * 
     * @return o tamanho atualizado da árvore após a execução da operação de inserção.
     */
	public int inserir(K chave, V item) {
        comparacoes = 0;
        LocalDateTime inicio = LocalDateTime.now();
        raiz = inserir(raiz, chave, item);
        LocalDateTime fim = LocalDateTime.now();
        tempo = Duration.between(inicio, fim).toNanos();
        tamanho++;
		return tamanho;
	}
    
    /**
     * Método recursivo responsável por adicionar um item à árvore.
     * @param raizArvore a raiz da árvore ou sub-árvore em que o item será adicionado.
     * @param chave a chave associada ao item que deverá ser inserido.
     * @param item o item que deverá ser adicionado à árvore.
     * @return a raiz atualizada da árvore ou sub-árvore em que o item foi adicionado.
     * @throws RuntimeException se um item com a mesma chave já estiver presente na árvore.
     */
    protected No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {
    	comparacoes++;
        if(raizArvore==null)
            return new No<K,V>(chave, item);
        
        int comparacao = comparador.compare(chave, raizArvore.getChave());

        comparacoes++;
        if(comparacao > 0)
           raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
        else if(comparacao < 0)
                raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
    	else  //comparacao == 0, ou seja, chave existe
            throw new IllegalArgumentException("Elemento já existe na árvore");
        return raizArvore;
    }

    @Override 
    public String toString(){
    	return percorrer();
    }

    @Override
	public String percorrer() {
    	if (vazia())
    		throw new IllegalStateException("A árvore está vazia!");
    	
    	return caminhamentoEmOrdem(raiz);
	}
    
    private String caminhamentoEmOrdem(No<K,V> raizArvore) {
    	
        if (raizArvore != null) {
    		String resposta = caminhamentoEmOrdem(raizArvore.getEsquerda());
    		resposta += raizArvore.getItem()+"\n";
    		resposta += caminhamentoEmOrdem(raizArvore.getDireita());
            return resposta;
    	}
        else return "";
    }
    
    public Lista<V> recortar(K chaveInicio, K chaveFinal){
        Lista<V> listaRecorte = new Lista<>();
        recortarRecursivo(raiz, chaveInicio, chaveFinal, listaRecorte);
        return listaRecorte;
    }

    private void recortarRecursivo(No<K,V> raizSubArvore, K chaveInicio, K chaveFinal, Lista<V> listaRecorte){
        if(raizSubArvore == null)
            return;
        if( comparador.compare(chaveInicio, raizSubArvore.getChave()) > 0)
                recortarRecursivo(raizSubArvore.getDireita(), chaveInicio, chaveFinal, listaRecorte);
        else if( comparador.compare(chaveFinal, raizSubArvore.getChave()) < 0)
                recortarRecursivo(raizSubArvore.getEsquerda(), chaveInicio, chaveFinal, listaRecorte);
        else {
            recortarRecursivo(raizSubArvore.getEsquerda(), chaveInicio, chaveFinal, listaRecorte);
            listaRecorte.inserir(raizSubArvore.getItem());
            recortarRecursivo(raizSubArvore.getDireita(), chaveInicio, chaveFinal, listaRecorte);
        }
    }

	@Override
	/**
     * Método que encapsula a remoção recursiva de um item da árvore.
     * @param chave a chave do item que deverá ser localizado e removido da árvore.
     * @return o valor associado ao item removido.
	 */
	public V remover(K chave) {
		V elemento = pesquisar(raiz, chave);
        //marcar tempo e comparações
        raiz = remover(raiz, chave);

		return elemento;
	}
    
    protected No<K,V> remover(No<K,V> raizSubArvore, K chave) {
        int comparacao;
    	
        comparacoes++;
    	if (raizSubArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(chave, raizSubArvore.getChave());
    	
        comparacoes++;   
    	if (comparacao == 0){
    		/// O item procurado foi encontrado.
            int grau = raizSubArvore.grau();
            switch (grau) {
                case 0 -> {return null;}
                case 1 -> {return raizSubArvore.getDireita();}
                case -1 -> {return raizSubArvore.getEsquerda();}
                case 2 -> removerAntecessor(raizSubArvore);
            }   
        }	
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		raizSubArvore.setEsquerda(remover(raizSubArvore.getEsquerda(), chave));
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		raizSubArvore.setDireita(remover(raizSubArvore.getDireita(), chave));
    
        return raizSubArvore;
    }

    private void removerAntecessor(No<K,V> raizSubArvore){
        //descobrindo o antecessor
        No<K,V> antecessor = raizSubArvore.getEsquerda();
        while (antecessor.getDireita()!=null) {
            antecessor = antecessor.getDireita();            
        }

        //copiar dados do antecessor para a raiz grau 2
        raizSubArvore.setItem(antecessor.getItem());
        raizSubArvore.setChave(antecessor.getChave());

        //remover o antecessor duplicado do lado esquerdo
        raizSubArvore.setEsquerda(remover(raizSubArvore.getEsquerda(), raizSubArvore.getChave()));
    }

	@Override
	public int tamanho() {
		return tamanho;
	}
	
	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return tempo;
	}
}