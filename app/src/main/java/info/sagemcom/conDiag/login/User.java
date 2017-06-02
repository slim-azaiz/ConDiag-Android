package info.sagemcom.conDiag.login;


public class User {

    private String oldUsername;
    private String newUsername;
    private String oldPassword;
    private String newPassword;


    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
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

    public String getOldUsername() {
        return oldUsername;
    }
    public String getNewUsername() {
        return newUsername;
    }


}
