/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JogoDaForca;

import java.net.Socket;

public class Servidor implements Runnable{
    private Socket conexao;             //Conexão Socket.
    private int player;                 //O Jogador que joga no momento.
    private int tentativas = 2;         //Tentativas de cada jogador do palpite direto.
    
    //Construtor do Servidor.
    public Servidor(Socket conexao, int player) {
        this.conexao = conexao;
        this.player = player;
    }

    public int getPlayer() {
        return player;
    }

    public int getTentativas() {
        return tentativas;
    }

    public void setTentativas(int tentativas) {
        this.tentativas = tentativas;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public Socket getConexao() {
        return conexao;
    }

    public void setConexao(Socket conexao) {
        this.conexao = conexao;
    }
    
    //Como o Servidor é uma interface de thread necessitando da run, por isso é obrigado colocar run.
    //Não implementamos ela pois no nosso caso não é necessário.
    @Override
    public void run() {
        
    }
    
}
