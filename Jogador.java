/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JogoDaForca;

import java.io.*; 
import java.net.*; 
import java.util.Scanner;

public class Jogador{ 
    public static void main(String argv[]) throws Exception{ 
        String ip;
        String palpite;
        String respostaS;
        
        System.out.println("Bem Vindo Jogador, informe o endereço IP do servidor que deseja conectar (HostLocal = 127.0.0.1): ");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        ip = inFromUser.readLine();
        System.out.println("Aguarde outros jogadores se conectar ao servidor...");
        
        //Conexão TCP com Coordenador(Servidor) na porta 8888.
        try (Socket clientSocket = new Socket(ip, 8888)) {
            PrintStream saida = new PrintStream(clientSocket.getOutputStream());
            Scanner entrada = new Scanner(clientSocket.getInputStream());
            
            //Corpo do Jogador(Cliente)
            while(true){
                if(entrada.hasNextLine()){
                    respostaS = entrada.nextLine();
                    //Casos em que o Jogador espera as entradas do Cliente.
                    switch (respostaS) {
                        //"Espera", situação do jogador aguardando o jogador atual jogar.
                        case "espera":
                            break;
                        //"Sua vez", jogador joga.
                        case "sua vez":
                            System.out.println("Opções: 0-Desistir, 1-Passar a vez, 'chutar' a letra ou palpite direto(2 tentativas).");
                            System.out.print("Sua vez jogador, você tem 20 segundos: ");
                            palpite = inFromUser.readLine();
                            saida.print(palpite + "\n");
                            break;
                        //Vitooooooria!!!!
                        case "vitoria":
                            System.out.println("Parabens!! Você acertou!\nFim de Jogo.");
                            System.exit(0);
                            break;
                        //Derrota :/
                        case "derrota":
                            System.out.println("Que pena, não foi dessa vez. :/\nFim de Jogo.");
                            System.exit(0);
                            break;
                        //Aqui o jogador recebe todo tipo de entrada do Servidor.
                        default:
                            System.out.println(respostaS);
                            break;
                    }
                }
                
            }
        }
    }	    
}
