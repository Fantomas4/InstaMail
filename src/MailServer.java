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
        private BufferedReader in;
        private PrintWriter out;
        private Account loggedInUser;
        boolean stopListening;

        private RequestServiceThread(Socket socket) {

            reqSocket = socket;
            loggedInUser = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
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

            out.println("connection_successful");

            String receivedMsg = "no_msg";

            while (!stopListening) {

                // send the appropriate menu to the client
                String menuOptions;
                // send the menu options to the client after completing his request
                if (loggedInUser != null) {
                    // the thread handles requests from a logged in user
                    menuOptions = "===============\n> NewEmail\n> ShowEmails\n> ReadEmail\n> DeleteEmail\n> LogOut\n> Exit\n===============";
                } else {
                    // the thread handles requests from a guest user
                    menuOptions = "==========\n> LogIn\n> Register\n> Exit";
                }

                out.println(menuOptions);

                try {
                    receivedMsg = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                switch (receivedMsg) {

                    case "login_request":
                        try {
                            out.println("Type your username: ");
                            String recvUsername = in.readLine();
                            System.out.println("DIAG: eftasa1");
                            out.println("Type your password: ");
                            System.out.println("DIAG: eftasa2");
                            String recvPassword = in.readLine();

                            String result = login(recvUsername, recvPassword);

                            switch (result) {
                                case "verification_success":
                                    loggedInUser = getUserAccount(recvUsername);
                                    out.println("Welcome back " + recvUsername + "!");
                                    break;
                                case "username_not_found":
                                    out.println("Error: Username does not exist!");
                                    break;
                                case "invalid_password":
                                    out.println("Error: Wrong password!");
                                    break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    case "register_request":
                        String username = "no_username";
                        String password = "no_password";

                        try {
                            out.println("Enter a username: ");
                            username = in.readLine();
                            out.println("Enter a password: ");
                            password = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (register(username, password).equals("account_created_successfully")) {
                            out.println("Account created successfully!");
                        } else if (register(username, password).equals("error_username_already_exists")) {
                            out.println("Error! Username already exists!");
                        }


                        break;

                    case "new_email_creation_request":

                        String receiver = "no_user";
                        String subject = "no_subject";
                        String mainBody = "no_main_body";

                        try {
                            out.println("Receiver: ");
                            receiver = in.readLine();
                            out.println("Subject: ");
                            subject = in.readLine();
                            out.println("Main body: ");
                            mainBody = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String result = newEmail(loggedInUser.getUsername(), receiver, subject, mainBody);

                        if (result.equals("email_sent_successfully")) {
                            out.println("Mail sent successfully!");
                        } else if (result.equals("error_receiver_not_valid")) {
                            out.println("Error! Invalid receiver!");
                        }

                        break;

                    case "get_emails_preview_request":
                        ArrayList<String> previewEntries = getEmailsPreview(loggedInUser);
                        StringBuilder sb = new StringBuilder();

                        // convert the ArrayList<String> to a String containing
                        // the preview's entries
                        for (String entry : previewEntries) {
                            sb.append(entry);
                            sb.append("\n");
                        }

                        out.println(sb.toString());

                        break;

                    case "get_complete_email_request":

                        String emailId = "no_id";

                        try {
                            out.println("Enter the email's ID: ");
                            emailId = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String emailResult = getEmail(emailId, loggedInUser);

                        if (emailResult.equals("error_invalid_emailId")) {
                            out.println("Error! Email ID not found!");
                        } else {
                            out.println(emailResult);
                        }

                        break;

                    case "delete_email_request":

                        String deleteId = "-1";

                        try {
                            out.println("Enter the email's ID: ");
                            deleteId = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String deleteResult = deleteEmail(deleteId, loggedInUser);

                        if (deleteResult.equals("error_invalid_emailId")) {
                            out.println("Email deleted successfully!");
                        } else if (deleteResult.equals("success_valid_emailId")) {
                            out.println("Error! Invalid email id!");
                        }

                    case "logout_request":
                        logOut();

                        break;

                    case "exit_request":
                        exit();

                        break;

                }

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
                    return "error_username_already_exists";
                }
            }

            // The given username is accepted, so a new
            // account is created using the given username and password.
            // The new account object is then added to the accountList array list.
            accountList.add(new Account(username, password));

            return "account_created_successfully";
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
                    return "verification_success";
                } else {
                    // The given password is invalid
                    return "invalid_password";
                }
            } else {
                // matchingAccount is null, meaning no account exists with the given username
                return "username_not_found";
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
                return "email_sent_successfully";
            } else {
                // receiver is not valid (a user with the given username does not exist)
                return "error_receiver_not_valid";
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
                return "error_invalid_emailId";
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
