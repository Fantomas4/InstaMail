import java.util.ArrayList;
import java.util.List;

public class MailServer {

    int port;
    List<Account> accountList;

    public MailServer(int port) {
        this.port = port;
        accountList = new ArrayList<>();
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

    public String getEmails() {
        // replaces the showEmails function in the exercise description

    }

    public String readEmail(String emailId) {


    }

    public String deleteEmail(String emailId) {

    }

    public void logOut() {

    }

    public void exit() {

    }
}
