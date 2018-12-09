import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailServer {

    private int handshakePort;
    private ArrayList<Account> accountList;
    private ServerSocket serverSocket;
    private ArrayList<Thread> requestServiceThreads;

    public MailServer(int port) {
        System.out.println("DIAG: eftasa0");
        handshakePort = port;
        accountList = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(handshakePort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        handshakeListeningThread();

    }

    private void handshakeListeningThread() {

        while (true) {
            try {
                // accept an incoming handshake from a new client,
                // and create a new socket for this particular client
                Socket serviceSocket = serverSocket.accept();

                // create a new RequestServiceThread object, pass it to a new serviceThread,
                // start the serviceThread and then add
                // it to the clientThreads list
                Thread serviceThread = new Thread(new RequestServiceThread(serviceSocket));
                serviceThread.start();
                requestServiceThreads.add(serviceThread);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private class RequestServiceThread implements Runnable {

        private Socket reqSocket;
        private DataInputStream in;
        private DataOutputStream out;
        private Account loggedInUser;
        boolean stopListening;

        private RequestServiceThread(Socket socket) {

            reqSocket = socket;
            loggedInUser = null;

            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            stopListening = false;

            run();
        }

        public void run() {

            // send a message to the client
            // in order to inform him that his
            // connection request
            // was accepted successfully

            try {
                out.writeUTF("CONNECTION_SUCCESSFUL");

//                // TEST delete ****
//                out.writeUTF("SOMETHING_ELSE");
//                // TEST delete ****

                String receivedMsg = "NO_MESSAGE";

                while (!stopListening) {

                    // Receive message from client
                    try {
                        System.out.println("point 1");
                        receivedMsg = in.readUTF();
                        System.out.println("point 2");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    switch (receivedMsg) {

                        case "GET_MENU_REQUEST":
                            // send the appropriate menu to the client
                            String menuOptions;
                            // send the menu options to the client after completing his request
                            if (loggedInUser != null) {
                                // the thread handles requests from a logged in user
                                out.writeUTF("===============\n> NewEmail\n> ShowEmails\n> ReadEmail\n> DeleteEmail\n> LogOut\n> Exit\n===============");
                            } else {
                                // the thread handles requests from a guest user
                                out.writeUTF("==========\n> LogIn\n> Register\n> Exit\n==========");
                            }
                            break;
                        case "LOGIN_REQUEST":
                            try {
                                System.out.println("DIAG: eftasa1");
                                out.writeUTF("LOGIN_AUTH");
//                                out.flush();
                                out.writeUTF("Type your username:");
//                                out.flush();
                                String recvUsername = in.readUTF();
                                System.out.println("DIAG: eftasa2");
                                out.writeUTF("LOGIN_AUTH");
//                                out.flush();
                                out.writeUTF("Type your password:");
//                                out.flush();
                                System.out.println("DIAG: eftasa3");
                                String recvPassword = in.readUTF();

                                String result = login(recvUsername, recvPassword);
                                System.out.println("DIAG: username: " + recvUsername + " password: " + recvPassword);
                                System.out.println("DIAG: result: " + result);

                                out.writeUTF("LOGIN_AUTH");
                                switch (result) {
                                    case "VERIFICATION_SUCCESS":
                                        loggedInUser = getUserAccount(recvUsername);
                                        out.writeUTF("Welcome back " + recvUsername + "!");
                                        break;
                                    case "USERNAME_NOT_FOUND":
                                        out.writeUTF("Error: Username does not exist!");
                                        break;
                                    case "INVALID_PASSWORD":
                                        out.writeUTF("Error: Wrong password!");
                                        break;
                                }

                                // the server notifies the client that it has finished handling
                                // the current client's request.
                                out.writeUTF("END_OF_REQUEST_HANDLING");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "REGISTER_REQUEST":
                            String username = "NO_USERNAME";
                            String password = "NO_PASSWORD";

                            try {
                                out.writeUTF("Enter a username: ");
                                username = in.readUTF();
                                out.writeUTF("Enter a password: ");
                                password = in.readUTF();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (register(username, password).equals("ACCOUNT_CREATION_SUCCESS")) {
                                out.writeUTF("Account created successfully!");
                            } else if (register(username, password).equals("ERROR_USERNAME_ALREADY_EXISTS")) {
                                out.writeUTF("Error! Username already exists!");
                            }


                            break;

                        case "NEW_EMAIL_CREATION_REQUEST":

                            String receiver = "NO_USER";
                            String subject = "NO_SUBJECT";
                            String mainBody = "NO_MAIN_BODY";

                            try {
                                out.writeUTF("Receiver: ");
                                receiver = in.readUTF();
                                out.writeUTF("Subject: ");
                                subject = in.readUTF();
                                out.writeUTF("Main body: ");
                                mainBody = in.readUTF();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String result = newEmail(loggedInUser.getUsername(), receiver, subject, mainBody);

                            if (result.equals("EMAIL_SEND_SUCCESS")) {
                                out.writeUTF("Mail sent successfully!");
                            } else if (result.equals("ERROR_INVALID_RECEIVER")) {
                                out.writeUTF("Error! Invalid receiver!");
                            }

                            break;

                        case "GET_EMAILS_PREVIEW_REQUEST":
                            ArrayList<String> previewEntries = getEmailsPreview(loggedInUser);
                            StringBuilder sb = new StringBuilder();

                            // convert the ArrayList<String> to a String containing
                            // the preview's entries
                            for (String entry : previewEntries) {
                                sb.append(entry);
                                sb.append("\n");
                            }

                            out.writeUTF(sb.toString());

                            break;

                        case "GET_COMPLETE_EMAIL_REQUEST":

                            String emailId = "NO_ID";

                            try {
                                out.writeUTF("Enter the email's ID: ");
                                emailId = in.readUTF();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String emailResult = getEmail(emailId, loggedInUser);

                            if (emailResult.equals("ERROR_INVALID_EMAIL_ID")) {
                                out.writeUTF("Error! Email ID not found!");
                            } else {
                                out.writeUTF(emailResult);
                            }

                            break;

                        case "DELETE_EMAIL_REQUEST":

                            String deleteId = "-1";

                            try {
                                out.writeUTF("Enter the email's ID: ");
                                deleteId = in.readUTF();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String deleteResult = deleteEmail(deleteId, loggedInUser);

                            if (deleteResult.equals("EMAIL_DELETION_SUCCESS")) {
                                out.writeUTF("Email deleted successfully!");
                            } else if (deleteResult.equals("ERROR_INVALID_EMAIL_ID")) {
                                out.writeUTF("Error! Invalid email id!");
                            }

                            break;

                        case "LOGOUT_REQUEST":
                            logOut();

                            break;

                        case "EXIT_REQUEST":
                            exit();

                            break;

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Account getUserAccount(String username) {

            Account target = null;

            for (Account account : accountList) {
                if (account.getUsername().equals(username)) {
                    target = account;
                }
            }

            return target;
        }

        private String register(String username, String password) {

            // Check whether the username already exists
            for (Account account : accountList) {
                if (account.getUsername().equals(username)) {
                    // The given username is already taken (already exists)
                    return "ERROR_USERNAME_ALREADY_EXISTS";
                }
            }

            // The given username is accepted, so a new
            // account is created using the given username and password.
            // The new account object is then added to the accountList array list.
            accountList.add(new Account(username, password));

            return "ACCOUNT_CREATION_SUCCESS";
        }

        private String login(String givenUsername, String givenPassword) {

            // Check whether an account with the given username exists
            Account matchingAccount = null;

            for (Account account : accountList) {
                if (account.getUsername().equals(givenUsername)) {

                    // An account with the target username exists
                    matchingAccount = account;
                }
            }

            if (matchingAccount != null) {

                // Check whether the given password is correct
                boolean validPassword = matchingAccount.isCorrectPassword(givenPassword);

                if (validPassword) {
                    // The given password is valid
                    loggedInUser = matchingAccount;
                    return "VERIFICATION_SUCCESS";
                } else {
                    // The given password is invalid
                    return "INVALID_PASSWORD";
                }
            } else {
                // matchingAccount is null, meaning no account exists with the given username
                return "USERNAME_NOT_FOUND";
            }

        }

        private String newEmail(String senderUsername, String receiverUsername, String subject, String mainBody) {

            Account target = null;

            // check whether the receiver exists as a valid user
            for (Account account : accountList) {
                if (account.getUsername().equals(receiverUsername)) {
                    target = account;
                    break;
                }
            }

            if (target != null) {
                // receiver is valid (username exists)
                target.addNewEmail(new Email(senderUsername, receiverUsername, subject, mainBody));
                return "EMAIL_SEND_SUCCESS";
            } else {
                // receiver is not valid (a user with the given username does not exist)
                return "ERROR_INVALID_RECEIVER";
            }

        }

        private ArrayList<String> getEmailsPreview(Account user) {
            // replaces the showEmails function from the exercise description
            ArrayList<String> emailDescriptions = new ArrayList<>();

            int mailId = 1;
            String newStatus;
            String sender;
            String subject;

            for (Email mail : user.getMailbox()) {

                if (mail.isNew()) {
                    newStatus = "[ New ]";
                } else {
                    newStatus = "       ";
                }

                sender = mail.getSender();
                subject = mail.getSubject();

                String result = mailId + "." + " " + newStatus + " " + sender + "      " + subject;
                emailDescriptions.add(result);

                mailId += 1;
            }

            return emailDescriptions;

        }

        private String getEmail(String emailId, Account user) {
            // replaces the readEMail function from the exercise description
            int targetId = Integer.parseInt(emailId);

            if (targetId < 1 || targetId > user.getMailbox().size()) {
                // invalid emailId given
                return "ERROR_INVALID_EMAIL_ID";
            } else {
                // the given emailId id valid
                Email targetEmail = user.getMailbox().get(Integer.parseInt(emailId) - 1);
                String result = targetEmail.getMainbody();
                targetEmail.markAsRead();

                return result;
            }

        }

        private String deleteEmail(String emailId, Account user) {
            return user.deleteEmail(emailId);
        }

        private void logOut() {

            // NOTE: outer class method directly
            // accesses and changes inner class private variable

            // log out the currently logged in user
            loggedInUser = null;
        }

        private void exit() {
            stopListening = true;
        }

    }

    public static void main(String[] args) {

        new MailServer(5678);

    }

}
