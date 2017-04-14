package info.androidhive.gmail.login;


public class User {

    private String username;
    private String oldPassword;
    private String newPassword;


    public void setUsername(String username) {
        this.username = username;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }


    public String getOldPassword() {
        return oldPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public String getUsername() {
        return username;
    }


}
