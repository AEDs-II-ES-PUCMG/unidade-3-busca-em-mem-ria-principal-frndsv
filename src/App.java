import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    static AVL<String, Produto> produtosCadastradosPorNome;
    
    static AVL<Integer, Produto> produtosCadastradosPorId;
    
    static AVL <Integer, Cliente> clientesPorId;
    
    static int quantosClientes = 0;
    
    static TabelaHash<Produto, Lista<Pedido>> pedidosPorProduto;
    
    static TabelaHash<Cliente, Lista<Pedido>> pedidosPorCliente;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** 
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar produto, por nome");
        System.out.println("3 - Procurar produto, por id");
        System.out.println("4 - Remover produto, por nome");
        System.out.println("5 - Remover produto, por id");
        System.out.println("6 - Recortar a lista de produtos, por nome");
        System.out.println("7 - Recortar a lista de produtos, por id");
        System.out.println("8 - Gravar, em arquivo, pedidos de um produto");
        System.out.println("9 - Procurar cliente, por id");
        System.out.println("10 - Remover cliente, por id");
        System.out.println("11 - Exibir histórico de compras de um cliente");
        System.out.println("0 - Finalizar");
        
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna uma ávore de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna uma árvore vazia em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Uma árvore com os produtos carregados, ou vazia em caso de problemas de leitura.
     */
    static <K> AVL<K, Produto> lerProdutos(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	AVL<K, Produto> produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new AVL<K, Produto>();
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			K chave = extratorDeChave.apply(produto);
    			produtosCadastrados.inserir(chave, produto);
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna uma árvore balanceada (AVL) de clientes. Arquivo-texto no formato
     * N (quantidade de clientes) <br/>
     * nome do cliente <br/>
     * Deve haver uma linha para cada um dos clientes. Retorna uma árvore vazia em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Uma árvore AVL com os clientes carregados, ou vazia em caso de problemas de leitura.
     */
    static AVL<Integer, Cliente> lerClientes(String nomeArquivoDados) {

        Scanner arquivo = null;
        AVL<Integer, Cliente> clientesCadastrados = new AVL<>();

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            int n = Integer.parseInt(arquivo.nextLine());

            quantosClientes = n;

            for (int i = 0; i < n; i++) {
                String nome = arquivo.nextLine();
                Cliente cliente = new Cliente(nome);

                clientesCadastrados.inserir(cliente.hashCode(), cliente);
            }

        } catch (Exception e) {
            return new AVL<>();
        } finally {
            if (arquivo != null) arquivo.close();
        }

        return clientesCadastrados;
    }

    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
    	
    	Produto produto;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	
    	try {
    		produto = produtosCadastrados.pesquisar(procurado);
    	} catch (NoSuchElementException excecao) {
    		produto = null;
    	}
    	
    	System.out.println("Número de comparações realizadas: " + produtosCadastrados.getComparacoes());
    	System.out.println("Tempo de processamento da pesquisa: " + produtosCadastrados.getTempo() + " ms");
        
    	return produto;
    	
    }
    
    /** Localiza um produto na árvore de produtos organizados por id, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null */
    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        
        int idProduto = lerOpcao("Digite o identificador do produto desejado: ", Integer.class);
        
        return localizarProduto(produtosCadastrados, idProduto);
    }
    
    /** Localiza um produto na árvore de produtos organizados por nome, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null */
    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        
    	String descricao;
    	
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        
        return localizarProduto(produtosCadastrados, descricao);
    }

    static Cliente localizarCliente(AVL<Integer, Cliente> clientes, Integer documento) {

        Cliente cliente;

        cabecalho();
        System.out.println("Localizando um cliente...");

        try {
            cliente = clientes.pesquisar(documento);
        } catch (NoSuchElementException e) {
            cliente = null;
        }

        return cliente;
    }

    static Cliente localizarClienteId(AVL<Integer, Cliente> clientes) {

        int id = lerOpcao("Digite o documento do cliente: ", Integer.class);

        return localizarCliente(clientes, id);
    }

    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        StringBuilder  mensagem = new StringBuilder("Produto não encontrado.\n");
        
        if (produto != null) {
            mensagem = new StringBuilder(String.format("%s\n", produto));            
        }

        System.out.println(mensagem.toString());
    }

    static void mostrarCliente(Cliente cliente) {

        cabecalho();
        StringBuilder  mensagem = new StringBuilder("Cliente nao encontrado. \n");

        if (cliente != null) {
            mensagem = new StringBuilder(String.format("%s\n", cliente));
        } 
        
        System.out.println(mensagem.toString());
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static <K> void listarTodosOsProdutos(ABB<K, Produto> produtosCadastrados) {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        System.out.println(produtosCadastrados.toString());
    }
    
    /** Localiza e remove um produto da árvore de produtos organizados por id, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null */
    static Produto removerProdutoId(ABB<Integer, Produto> produtosCadastrados) {
         cabecalho();
         System.out.println("Localizando o produto por id");
         int id = lerOpcao("Digite o id do produto que deve ser removido", Integer.class);
         Produto localizado =  removerProduto(produtosCadastrados, id);
         return localizado;
    }

     /** Localiza e remove um produto na árvore de produtos organizados por nome, a partir do nome de produto informado pelo usuário, e o retorna. 
      *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null */
    static Produto removerProdutoNome(ABB<String, Produto> produtosCadastrados) {
    	String descricao;
         
    	cabecalho();
        System.out.println("Localizando o produto por nome");
        System.out.print("Digite a descrição do produto que deve ser removido: ");
        descricao = teclado.nextLine();
        Produto localizado =  removerProduto(produtosCadastrados, descricao);
        return localizado;
    }

    static <K> Produto removerProduto(ABB<K, Produto> produtosCadastrados, K chave){
         cabecalho();
         Produto localizado =  produtosCadastrados.remover(chave);
         return localizado;
    }

    
    static Cliente removerClienteId(AVL <Integer, Cliente> clientesPorId) {
        cabecalho();
        System.out.println("Localizando o cliente por id");
        int id = lerOpcao("Digite o documento do cliente", Integer.class);
        Cliente localizado = removerCliente(clientesPorId, id);
        return localizado;
    }

    static Produto removerClienteNome(ABB<String, Produto> produtosCadastrados) {
    	String descricao;
         
    	cabecalho();
        System.out.println("Localizando o produto por nome");
        System.out.print("Digite a descrição do produto que deve ser removido: ");
        descricao = teclado.nextLine();
        Produto localizado =  removerProduto(produtosCadastrados, descricao);
        return localizado;
    }

    static Cliente removerCliente(AVL<Integer, Cliente> clientes, Integer chave) {
        cabecalho();
        Cliente localizado = clientes.remover(chave);
        return localizado;
    }


    
    private static <K> void recortarProduto(ABB<K, Produto> produtosCadastrados, K deOnde, K ateOnde) {
    	cabecalho();
    	System.out.println(produtosCadastrados.recortar(deOnde, ateOnde).toString());
    }
    
    private static void recortarProdutosNome(ABB<String, Produto> produtosCadastrados) {
     	
    	String descricaoDeOnde, descricaoAteOnde;
        
    	cabecalho();
    	System.out.print("Digite o nome do primeiro produto do filtro: ");
        descricaoDeOnde = teclado.nextLine();
        System.out.print("Digite o nome do último produto do filtro: ");
        descricaoAteOnde = teclado.nextLine();
        recortarProduto(produtosCadastrados, descricaoDeOnde, descricaoAteOnde);
     }
     
    private static void recortarProdutosId(ABB<Integer, Produto> produtosCadastrados) {
     	
    	cabecalho();
        int idDeOnde = lerOpcao("Digite o id do primeiro produto do filtro", Integer.class);
        int idAteOnde = lerOpcao("Digite o id do último produto do filtro", Integer.class);
        recortarProduto(produtosCadastrados, idDeOnde, idAteOnde);
    }
    
    private static Lista<Pedido> gerarPedidos(int quantidade) {
        Lista<Pedido> pedidos = new Lista<>();
        Random sorteio = new Random(42);
        int quantProdutos;
        int formaDePagamento;
        int quant;
        int idCliente;
        Cliente cliente;

        for (int i = 0; i < quantidade; i++) {
        	formaDePagamento = sorteio.nextInt(2) + 1;

        	// Sorteie um documento de cliente (use sorteio.nextInt(quantosClientes) + 10_000)
        	// e localize o cliente correspondente em clientesPorId.
            idCliente = sorteio.nextInt(quantosClientes) + 10_000;
            cliente = clientesPorId.pesquisar(idCliente);

        	Pedido pedido = new Pedido(LocalDate.now(), formaDePagamento, cliente);
            quantProdutos = sorteio.nextInt(8) + 1;
            for (int j = 0; j < quantProdutos; j++) {
                int id = sorteio.nextInt(7750) + 10_000;
                Produto produto = produtosCadastradosPorId.pesquisar(id);
                quant = sorteio.nextInt(10) + 1;
                pedido.incluirProduto(produto, quant);
                inserirNaTabela(produto, pedido);
            }
            pedidos.inserir(pedido);

            // chamando inserirNaTabelaPedidosDoCliente(cliente, pedido).
            inserirNaTabelaPedidosDoCliente(cliente, pedido);
        }
        return pedidos;
    }

    /**
     * Associa, na tabela hash pedidosPorCliente, o pedido informado ao histórico de pedidos do cliente.
     * Caso o cliente ainda não possua um histórico registrado, um novo deve ser criado.
     */
    private static void inserirNaTabelaPedidosDoCliente(Cliente cliente, Pedido pedido) {

        Lista<Pedido> pedidosDoCliente;

        try {
            pedidosDoCliente = pedidosPorCliente.pesquisar(cliente);
        } catch (NoSuchElementException e) {
            pedidosDoCliente = new Lista<>();
            pedidosPorCliente.inserir(cliente, pedidosDoCliente);
        }

        pedidosDoCliente.inserir(pedido);
    }
    
    private static void inserirNaTabela(Produto produto, Pedido pedido) {
        
    	Lista<Pedido> pedidosDoProduto;
    	
    	try {
    		pedidosDoProduto = pedidosPorProduto.pesquisar(produto);
    	} catch (NoSuchElementException excecao) {
    		pedidosDoProduto = new Lista<>();
    		pedidosPorProduto.inserir(produto, pedidosDoProduto);
    	}
    	pedidosDoProduto.inserir(pedido);
    }
    
    private static void pedidosDoProduto() {
    	
    	Lista<Pedido> pedidosDoProduto;
    	Produto produto = localizarProdutoID(produtosCadastradosPorId);
    	String nomeArquivo = "RelatorioProduto" + produto.hashCode() + ".txt";  
    	
        try {
        	FileWriter arquivoRelatorio = new FileWriter(nomeArquivo, Charset.forName("UTF-8"));
    		
        	pedidosDoProduto = pedidosPorProduto.pesquisar(produto);
        	arquivoRelatorio.append(pedidosDoProduto.toString() + "\n");
            arquivoRelatorio.close();
            System.out.println("Dados salvos em " + nomeArquivo);
        } catch(IOException excecao) {
            System.out.println("Problemas para criar o arquivo " + nomeArquivo + ". Tente novamente");        	
        }
    }
    
    /**
     * Lê o documento de um cliente informado pelo usuário, localiza o cliente correspondente
     * e exibe seu histórico completo de pedidos.
     */
    public static void pedidosDoCliente() {

        cabecalho();

        int id = lerOpcao("Digite o documento do cliente: ", Integer.class);

        try {
            Cliente cliente = clientesPorId.pesquisar(id);

            Lista<Pedido> historico = pedidosPorCliente.pesquisar(cliente);

            System.out.println("CLIENTE:");
            System.out.println(cliente);

            System.out.println("\nHISTÓRICO DE PEDIDOS:");
            System.out.println(historico);

        } catch (NoSuchElementException e) {
            System.out.println("Cliente ou histórico não encontrado.");
        }
    }
    
    public static void main(String[] args) {
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        produtosCadastradosPorNome = lerProdutos(nomeArquivoDados, (p -> p.descricao));
        produtosCadastradosPorId = new AVL<Integer, Produto>(produtosCadastradosPorNome, (p -> p.idProduto));

        nomeArquivoDados = "clientes.txt";
        clientesPorId = lerClientes(nomeArquivoDados);
        
        pedidosPorProduto = new TabelaHash<>((int)(quantosProdutos * 1.25));
        pedidosPorCliente = new TabelaHash<>((int)(quantosClientes * 1.25));
        
        gerarPedidos(25_000);
        
        int opcao = -1;
      
        do{
        	opcao = menu();
            switch (opcao) {
            case 1 -> listarTodosOsProdutos(produtosCadastradosPorNome);
            case 2 -> mostrarProduto(localizarProdutoNome(produtosCadastradosPorNome));
            case 3 -> mostrarProduto(localizarProdutoID(produtosCadastradosPorId));
            case 4 -> mostrarProduto(removerProdutoNome(produtosCadastradosPorNome));
        	case 5 -> mostrarProduto(removerProdutoId(produtosCadastradosPorId));
        	case 6 -> recortarProdutosNome(produtosCadastradosPorNome); 
        	case 7 -> recortarProdutosId(produtosCadastradosPorId); 
        	case 8 -> pedidosDoProduto(); 
            case 9 -> mostrarCliente(localizarClienteId(clientesPorId));
            case 10 -> mostrarCliente(removerClienteId(clientesPorId));
            case 11 -> pedidosDoCliente();

            case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);       

        teclado.close();    
    }
}