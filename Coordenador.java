/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JogoDaForca;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Scanner;

public class Coordenador {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket servidor = new ServerSocket(8888);                    //Porta TCP Socket
        ArrayList <Servidor> list = new ArrayList<>();                      //Lista com todos os Socket's
        ArrayList <Thread> listT = new ArrayList<>();                       //Lista das thread
        Scanner teclado = new Scanner(System.in);                           //entrada do teclado
        Scanner sc;                                                         //entrada de dados dos jogador atribuida na linha 100
        Servidor player;                                                    //Jogador ativo no momento
        String palavra;                                                     //palavra chave
        String palavraA;                                                    //palavra chave alterada
        char [] palavraC;                                                   //palavra chave codificada
        String dica;                                                        //dica da palavra chave
        String entradaP;                                                    //entrada da respota do jogador
        String letras = "As letras digitadas foram: ";                      //Armazena as letras digitadas
        Servidor s;                                                         //Variavel Servidor usada para colcoar alguem na list
        int rodadas = 0;                                                    //Numero de rodadas
        boolean flag=true;                                                  //Controle do loop do Lobby
        boolean acertou = false;                                            //flag de controle de acerto
        boolean ganhou = false;                                             //flag de controle caso o player ganhei
        long time1;                                                         //Variaveis para calculo do tempo
        long time2;                                                         //Variaveis para calculo do tempo
        
        //Inicia o servidor
        System.out.println("Servidor iniciado na porta "+ servidor.getLocalPort() + " \nNão há nenhum cliente conectado." );
        while(flag) {
            System.out.println("\n**********************************************************************************\n");
            //aceita um cliente
            Socket cliente = servidor.accept();
            System.out.println(cliente.getInetAddress().getHostAddress());
            PrintStream ps = new PrintStream(cliente.getOutputStream());
            s = new Servidor(cliente,list.size()+1);
            list.add(s);
            Thread t1 = new Thread(s);
            listT.add(t1);
            System.out.println("Atualmente temos: " + listT.size() + " Jogadores\nDigite Y para mais e N para começar o jogo.");
            
            //While que roda até atingir o minimo de 3 jogadores.
            while (teclado.hasNextLine()) {
                if(!teclado.nextLine().equals("Y") && list.size() > 2){
                    flag = false;
                }
                else if(list.size() <= 2){
                    System.out.println("Ainda não atingiu a quantidade minima de jogadores que são 3.\nAguardando novos jogadores.");
                }
                break;
            }
        }
        //O Jogo começa, servidor escolhe uma palavra chave e uma dica.
        distribuiMensagem(list,"O Jogo Irá Começar!\nAguardando a palavra chave do servidor");
        System.out.println("Informe a palavra chave:");
        palavra = teclado.nextLine();
        palavraA = palavra;
        System.out.println("Informe a dica:");
        dica = teclado.nextLine();
        palavraC = palavra.trim().toCharArray();
        
        //Preenche a String chave codificada
        for(int i=0; i < palavraC.length;i++){
           palavraC[i]='*';
        }
        
        //Remove o primeiro elemento da lista de jogadores.
        player = list.remove(0);
        OUTER:
        
