package info.androidhive.gmail.login;


public class User {

    private String username;
    private String password;
    private String newPassword;


    public void setUsername(String username) {
        this.username = username;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public String getPassword(String password) {
        return password;
    }
    public String getNewPassword(String password) {
        return newPassword;
    }
    public String getUsername() {
        return username;
    }


}
