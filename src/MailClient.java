import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

        String requestMsg = "no_msg";

        switch (choice) {
            case "LogIn":
                requestMsg = "login_request";
                break;
            case "SignUp":
                requestMsg =  "register_request";
                break;
            case "Exit":
                requestMsg =  "exit_request";
                break;
            case "NewEmail":
                requestMsg =  "new_email_creation_request";
                break;
            case "ShowEmails":
                requestMsg =  "get_emails_preview_request";
                break;
            case "ReadEmail":
                requestMsg =  "get_complete_email_request";
                break;
            case "DeleteEmail":
                requestMsg =  "delete_email_request";
                break;
            case "LogOut":
                requestMsg =  "logout_request";
                break;
        }

        return requestMsg;
    }

    private void run() {

        String recvMsg = "no_msg";
        String reqMsg = "no_request";
        //String serverResponse = "no_response";
        Scanner input = new Scanner(System.in);
        String userChoice;

        try {
            recvMsg = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (recvMsg.equals("connection_successful")) {
            System.out.println("> Connected to MailServer!");
        }

        while (!stopListening) {

            // get the appropriate menu from the server
            try {
                recvMsg = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // print the menu that was sent by the server
            // to the user
            System.out.println(recvMsg);

            // read the user's input choice
            userChoice = input.next();

            // prepare the correct outbound message to the server,
            // according to the user's choice
            reqMsg = createRequestMessage(userChoice);

            // send the formed request message to the server
            try {
                out.writeUTF(reqMsg);
                recvMsg = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // print the message the server sent back
            // as a response
            System.out.println(recvMsg);

            // depending on the user's type of chosen function,
            // print the appropriate received messages,
            // send the appropriate follow up requests and receive
            // the needed responses
            switch (userChoice) {

                case "LogIn":
                case "SignUp":

                    try {
                        // give username
                        System.out.println(in.readUTF());
                        out.writeUTF(input.next());
                        // give password
                        System.out.println(in.readUTF());
                        out.writeUTF(input.next());
                        // get and print result from server
                        System.out.println(in.readUTF());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "NewEmail":

                    try {
                        // print "Receiver: "
                        System.out.println(in.readUTF());
                        // give receiver
                        out.writeUTF(input.next());
                        // print "Subject: "
                        System.out.println(in.readUTF());
                        // give subject
                        out.writeUTF(input.next());
                        // print "Main body: "
                        System.out.println(in.readUTF());
                        // give main body
                        out.writeUTF(input.next());
                        // get and print result from server
                        System.out.println(in.readUTF());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "ShowEmails":

                    try {
                        // get and print result from server
                        System.out.println(in.readUTF());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "ReadEmail":
                case "DeleteEmail":

                    try {
                        // print "Enter the email's ID: "
                        System.out.println(in.readUTF());
                        // give the id
                        out.writeUTF(input.next());
                        // get and print result from server
                        System.out.println(in.readUTF());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }

            if (userChoice.equals("Exit")) {
                stopListening = true;
            }

        }

    }



    public static void main(String[] args) {

        new MailClient("127.0.0.1", 5678);

    }
}
