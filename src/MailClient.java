import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MailClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public MailClient(int port) {

        try {
            socket = new Socket("127.0.0.1", port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }



    public static void main(String[] args) {

        new MailClient(5678);

    }
}
