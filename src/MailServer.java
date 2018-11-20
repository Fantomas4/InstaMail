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

    public String login() {

    }

    public String newEmail() {

    }

    public String showEmails() {
        // change the return data type
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
