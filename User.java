public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String hashPassword; // store hashed password
    protected String role;

    public User(String id, String name, String email, String hashPassword) {
        this.id = id.toUpperCase();
        this.name = name;
        this.email = email;
        if(this.id.charAt(0)=='S')
        {
            this.role="student";
        }
        else {
            this.role="instructor";
        }

        this.hashPassword = hashPassword;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }


    public abstract void showInfo();
}