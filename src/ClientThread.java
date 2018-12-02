import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private MailServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private Account loggedInUser;
    private JSONParser parser;


    public ClientThread(Socket socket, MailServer server) {

        this.socket = socket;
        this.server = server;
        loggedInUser = null;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        run();

    }

    private void messageAnalysis(JSONObject obj) {

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

            String receivedMsg = "";
//
            try {
                receivedMsg = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            switch (receivedMsg) {

                case "login_request":
                    try {
                        out.writeUTF("Type your username: ");
                        String recvUsername = in.readUTF();
                        out.writeUTF("Type your password: ");
                        String recvPassword = in.readUTF();

                        String result = server.login(recvUsername, recvPassword);

                        switch (result) {
                            case "verification_success":
                                loggedInUser = server.getUserAccount(recvUsername);
                                out.writeUTF("Welcome back " + recvUsername + "!");
                                break;
                            case "username_not_found":
                                out.writeUTF("Error: Username does not exist!");
                                break;
                            case "invalid_password":
                                out.writeUTF("Error: Wrong password!");
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


//
//                case "new_email_creation_request":
//                    server.newEmail();
//                    break;
//
//                case "get_emails_preview_request":
//                    server.getEmailsPreview();
//                    break;

//                case "get_complete_email_request":
//                    server.getEmail();
//                    break;
            }

            // send the appropriate menu an the end of the
            // current request handling
            String menuOptions;

            // send the menu options to the client after completing his request
            if (loggedInUser != null) {
                // the thread handles requests from a logged in user
                menuOptions = "===============\n> NewEmail\n> ShowEmails\n> ReadEmail\n> DeleteEmail\n> LogOut\n> Exit\n===============";
            } else {
                // the thread handles requests from a guest user
                menuOptions = "==========\n> LogIn\n> Register\n> Exit";
            }

            try {
                out.writeUTF(menuOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                Object obj = parser.parse(receivedMsg);
////                JSONArray jsonMsg = (JSONArray)obj;
//                JSONObject jsonObj = (JSONObject)obj;
//
//                String msgType = (String)jsonObj.get("type");
//                String msgContext = (String)jsonObj.get("context");
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

        }
    }

}
