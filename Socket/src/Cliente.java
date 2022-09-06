import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente extends Thread {

    private Socket conexao;

    public Cliente(Socket s) {
        conexao = s;
    }

    public static void main(String[] args) throws IOException {

        String msg_recebida; // mensagem recebida
        String nome_cliente; // nome do cliente
        String assunto;

        Socket cliente = new Socket("localhost", 8657);

        DataOutputStream saida_servidor = new DataOutputStream(cliente.getOutputStream());
        BufferedReader entrada_servidor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        // solicita um nome e assunto para o cliente
        System.out.println("Informe o nome do cliente: ");
        nome_cliente = teclado.readLine();
        saida_servidor.writeBytes(nome_cliente + "\n");

        msg_recebida = entrada_servidor.readLine();
        System.out.println("Servidor: " + msg_recebida);

        System.out.println("--Informe o assunto desejado-- ");
        System.out.print("1 -Economia \n2 -Entreterimento \n3 -Tecnologia \nNúmero do assunto escolhido:");
        assunto = teclado.readLine();


        saida_servidor.writeBytes(assunto + "\n");

        msg_recebida = entrada_servidor.readLine();
        System.out.println(msg_recebida);

        Thread thread = new Cliente(cliente);
        thread.start();

        while (true) {
            msg_recebida = entrada_servidor.readLine();

            System.out.println(msg_recebida);
        }

    }

    public void run() {
        String msg_digitada;

        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        try {
            DataOutputStream saida_servidor = new DataOutputStream(conexao.getOutputStream()
            );

            while (true) {
                msg_digitada = teclado.readLine();

                if (msg_digitada.startsWith("fim")) {
                    break;
                }

                saida_servidor.writeBytes(msg_digitada + '\n');
            }

            conexao.close();
            System.out.println("Cliente se desconectou!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
