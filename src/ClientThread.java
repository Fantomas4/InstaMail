import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private MailServer server;
    private DataInputStream in;
    private DataOutputStream out;


    public ClientThread(Socket socket, MailServer server) {

        this.socket = socket;
        this.server = server;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        run();

    }

    public void run() {

        boolean stopListening = false;

        // send a message to the client
        // in order to inform him that his
        // connection request
        // was accepted successfully
        try {
            out.writeUTF("connection_successful");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!stopListening) {
            try {
                String receivedMsg = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

}
