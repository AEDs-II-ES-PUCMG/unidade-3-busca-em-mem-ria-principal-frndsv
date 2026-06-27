import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

    /**
     * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
     */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Quantidade de produtos cadastrados atualmente na lista */
    static int quantosProdutos = 0;

    static ABB<Integer, Produto> produtosPorId;
    static ABB<String, Produto> produtosPorNome;
    static TabelaHash<Produto, Lista<Pedido>> pedidosPorProduto;

    /** Limpa o buffer do console, simulando uma limpeza de tela num terminal vt-100 */
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
        limparTela();
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    /**
     * Método genérico para ler dados numéricos do teclado
     * @param <T> A classe de retorno (tipicamente int ou double)
     * @param mensagem Mensagem a ser exibida na leitura
     * @param classe Classe do tipo T para uso na reflaxão Java
     * @return Um valor numérico da classe desejada
     */
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
     * Perceba que poderia haver uma melhor modularização com a criação de uma
     * classe Menu.
     * 
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Procurar produtos, por id");
        System.out.println("2 - Recortar produtos, por descrição");
        System.out.println("3 - Pedidos de um produto, em arquivo");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo-texto e retorna uma árvore de produtos.
     * Arquivo-texto no formato
     * N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna uma árvore vazia em
     * caso de problemas com o arquivo.
     * 
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Uma árvore com os produtos carregados, ou vazia em caso de problemas
     *         de leitura.
     */
    static <T> ABB<T, Produto> lerProdutos(String nomeArquivoDados,
            Function<Produto, T> extratorDeChave) {

        Scanner arquivo = null;
        int numProdutos;
        String linha;
        Produto produto;
        ABB<T, Produto> produtosCadastrados;

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            numProdutos = Integer.parseInt(arquivo.nextLine());
            produtosCadastrados = new AVL<>();

            for (int i = 0; i < numProdutos; i++) {
                linha = arquivo.nextLine();
                produto = Produto.criarDoTexto(linha);
                T chave = extratorDeChave.apply(produto);
                produtosCadastrados.inserir(chave, produto);
            }
            quantosProdutos = produtosCadastrados.tamanho();

        } catch (IOException excecaoArquivo) {
            produtosCadastrados = null;
        } finally {
            arquivo.close();
        }

        return produtosCadastrados;
    }

    /**
     * Localiza um produto na árvore de produtos organizados por id, a partir do
     * código de produto informado pelo usuário, e o retorna.
     * Em caso de não encontrar o produto, retorna null
     */
    static Produto localizarProdutoID() {
        cabecalho();
        System.out.println("LOCALIZANDO POR ID");
        int ID = lerOpcao("Digite o ID para busca", Integer.class);
        Produto localizado = localizarProduto(produtosPorId, ID);
        mostrarProduto(localizado);
        return localizado;
    }

    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K chave) {
        cabecalho();
        Produto localizado = produtosCadastrados.pesquisar(chave);
        System.out.println("Tempo: " + produtosCadastrados.getTempo());
        System.out.println("Comparações: " + produtosCadastrados.getComparacoes());
        pausa();
        return localizado;
    }

    private static void mostrarProduto(Produto produto) {

        cabecalho();
        String mensagem = "Dados inválidos para o produto!";

        if (produto != null) {
            mensagem = String.format("Dados do produto:\n%s", produto);
        }

        System.out.println(mensagem);
    }

    private static Lista<Pedido> gerarPedidos(int quantidade) {
        Lista<Pedido> pedidos = new Lista<>();
        Random sorteio = new Random(42);
        int quantProdutos;
        for (int i = 0; i < quantidade; i++) {
            Pedido ped = new Pedido();
            quantProdutos = sorteio.nextInt(8) + 1;
            for (int j = 0; j < quantProdutos; j++) {
                int id = sorteio.nextInt(7750) + 10_000;
                Produto prod = produtosPorId.pesquisar(id);
                ped.incluirProduto(prod);
                inserirNaTabela(prod, ped);
            }
            pedidos.inserir(ped);
        }
        return pedidos;
    }

    private static void inserirNaTabela(Produto produto, Pedido pedido) {
        // TODO
    }

    private static void recortarArvore(ABB<String, Produto> arvore) {

        cabecalho();
        System.out.print("Digite ponto de início do filtro: ");
        String descIni = teclado.nextLine();
        System.out.print("Digite ponto de fim do filtro: ");
        String descFim = teclado.nextLine();

        System.out.println(arvore.recortar(descIni, descFim));
    }

    static void pedidosDoProduto(){
        Produto produto = localizarProdutoID();
        String nomeArquivo = "RelatorioProduto"+produto.hashCode()+".txt";    
        try (FileWriter arquivoRelatorio = new FileWriter(nomeArquivo)){
            Lista<Pedido> listaProd = pedidosPorProduto.pesquisar(produto);
            arquivoRelatorio.append(listaProd+"\n");
            arquivoRelatorio.close();
            System.out.println("Dados salvos em "+nomeArquivo);
        } catch (IOException e) {
            System.out.println("Problemas para criar o arquivo "+nomeArquivo+". Tente novamente");
        }
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        produtosPorId = lerProdutos(nomeArquivoDados, Produto::hashCode);
        produtosPorNome = new AVL<>(produtosPorId, prod -> prod.descricao, String::compareTo);
        pedidosPorProduto = new TabelaHash<>((int) (quantosProdutos * 1.25));
        gerarPedidos(25000);

        int opcao = -1;

        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> localizarProdutoID();
                case 2 -> recortarArvore(produtosPorNome);
                case 3 -> pedidosDoProduto();
            }
            pausa();
        } while (opcao != 0);

        teclado.close();
    }
}
