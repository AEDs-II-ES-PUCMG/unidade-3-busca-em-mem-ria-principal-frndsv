import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class TestePreAtividade {
    public static void main(String[] args) {
        int quantidadeN = 10000;
        int quantidadeM = 1000;

        int[] listaN = new int[quantidadeN];
        int[] listaM = new int[quantidadeM];
        Random sorteio = new Random(42);
        ABB<Integer, Integer> abb = new ABB<>();
        AVL<Integer, Integer> avl = new AVL<>();

        // Inserindo no array inteiro os valores com a semente de N numeros 
        for (int i = 0; i < quantidadeN; i++) {
            listaN[i] = sorteio.nextInt();
        }

        // Inserindo os valores da semente de N numeros na ABB e na AVL
        for (int i = 0; i < listaN.length; i++) {
            abb.inserir(listaN[i], listaN[i]);
            avl.inserir(listaN[i], listaN[i]);
        }

        // Inserindo no array inteiro os valores com a semente de M numeros
        for (int i = 0; i < quantidadeM; i++) {
            listaM[i] = sorteio.nextInt();
        }
        
        Long comparacoesABBDesordenado = null;
        Long comparacoesAVLDesordenado = null;
        Double tempoABBDesordenado = 0.0;
        Double tempoAVLDesordenado = 0.0;

        for(int i = 0; i < quantidadeM; i++) {
            try {
                abb.pesquisar(listaM[i]);
                avl.pesquisar(listaN[i]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
            comparacoesABBDesordenado += abb.getComparacoes();
            comparacoesAVLDesordenado += avl.getComparacoes();
            tempoABBDesordenado += abb.getTempo();
            tempoAVLDesordenado += avl.getTempo();
        }

        // Ordenando
        for (int i = 1; i < listaN.length; i++) {
            int tmp = listaN[i];
            int j = i - 1;

            while ((j >= 0) && (listaN[j] > tmp)) {
                listaN[j + 1] = listaN[j];
                j--;
            }
            listaN[j + 1] = tmp;
      	}
    
        ABB<Integer, Integer> abbOrdem = new ABB<>();
        AVL<Integer, Integer> avlOrdem = new AVL<>();        

        // Inserindo os valores da semente de N (agora ordenada) numeros na ABB e na AVL
        for (int i = 0; i < listaN.length; i++) {
            abbOrdem.inserir(listaN[i], listaN[i]);
            avlOrdem.inserir(listaN[i], listaN[i]);
        }

        Long comparacoesABBOrdenado = null;
        Long comparacoesAVLOrdenado = null;
        Double tempoABBOrdenado = 0.0;
        Double tempoAVLOrdenado = 0.0;

        for(int i = 0; i < quantidadeM; i++) {
            try {
                abbOrdem.pesquisar(listaM[i]);
                avlOrdem.pesquisar(listaN[i]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
           
            comparacoesABBOrdenado += abbOrdem.getComparacoes();
            comparacoesAVLOrdenado += avlOrdem.getComparacoes();
            tempoABBOrdenado += abbOrdem.getTempo();
            tempoAVLOrdenado += avlOrdem.getTempo();
        }

        System.out.println("Comparacoes ABB com array DESORDENADO " + comparacoesABBDesordenado);
        System.out.println("Comparacoes AVL com array DESORDENADO " + comparacoesAVLDesordenado);
        System.out.println("Tempo ABB com array DESORDENADO " + tempoABBDesordenado);
        System.out.println("Tempo AVL com array DESORDENADO " + tempoAVLDesordenado);

        System.out.println("Comparacoes ABB com array ORDENADO " + comparacoesABBOrdenado);
        System.out.println("Comparacoes AVL com array ORDENADO " + comparacoesAVLOrdenado);
        System.out.println("Tempo ABB com array ORDENADO " + tempoABBOrdenado);
        System.out.println("Tempo AVL com array ORDENADO " + tempoAVLOrdenado);

        





    }
}
