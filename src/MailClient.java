import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MailClient {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
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

    private void run() {

        Scanner input = new Scanner(System.in);
        String userChoice;

        String receivedMsg = "NO_MESSAGE";

        try {

            while (!stopListening) {

                System.out.print("-----------\nMailServer:\n-----------\n");

                // the first readUTF() gets the CONTEXT of the request
                // example: LOGIN_AUTH
                // Based on the context of the request, there are
                // follow up readUTF() commands that get the CONTENT of
                // the request.
                receivedMsg = in.readUTF();
//                System.out.println("DIAG: receivedMsg: " + receivedMsg);

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
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else if (receivedMsg.equals("Type your password:")) {
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

                } else if (receivedMsg.equals("EMAILS_PREVIEW")) {
                    System.out.print(in.readUTF() + "\n");

                } else if (receivedMsg.equals("COMPLETE_EMAIL_CONTENT") || receivedMsg.equals("EMAIL_DELETION")) {
                    receivedMsg = in.readUTF();
                    if (receivedMsg.equals("Enter the email's ID:")) {
                        System.out.println(receivedMsg);
                        out.writeUTF(input.next());
                        out.flush();
                    } else {
                        // the receivedMsg contains the complete email content
                        // or the delete message action's result status
                        System.out.println(receivedMsg);
                    }
                } else if (receivedMsg.equals("TERMINATE_CONNECTION")) {
                    // the server acknowledges the client's exit request and
                    // asks the client to gracefully terminate its connection,
                    // while the server does the same.
                    stopListening = true;
                }

            }

        } catch (IOException e) {
            if (e instanceof EOFException) {
                // the server has unexpectedly closed the connection
                System.out.println("*** Error: The server has unexpectedly closed the connection! ***");
            } else {
                e.printStackTrace();
            }
        } finally {
            // release the system's resources and clean up
            // before exiting

            try {
                System.out.println("Client cleaning up...");
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



    public static void main(String[] args) {

        new MailClient("127.0.0.1", 5678);

    }
}
