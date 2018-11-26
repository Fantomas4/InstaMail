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
}
