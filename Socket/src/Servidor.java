import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class Servidor extends Thread {

    private Socket conexao;

    private static Vector<DataOutputStream> vet_Saida_Economia = new Vector<DataOutputStream>();
    private static Vector<DataOutputStream> vet_Saida_Entreterimento = new Vector<DataOutputStream>();
    private static Vector<DataOutputStream> vet_Saida_Tecnologia = new Vector<DataOutputStream>();

    public Servidor(Socket s) {
        conexao = s;
    }

    public static void main(String[] args) {
        try {
            // cria socket de comunicação com os clientes na porta 8657
            ServerSocket servidor = new ServerSocket(8657);

            // espera msg de algum cliente e trata
            while (true) {

                // espera conexão de algum cliente
                System.out.println("Esperando cliente se conectar...");

                Socket conexao = servidor.accept();

                // Gerar Thread diferentes para cada cliente
                Thread thread = new Servidor(conexao);

                //Executar o método run
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String data() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTimeFormatter.format(LocalDateTime.now());
    }

    public void run() {

        String msg_recebida; // lida do cliente
        String msg_enviada; // enviada ao cliente
        String nome_cliente;
        String assunto;

        try {
            System.out.println("Cliente conectado!");

            // cria streams de entrada e saida com o cliente que chegou
            BufferedReader entrada_cliente = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            DataOutputStream saida_cliente = new DataOutputStream(conexao.getOutputStream());

            // lê mensagem do cliente
            nome_cliente = entrada_cliente.readLine();
            saida_cliente.writeBytes(nome_cliente + " seja bem-vindo!" + "\n");
            assunto = entrada_cliente.readLine();


            //Vetor generico para tratar os outros vetores
            Vector<DataOutputStream> clientes = null;


            switch (assunto) {

                case "1":
                    vet_Saida_Economia.add(saida_cliente);
                    clientes = vet_Saida_Economia;
                    assunto = "Economia";
                    saida_cliente.writeBytes("Você entrou no chat de assunto " + assunto + "!");
                    break;

                case "2":
                    vet_Saida_Entreterimento.add(saida_cliente);
                    clientes = vet_Saida_Entreterimento;
                    assunto = "Entreterimento";
                    saida_cliente.writeBytes("Você entrou no chat de assunto " + assunto + "!");
                    break;

                case "3":
                    vet_Saida_Tecnologia.add(saida_cliente);
                    clientes = vet_Saida_Tecnologia;
                    assunto = "Tecnologia";
                    saida_cliente.writeBytes("Você entrou no chat de assunto " + assunto + "!");
                    break;

            }

            for (int i = 0; i < clientes.size(); i++) {
                clientes.get(i).writeBytes("\nServidor: " + nome_cliente + " entrou no chat de " + assunto
                        + " no horário de: " + data() + "\n");
            }

            msg_recebida = entrada_cliente.readLine();

            while (msg_recebida != null && !(msg_recebida.trim().equals(""))
                    && !(msg_recebida.startsWith("fim"))) {

                // mostra mensagem recebida na console
                System.out.println(nome_cliente + ": " + msg_recebida);

                // monta retorno para o cliente
                msg_enviada = assunto + ": " + msg_recebida + " (" + data() + ")" + '\n';

                for (int i = 0; i < clientes.size(); i++) {
                    if (clientes.get(i) != saida_cliente) {
                        clientes.get(i).writeBytes(msg_enviada);
                    }
                }

                // lê mensagem do cliente
                msg_recebida = entrada_cliente.readLine();
            }

            System.out.println(nome_cliente + " desconectado!");
            conexao.close();

        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}
