import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MailClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean stopListening;

    public MailClient(String ipAddr, int port) {

        try {
            socket = new Socket(ipAddr, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            stopListening = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        run();

    }

    // gets the user input choice and returns the appropriate
    // request message form
    private String createRequestMessage(String choice) {

        String requestMsg = "NO_MESSAGE";

        switch (choice) {
            case "LogIn":
                requestMsg = "LOGIN_REQUEST";
                break;
            case "SignUp":
                requestMsg =  "REGISTER_REQUEST";
                break;
            case "Exit":
                requestMsg =  "EXIT_REQUEST";
                break;
            case "NewEmail":
                requestMsg =  "COMPOSE_NEW_EMAIL_REQUEST";
                break;
            case "ShowEmails":
                requestMsg =  "GET_EMAILS_PREVIEW_REQUEST";
                break;
            case "ReadEmail":
                requestMsg =  "GET_COMPLETE_EMAIL_REQUEST";
                break;
            case "DeleteEmail":
                requestMsg =  "DELETE_EMAIL_REQUEST";
                break;
            case "LogOut":
                requestMsg =  "LOGOUT_REQUEST";
                break;
        }

        return requestMsg;
    }

    public String receiveCompleteMsg() {

        String temp;
        String finalMsg = "";

        try {
            while (true) {

                temp = in.readUTF();
                System.out.println("temp: " + temp);

                if (temp == null) {
                    System.out.println("Mpika break!");
                    break;
                } else {
                    finalMsg += temp;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(finalMsg);

        return finalMsg;

    }

    private void run() {

        Scanner input = new Scanner(System.in);
        String userChoice;

//        recvMsg = receiveCompleteMsg();

        String receivedMsg = "NO_MESSAGE";

        try {

            while (!stopListening) {
                // the first readUTF() gets the CONTEXT of the request
                // example: LOGIN_AUTH
                // Based on the context of the request, there are
                // follow up readUTF() commands that get the CONTENT of
                // the request.
                receivedMsg = in.readUTF();
                System.out.println("DIAG: receivedMsg: " + receivedMsg);
//                // test DELETE *****
//                receivedMsg = in.readUTF();
//                System.out.println("DIAG: receivedMsg: " + receivedMsg);
//                // test DELETE *****

                if (receivedMsg.equals("END_OF_REQUEST_HANDLING") || receivedMsg.equals("CONNECTION_SUCCESSFUL")) {
                    out.writeUTF("GET_MENU_REQUEST");

                    // print the menu the server just sent
                    System.out.print(in.readUTF() + "\n");

                    // read the user's menu choice
                    userChoice = input.next();

                    // send the appropriate request according to the user's menu choice
                    out.writeUTF(createRequestMessage(userChoice));

                } else if (receivedMsg.equals("LOGIN_AUTH")) {
                    receivedMsg = in.readUTF();
                    if (receivedMsg.equals("Type your username:")) {
//                    out.writeUTF("fantom");
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else if (receivedMsg.equals("Type your password:")) {
//                    out.writeUTF("mypass");
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else {
                        // the receivedMsg contains the authentication result report
                        System.out.println(receivedMsg);
                    }
                } else if (receivedMsg.equals("REGISTER_INFO")) {
                    receivedMsg = in.readUTF();
                    if (receivedMsg.equals("Enter a username:")) {
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else if (receivedMsg.equals("Enter a password:")) {
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else {
                        // the receivedMsg contains the authentication result report
                        System.out.println(receivedMsg);
                    }

                } else if (receivedMsg.equals("EMAIL_COMPOSITION")) {
                    receivedMsg = in.readUTF();
                    if (receivedMsg.equals("Receiver:") || receivedMsg.equals("Subject:") || receivedMsg.equals("Main body:")) {
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else {
                        // the receivedMsg contains the email composition result report
                        System.out.println(receivedMsg);
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public static void main(String[] args) {

        new MailClient("127.0.0.1", 5678);

    }
}