        //Corpo do programa, while roda até alguem ganhar
        while (true) {
            //Condição em que é sugerido ao servidor nova dica.
            if(rodadas%listT.size() == 0 && rodadas != 0){
                rodadas++;
                distribuiMensagem(list,"Aguarde o servidor se deseja escolher uma nova dica...");
                enviarMensagem(player,"Aguarde o servidor se deseja escolher uma nova dica...");
                System.out.println("Deseja informar mais uma dica?\nY - Sim e N - Não");
                if(teclado.hasNextLine()){
                    if(teclado.nextLine().equals("Y")){
                        System.out.println("Informe mais uma dica");
                        dica = dica + " - " + teclado.nextLine();
                    }
                }
            }
            
            distribuiMensagem(list, "\n**********************************************************************************\n");
            enviarMensagem(player,"\n**********************************************************************************\n");
            System.out.println("\n**********************************************************************************\n");
            sc = new Scanner(player.getConexao().getInputStream());
            distribuiMensagem(list, "A palavra chave é: " + String.valueOf(palavraC) + "\nA Dica é: " + dica);
            enviarMensagem(player,"A palavra chave é: " + String.valueOf(palavraC) + "\nA Dica é: " + dica);
            distribuiMensagem(list, letras);
            enviarMensagem(player, letras);
            acertou = false;
            distribuiMensagem(list,"O Jogador " + player.getPlayer() + " está jogando!\n Aguarde...");
            distribuiMensagem(list,"espera");
            enviarMensagem(player, "sua vez");
            
            //Criando condição em que o jogador só tem 20s para responder.
            time1 = Clock.systemUTC().millis();
            if (sc.hasNextLine()) {
                time2 = Clock.systemUTC().millis();
                if (time2 - time1 > 20000) {
                    enviarMensagem(player, "Você ultrapassou o limite de tempo permitido, resposta não registada!");
                    distribuiMensagem(list,"O jogador " + player.getPlayer() + " esgotou seu tempo!\n");
                    System.out.println("O jogador " + player.getPlayer() + " esgotou o tempo!\n");
                    acertou = false;
                } 
                else {
                    entradaP = sc.nextLine();
                    //Se a opção que o jogador escolheu foi um caractere.
                    if (entradaP.length()== 1) {
                        switch (entradaP) {
                            case "1":
                                //Caso 1-Passa a vez. 
                                enviarMensagem(player, "Você passou a vez");
                                distribuiMensagem(list, "O jogador " + player.getPlayer() + " passou a vez!");
                                System.out.println("O jogador " + player.getPlayer() + " passou a vez!\n");
                                acertou = false;
                                list.add(player);
                                player = list.remove(0);
                                rodadas++;
                                continue;
                            case "0":
                                //Caso 0-Desiste.
                                enviarMensagem(player, "Você desistiu, a palavra é: " + palavra +"\n");
                                enviarMensagem(player, "derrota");
                                distribuiMensagem(list, "O jogardor " + player.getPlayer() + " desitiu!" );
                                System.out.println("O jogador " + player.getPlayer() + " desistiu!\n");
                                player = list.remove(0);
                                rodadas++;
                                continue;
                            default:
                                //Caso seja letra.
                                letras = letras + entradaP + "-";
                                if (palavraA.contains(entradaP)) {
                                    //Acertou a letra.
                                    System.out.println("O jogador " + player.getPlayer() + " acertou uma letra!");
                                    //Verifica se tem a letra na palavra e substitue o *.
                                    while(palavraA.contains(entradaP)){
                                        palavraC[palavraA.indexOf(entradaP)] = entradaP.charAt(0);
                                        palavraA = palavraA.replaceFirst(entradaP, "*");
                                    }   
                                    acertou = true;
                                    
                                    //verificar se completou a palavra.
                                    for(int i=0;i<palavraC.length;i++){                         
                                        if(palavraC[i]=='*'){
                                            ganhou = false;
                                            break;
                                        }
                                        ganhou = true;
                                    }   
                                    //Vitoria por ter completado a palavra.
                                    if (ganhou){
                                        enviarMensagem(player,"vitoria");
                                        distribuiMensagem(list,"O jogador " + player.getPlayer() + " ganhou!\nderrota");
                                        System.out.println("O jogador " + player.getPlayer() + " ganhou!\n");
                                        break OUTER;
                                    } 
                                    else {
                                        enviarMensagem(player, "Você acertou uma letra!");
                                    }
                                } 
                                //O Jogador errou a letra.
                                else {
                                    enviarMensagem(player, "Errou a letra!");
                                    distribuiMensagem(list,"O jogador " + player.getPlayer() + " errou a letra!");
                                    System.out.println("O jogador " + player.getPlayer() + " errou uma letra!\n");
                                    acertou = false;
                                }
                                break;
                        }
                    }
                    //Vitoria por um palpite direto.
                    else {
                        if(palavra.equals(entradaP)){                                                          
                            enviarMensagem(player, "vitoria");
                            distribuiMensagem(list,"O jogador " + player.getPlayer() + " ganhou!\nderrota");
                            System.out.println("O jogador " + player.getPlayer() + " ganhou!\n");
                            break;
                        }
                        //Derrota por um palpite direto.
                        else{                                                               
                            acertou = false;
                            //Decrementa tentativas do palpite direto.
                            if(player.getTentativas() >1){
                                player.setTentativas(player.getTentativas()-1);
                                enviarMensagem(player, "Você errou!\nRestam " + player.getTentativas() + " tentativas!");
                                System.out.println("O jogador " + player.getPlayer() + " perdeu!");
                            }
                            //Esgotou as duas tentativas do palpite direto.
                            else{
                                enviarMensagem(player, "Você perdeu, a palavra é: " + palavra + "\nderrota");
                                System.out.println("O jogador " + player.getPlayer() + " perdeu!\n");
                                distribuiMensagem(list, "O jogardor " + player.getPlayer() + " perdeu!" );
                                player = list.remove(0);
                                continue;
                            }
                        }
                    }
                }
            }
            //O jogador não achertou, aqui realiza a troca para o proximo jogador.
            if(!acertou){
                rodadas++;
                list.add(player);
                player = list.remove(0);
            }
        }       
    }
    //Função onde tem a saida do Coordenador para a lista de Jogadores que estão esperando.
    public static void distribuiMensagem(ArrayList <Servidor> list, String msg) throws IOException{
        PrintStream pi;
        for (Servidor list1 : list) {
            pi = new PrintStream(list1.getConexao().getOutputStream());
            pi.println(msg);
            pi.flush();
        }
    }
    //Função onde tem a saida do Coordenador para o Jogador que está jogando no momento.
    public static void enviarMensagem(Servidor s, String msg) throws IOException{
        PrintStream pi;
        pi = new PrintStream(s.getConexao().getOutputStream());
        pi.println(msg);
    }
    
}
