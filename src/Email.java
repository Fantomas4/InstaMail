public class Email {

    private boolean isNew;
    private String sender;
    private String receiver;
    private String subject;
    private String mainbody;


    public Email(String sender, String receiver, String subject, String mainbody) {
        isNew = true;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainbody = mainbody;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getMainbody() {
        return mainbody;
    }

    public boolean isNew() {
        return isNew;
    }

    public void markAsRead() {
        isNew = false;
    }




}
