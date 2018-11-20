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
}
