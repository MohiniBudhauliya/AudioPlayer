package mb.com.mp3player.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;

import org.json.JSONException;

/**
 * Created by Anshul on 23-11-17.
 */

public class RegistrationData {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("useremail")
    @Expose
    private String useremail;
    @SerializedName("userpassword")
    @Expose
    private String userpassword;
    @SerializedName("userdob")
    @Expose
    private String userdob;


    public RegistrationData(String name, String email, String password, String dob) throws JSONException {
        this.username=name;
        this.useremail=email;
        this.userpassword=password;
        this.userdob=dob;
    }
    public RegistrationData(String email, String password) throws JSONException {
        this.useremail=email;
        this.userpassword=password;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getuseremail() {

        return useremail;
    }

    public void setuseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getuserpassword() {

        return userpassword;
    }

    public void setuserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getuserdob() {

        return userdob;
    }

    public void setuserdob(String userdob) {
        this.userdob = userdob;
    }


}
