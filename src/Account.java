import java.util.ArrayList;
import java.util.List;

public class Account {

    private String username;
    private String password;
    private List<Email> mailbox;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        mailbox = new ArrayList<>();

        // *** FOR TESTING PURPOSES ONLY
        mailbox.add(new Email("fantom", "fantom", "test", "test finish."));
        // *** FOR TESTING PURPOSES ONLY
    }

    public String getUsername() {
        return username;
    }

    public boolean isCorrectPassword(String givenPassword) {
        // the given password matches the account's actual password
        return this.password.equals(givenPassword);
    }

    public List<Email> getMailbox() {
        return  mailbox;
    }

    public void addNewEmail(Email email) {
        mailbox.add(email);
    }

    public String deleteEmail(String emailId) {

        int targetId = Integer.parseInt(emailId);

        if (targetId < 1 || targetId > mailbox.size()) {
            // invalid emailId given
            return "ERROR_INVALID_EMAIL_ID";
        } else {
            // the emailId given is valid
            mailbox.remove(Integer.parseInt(emailId) - 1);
            return "EMAIL_DELETION_SUCCESS";
        }
    }
}
