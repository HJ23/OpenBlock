package appsec.openblock.DTO;


import javax.validation.constraints.*;

public class RegistrationDTO {

    @NotBlank(message = "Username field should not be empty !")
    @Size(min=2,max=25,message = "Username size should be between 3 and 10 !")
    private String firstname;

    @NotBlank(message = "Username field should not be empty !")
    @Size(min=2,max=25,message = "Username size should be between 3 and 10 !")
    private String lastname;


    @NotBlank(message = "Email field should not be empty !")
    @Email(message = "Enter valid email !")
    private String email;
    @NotBlank(message = "Password field should not be empty !")
    @Size(min=5,max=16,message = "Enter valid password, size must be between 5 and 16 !")
    private String password;

    @Pattern(regexp = "^\\d{10}$",message = "Enter valid mobile number all number and 10 digits required !")
    @NotBlank(message = "Mobile number field should not be empty !")
    private String mobileNumber;

    @NotBlank(message = "Captcha is empty!")
    private String captchaInput;

    private String captchaAnswer;

    public String getCaptchaInput() {
        return captchaInput.strip();
    }

    public void setCaptchaInput(String captcha) {
        this.captchaInput = captcha;
    }

    public String getCaptchaAnswer() {
        return captchaAnswer.strip();
    }

    public void setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return firstname;
    }

}
