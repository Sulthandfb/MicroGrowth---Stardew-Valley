package fxml_helloworld.Login_Scene;

import java.time.LocalDate;

public class RegisterData {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String typeRegister;

    public RegisterData(String name, String email, String password, String phoneNumber, String typeRegister) {
        this.name = name;
        this.email = email;
        this.password =  password;
        this.phoneNumber = phoneNumber;
        this.typeRegister = typeRegister;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTypeRegister() {
        return typeRegister;
    }

    public void setTypeRegister(String typeRegister) {
        this.typeRegister = typeRegister;
    }

    
    
}
