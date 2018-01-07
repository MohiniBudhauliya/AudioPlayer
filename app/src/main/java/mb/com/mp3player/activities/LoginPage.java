package mb.com.mp3player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mb.com.mp3player.interfaces.ApiInterface;
import mb.com.mp3player.R;
import mb.com.mp3player.models.RegistrationData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anshul on 21-12-17.
 */

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    private EditText email,password;
    private Button loginbtn,toregister;
    String enteremail,enterpwd;
    boolean email_result = false;
    private static ApiInterface apiInterface;
    private static Retrofit retrofit = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        loginbtn=(Button)findViewById(R.id.login);
        toregister=(Button)findViewById(R.id.toregister);

        loginbtn.setOnClickListener(this);
        toregister.setOnClickListener(this);

        initialize();
    }
    public void initialize() {
        enteremail = email.getText().toString();
        enterpwd = password.getText().toString();
    }
    public void register() {
        initialize();
        if (!validate()) {
            Toast.makeText(this, "Please provide vaild information", Toast.LENGTH_SHORT);
        } else {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).build();


            retrofit = new Retrofit.Builder().baseUrl(ApiInterface.BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            String emailId = email.getText().toString().trim();
            String paswd = password.getText().toString().trim();
            try {
                RegistrationData registerationdata = new RegistrationData(emailId, paswd);
                registerationdata.setuseremail(emailId);
                registerationdata.setuserpassword(paswd);
                apiInterface = retrofit.create(ApiInterface.class);
                apiInterface.getdetails(registerationdata).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully logged in ", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginPage.this,HomePage.class);
                            startActivity(intent);
                        }
                        else if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(), "Record Not Found", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == 500) {
                            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Getting issue while fetching data to the server ", Toast.LENGTH_SHORT);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean validate() {
        boolean valid = true;
        if (enteremail.isEmpty()||enteremail.length()>=32) {
            email.setError("Please Enter Valid name");
            valid=false;
            return valid;
        }
        if(enteremail.isEmpty())
        {
            email.setError("Please Enter Email");
            valid=false;
            return valid;

        }
        else
        {
            isValidEmailAddress(enteremail);
            if(email_result)
            {
                email.setText(enteremail);
            }
            else
            {
                email.setError("Enter valid email address.");
                valid=false;
                return valid;
            }
        }
        if(enterpwd.isEmpty())
        {
            password.setError("Please enter password");
            valid=false;
            return valid;
        }
        else
        {
            if(enterpwd.length()>=8&&isValidPassword(password.getText().toString()))
            {
                password.setText(enterpwd);
            }
            else
            {
                password.setError("1.Password must contain alphanumeric value"+"\n"
                        + "2.Atleast one special character"+"\n"+
                        "3.Password length mustbe atleast 8");
                valid=false;
                return valid;
            }
        }
        return valid;
    }
    public  boolean isValidEmailAddress(String email)
    {

        if(email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))//email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        {
            email_result=true;
            return  email_result;
        }
        return email_result;
    }

    public  boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id) {
            case R.id.login:
                register();
                break;

            case R.id.toregister:
                Intent gotoregistration=new Intent(this, RegistrationPage.class);
                startActivity(gotoregistration);
                break;
        }

    }
}
