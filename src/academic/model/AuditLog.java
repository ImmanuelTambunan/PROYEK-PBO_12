package academic.model;

public class AuditLog {
    private int id;
    private String username;
    private String action;
    private String detail;
    private String timestamp;

    // Constructor untuk SELECT dari DB (dengan id)
    public AuditLog(int id, String username, String action, String detail, String timestamp) {
        this.id        = id;
        this.username  = username;
        this.action    = action;
        this.detail    = detail;
        this.timestamp = timestamp;
    }

    // Constructor untuk INSERT baru (tanpa id)
    public AuditLog(String username, String action, String detail, String timestamp) {
        this(-1, username, action, detail, timestamp);
    }

    public int    getId()        { return id; }
    public String getUsername()  { return username; }
    public String getAction()    { return action; }
    public String getDetail()    { return detail; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %-12s %-20s %s | %s",
            timestamp, username, action, detail != null ? detail : "", "");
    }
}
