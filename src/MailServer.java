import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailServer {

    private int handshakePort;
    private ArrayList<Account> accountList;
    private Account loggedInUser;
    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSockets;
    private ArrayList<Thread> clientThreads;


    public MailServer(int port) {
        handshakePort = port;
        accountList = new ArrayList<>();
        try {
            serverSocket = new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handshakeListeningThread() {

        while (true) {
            try {
                // accept an incoming handshake from a new client,
                // and create a new socket for this particular client
                Socket clientSocket = serverSocket.accept();

                // create a new Client object, pass it to a new client thread,
                // start the client thread and then add
                // it to the clientThreads list
                Thread clientThread = new Thread(new ClientThread(clientSocket));
                clientThread.start();
                clientThreads.add(clientThread);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String register(String username, String password) {

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

    public String login(String givenUsername, String givenPassword) {

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

    public String newEmail() {

    }

    public ArrayList<String> getEmailsPreview() {
        // replaces the showEmails function from the exercise description
        ArrayList<String> emailDescriptions = new ArrayList<>();

        int mailId = 1;
        String newStatus;
        String sender;
        String subject;

        for (Email mail : loggedInUser.getMailbox()) {

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

    public String getEmail(String emailId) {
        // replaces the readEMail function from the exercise description
        int targetId = Integer.parseInt(emailId);

        if (targetId < 1 || targetId > loggedInUser.getMailbox().size()) {
            // invalid emailId given
            return "error_invalid_emailId";
        } else {
            // the given emailId id valid
            Email targetEmail = loggedInUser.getMailbox().get(Integer.parseInt(emailId) - 1);
            String result = targetEmail.getMainbody();
            targetEmail.markAsRead();

            return result;
        }

    }

    public String deleteEmail(String emailId) {
        return loggedInUser.deleteEmail(emailId);
    }

    public void logOut() {

    }

    public void exit() {

    }
}
