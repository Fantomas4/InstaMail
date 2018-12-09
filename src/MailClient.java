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
                requestMsg =  "NEW_EMAIL_CREATION_REQUEST";
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

//        String recvMsg = "no_msg";
//        String reqMsg = "no_request";
//        String serverResponse = "no_response";
        Scanner input = new Scanner(System.in);
        String userChoice;

//        recvMsg = receiveCompleteMsg();

        String receivedMsg = "NO_MESSAGE";

//        // get connection status response from server
//        try {
//            receivedMsg = in.readUTF();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (receivedMsg.equals("CONNECTION_SUCCESSFUL")) {
//            System.out.println("> Connected to MailServer!");
//            try {
//                out.writeUTF("GET_MENU_REQUEST");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        try {

            while (!stopListening) {
                System.out.println("Client point 1");
                // the first readUTF() gets the CONTEXT of the request
                // example: LOGIN_AUTH
                // Based on the context of the request, there are
                // follow up readUTF() commands that get the CONTENT of
                // the request.
                receivedMsg = in.readUTF();
                System.out.println("Client point 2");
                System.out.println("DIAG: receivedMsg: " + receivedMsg);
//                // test DELETE *****
//                receivedMsg = in.readUTF();
//                System.out.println("DIAG: receivedMsg: " + receivedMsg);
//                // test DELETE *****

                if (receivedMsg.equals("END_OF_REQUEST_HANDLING") || receivedMsg.equals("CONNECTION_SUCCESSFUL")) {
                    out.writeUTF("GET_MENU_REQUEST");

                    // print the menu the server just sent
                    System.out.print(in.readUTF());

                    // read the user's menu choice
                    userChoice = input.next();

                    // send the appropriate request according to the user's menu choice
                    out.writeUTF(createRequestMessage(userChoice));

                } else if (receivedMsg.equals("LOGIN_AUTH")) {
                    receivedMsg = in.readUTF();
                    System.out.println(receivedMsg);
                    if (receivedMsg.equals("Type your username:")) {
//                    out.writeUTF("fantom");
                        out.writeUTF(input.next());
                        out.flush();
                    } else if (receivedMsg.equals("Type your password:")) {
//                    out.writeUTF("mypass");
                        out.writeUTF(input.next());
                        out.flush();
                    }
                }




//                // print the menu the server just sent
//                System.out.print(in.readUTF());

//                // read the user's menu choice
//                userChoice = input.next();

//                // send the appropriate request according to the user's menu choice
//                out.writeUTF(createRequestMessage(userChoice));










//                if (receivedMsg.equals("CONNECTION_SUCCESSFUL")) {
//                    System.out.println("> Connected to MailServer!");
//                    out.writeUTF("GET_MENU_REQUEST");
//                    out.flush();
//                    System.out.println("Client point 3");
//                    System.out.print(in.readUTF());
//                    userChoice = input.next();
//                    out.writeUTF(createRequestMessage(userChoice));
//                    out.flush();
//                }




            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public static void main(String[] args) {

        new MailClient("127.0.0.1", 5678);

    }
}
