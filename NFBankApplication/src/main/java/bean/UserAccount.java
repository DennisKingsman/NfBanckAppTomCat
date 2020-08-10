package bean;

import java.sql.Date;

public class UserAccount {
    private Long id;
    private String userLogin;
    private String userPassword;
    private String userRole;
    private Date dateOfBirth;
    private String userName;
    private String secondName;

    public UserAccount() {
    }

    public UserAccount(String userLogin, String userPassword, String userRole) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userRole = userRole;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.userLogin;
    }

    public void setLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getPassword() {
        return this.userPassword;
    }

    public void setPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getRole() {
        return this.userRole;
    }

    public void setRole(String userRole) {
        this.userRole = userRole;
    }

    public Date getDate() {
        return this.dateOfBirth;
    }

    public void setDate(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return this.userName;
    }

    public void setName(String userName) {
        this.userName = userName;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    @Override
    public String toString() {
        return "user account: "
                + this.userLogin
                + " name: "
                + this.userName
                + " secondName: "
                + this.secondName;
    }
}
