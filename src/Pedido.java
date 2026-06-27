import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class Pedido {

	/** Vetor para armazenar os produtos do pedido */
	private Lista<Produto> produtos;
	
	/** Data de criação do pedido */
	private LocalDate dataPedido;
	
	/** Indica a quantidade total de produtos no pedido até o momento */
	private int quantProdutos = 0;
	
	/** Construtor do pedido.
	 *  Deve criar o vetor de produtos do pedido e armazenar a data atual do sistema como a data do pedido */
	public Pedido() {		
		produtos = new Lista<Produto>();
		quantProdutos = 0;
		dataPedido = LocalDate.now();
	}
	
	/**
     * Inclui um produto neste pedido e aumenta a quantidade de produtos armazenados no pedido até o momento.
     * @param novo O produto a ser incluído no pedido
     * @return A quantidade de produtos no pedido após a inclusão
     */
	public int incluirProduto(Produto novo) {
		
		if(novo == null)
			throw new IllegalArgumentException("Não se pode guardar um produto vazio no pedido");
		
		produtos.inserir(novo);
		quantProdutos++;
		return quantProdutos;
	}
	
	public Lista<Produto> getProdutos() {
		return produtos;
	}
	
	/**
     * Calcula e retorna o valor final do pedido (soma do valor de venda de todos os produtos do pedido)
     * @return Valor final do pedido (double)
     */
	public double valorFinal() {	
		return produtos.calcularValorTotal((prod -> prod.valorDeVenda()));
	}
	/**
     * Representação, em String, do pedido.
     * Contém um cabeçalho com sua data e depois, em cada linha, a descrição de cada produto.
     * Ao final, mostra o valor a ser pago pelo pedido.
     * @return Uma string contendo dados do pedido conforme especificado (cabeçalho, detalhes, valor a pagar)
     */
	public String toString() {
		
		StringBuilder stringPedido = new StringBuilder();
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		stringPedido.append("Pedido na data " + formatoData.format(dataPedido) + " - ");
		stringPedido.append("Valor a pagar: R$ " + String.format("%.2f\n", valorFinal()));
		
		stringPedido.append(produtos.toString());
		
		return stringPedido.toString();
	}
	
	/**
	 * Conta as repetições de um produto dentro do pedido.  
	 * @param produto Objeto "Produto" com a descrição do produto a ser contado
	 * @return Quantidade de repetições deste produto no pedido
	 */
	public int repeticoes(Produto produto){
		Predicate<Produto> condicao = ((prod) -> prod.descricao.equals(produto.descricao)); 
		return produtos.contarRepeticoes(condicao);
	}

	/**
	 * Deve retornar uma descrição resumida do pedido, em uma única linha.
	 * O formato deve ser o seguinte:
	 * "Pedido com XX produtos em DD/MM/AAAA, valor total de R$ XX,XX"
	 * @return Uma string como especificada acima
	 */
	public String resumo() {
		
		//"Pedido com XX produtos em DD/MM/AAAA, valor total de R$ XX,XX"
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		return ("Pedido com " + quantProdutos + " produtos em " + formatoData.format(dataPedido)
		+ ", valor total de R$ " + String.format("%.2f", valorFinal()));
	}
}

