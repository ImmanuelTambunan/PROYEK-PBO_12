package academic.model;

public class User {
    private String username;
    private String passwordHash;
    private Role role;
    private String refId;

    public User(String username, String passwordHash, Role role, String refId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.refId = refId;
    }

    public String getUsername()    { return username; }
    public String getPasswordHash(){ return passwordHash; }
    public Role   getRole()        { return role; }
    public String getRefId()       { return refId; }

    @Override
    public String toString() {
        return "User[" + username + ", role=" + role + ", refId=" + refId + "]";
    }
}
