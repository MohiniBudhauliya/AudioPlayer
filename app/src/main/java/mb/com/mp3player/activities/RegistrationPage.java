package mb.com.mp3player.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegistrationPage extends AppCompatActivity {


    EditText userName, userEmail, password, userDOB;
    String uName, uEid, pwd, DOB;
    Button registerButton;
    boolean email_result = false;
    boolean validDate = false;
    private static final String  TAG ="RegistrationPage" ;
    private static ApiInterface apiInterface;
    private static Retrofit retrofit = null;
    TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrationpage);
        userName = (EditText) findViewById(R.id.UserName);
        userEmail = (EditText) findViewById(R.id.UserEmail);
        password = (EditText) findViewById(R.id.Password);
        registerButton = (Button) findViewById(R.id.RegisterButton);
        userDOB = (EditText) findViewById(R.id.UserDOB);
        result=(TextView)findViewById(R.id.response);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    public void register() {
        initialize();
        if (!Validate()) {
            Toast.makeText(getApplicationContext(), "Your Registration is not done yet.", Toast.LENGTH_SHORT);
        }
        else {

            onRegistrationSuccess();
        }

    }


    public  void onRegistrationSuccess() {
        // To issue network requests to a RESTful API with Retrofit,
        // we need to create an instance using the Retrofit Builder class and configure it with a base URL
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).build();

         retrofit = new Retrofit.Builder().baseUrl(ApiInterface.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        String name=userName.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        String dob=userDOB.getText().toString().trim();
        try {
            RegistrationData registerationdata = new RegistrationData(name,email, pwd,dob);
            registerationdata.setusername(name);
            registerationdata.setuseremail(email);
            registerationdata.setuserpassword(pwd);
            registerationdata.setuserdob(dob);
            apiInterface=retrofit.create(ApiInterface.class);
            apiInterface.senddetails(registerationdata).enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if(response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),"Your record saved",Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==500)
                    {
                        Toast.makeText(getApplicationContext(),"Email id is already registered.",Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==400)
                    {
                        Toast.makeText(getApplicationContext(),"Provided information is not in valid format.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"Unable to save data on server",Toast.LENGTH_SHORT);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public boolean Validate() {
       boolean valid = true;
        if (uName.isEmpty() || uName.length() >= 32) {
            userName.setError("Please Enter Valid name");
            valid=false;
            return valid;
        }
        else
        {
            userName.setText(uName);
        }
        if (uEid.isEmpty()) {
            userEmail.setError("Please Enter Email");
            valid=false;
            return valid;

        } else {
            isValidEmailAddress(uEid);
            if (email_result) {
                userEmail.setText(uEid);
            } else {
                userEmail.setError("Enter valid email address.");
                valid=false;
                return valid;
            }
        }
        if (pwd.isEmpty()) {
            password.setError("Please enter password");
            valid=false;
            return valid;
        } else {
            if (pwd.length() >= 8 && isValidPassword(password.getText().toString())) {
                password.setText(pwd);

            } else {
                password.setError("1.Password must contain alphanumeric value" + "\n"
                        + "2.Atleast one special character" + "\n"
                        + "3.Password length mustbe atleast 8");
                valid=false;
                return valid;
            }
        }
        if (DOB.isEmpty()) {
            userDOB.setError("Please enter DOB");
            valid=false;
            return valid;
        } else {
            isvalidDate(DOB);
            if (validDate) {
                userDOB.setText(DOB);
            }
            else {userDOB.setError("Age should be greater than or equal to 18");
            valid=false;
            return valid;}
            }

        return valid;
    }


    public boolean isvalidDate(String DateOfBirth) {
        String[] s = DateOfBirth.split("/");
        int day = Integer.parseInt(s[0]);
        int month = Integer.parseInt(s[1]);
        int year = Integer.parseInt(s[2]);

        if (year >= 1947 && year <= 1999)
            {
                if(month>=1&&month<=12)
                {
                    if (day>=1&&day<=31) {
                        if (year % 4 == 0 || year % 100 == 0 || year % 400 == 0) {
                            if (month == 2) {
                                if (day >= 1 && day <= 29) {
                                    validDate = true;
                                    return validDate;
                                } else {
                                    userDOB.setError("February has 29 days in leap year");
                                }
                            }
                            else if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) {
                                if (day >= 1 && day <= 31) {
                                    validDate = true;
                                    return validDate;
                                } else {
                                    userDOB.setError(month + "have 31 days");
                                }
                            }
                            else if ((month == 4 || month == 6 || month == 9 || month == 11 )) {
                                if (day >= 1 && day <= 30) {
                                    validDate = true;
                                    return validDate;
                                } else {
                                    userDOB.setError(month + "have 30 days");
                                }
                            }
                        }
                        else {
                            if (month == 2) {
                                if (day >= 1 && day <= 28) {
                                    validDate = true;
                                    return validDate;
                                } else {
                                    userDOB.setError("February has 28 days in non leap year");
                                }
                            }
                            if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) {
                                if (day >= 1 && day <= 30) {
                                    validDate = true;
                                    return validDate;
                                } else {
                                    userDOB.setError(month + "have 30 days");
                                }
                            }
                            else if ((month == 4 || month == 6 || month == 9 || month == 11 )) {
                                if (day >= 1 && day <= 30) {
                                    validDate = true;
                                    return validDate;
                                } else {
                                    userDOB.setError(month + "have 30 days");
                                }
                            }
                        }
                    }
                    else
                    {
                        userDOB.setError("days range should between 1 to 31");
                    }
                    }
                     else
                    {
                    userDOB.setError("month range should between 1 to 12");
                    }
                    }
                   else
                   {
                    userDOB.setError("Age should be greater than 18");
                   }
             return validDate;
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

    public void initialize()
    {
     uName=userName.getText().toString();
     uEid=userEmail.getText().toString();
     pwd=password.getText().toString();
     DOB =userDOB.getText().toString();
    }
    }
