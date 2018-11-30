import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;


    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {

    }

}
