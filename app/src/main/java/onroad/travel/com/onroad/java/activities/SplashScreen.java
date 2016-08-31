package onroad.travel.com.onroad.java.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.appacitive.android.AppacitiveContext;
import com.appacitive.core.AppacitiveUser;
import com.appacitive.core.model.Callback;
import com.appacitive.core.model.Environment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;
import java.util.Arrays;
import onroad.travel.com.onroad.R;
import onroad.travel.com.onroad.java.CommonMethods;
import onroad.travel.com.onroad.java.Constants;
import onroad.travel.com.onroad.java.models.Model;
import onroad.travel.com.onroad.java.utils.SharedPreferenceModel;

public class SplashScreen extends Activity {
    CallbackManager callbackManager;
    SharedPreferenceModel sharedPreferenceModel;
    Model model;
    String fb_id, location, user_slug;
    JSONObject object;
    AppacitiveUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppacitiveContext.initialize("ONaJNEU0ZzTSmbmqvXEq+fYax0/uYv4iUdBBp9WyiKY=", Environment.sandbox, this.getApplicationContext());

        sharedPreferenceModel = new SharedPreferenceModel(SplashScreen.this);

        if (checkLoginStatus()) {
            goToMainActivity();
        } else {
            login();
        }


    }

    private void login() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends", "user_location"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("Status", "Success");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jobject,
                                    GraphResponse response) {
                                object = jobject;
                                Log.e("facebook response", "" + object);

                                model = new Model(SplashScreen.this);

                                fb_id = object.optString("id");
                                user_slug = CommonMethods.createSlug(Long.parseLong(fb_id));
                                location = object.optJSONObject("location").optString("name");


                                user = new AppacitiveUser();
                                user.setFirstName(object.optString("name"));
                                user.setUsername(fb_id);
                                user.setPassword(user_slug);
                                user.setEmail(object.optString("email"));
                                user.setStringProperty("facebook_id", fb_id);
                                user.setStringProperty("user_slug", user_slug);
                                user.setStringProperty("birthdate", object.optString("birthday"));
                                user.setStringProperty("phone", "");
                                user.setStringProperty("user_country", location);
                                user.setStringProperty("user_current_city", location);


                                long expiry = -1;
                                int attempts = -1;
                                AppacitiveUser.loginInBackground(fb_id, user_slug, expiry, attempts, new Callback<AppacitiveUser>() {
                                    @Override
                                    public void success(AppacitiveUser user) {
                                        String id = "" + user.getId();
                                        model.createUser(id, fb_id, fb_id, "https://graph.facebook.com/" + fb_id + "/picture?type=large", location, location,
                                                "5",  location, object.optString("email"), object.optString("name"), " ", object.optString("birthday"), "NA", user_slug);
                                        sharedPreferenceModel.insertData(Constants.LoginStatus, true);
                                        goToMainActivity();
                                    }

                                    @Override
                                    public void failure(AppacitiveUser result, Exception e) {


                                        user.signupInBackground(new Callback<AppacitiveUser>() {
                                            @Override
                                            public void success(AppacitiveUser user) {
                                                String id = "" + user.getId();
                                                Log.e("id", id);
                                                model.createUser(id, fb_id, fb_id, "https://graph.facebook.com/" + fb_id + "/picture?type=large", location, location,
                                                        "5",  location, object.optString("email"), object.optString("name"), " ", object.optString("birthday"), "NA", user_slug);
                                                sharedPreferenceModel.insertData(Constants.LoginStatus, true);
                                                goToMainActivity();
                                            }

                                            @Override
                                            public void failure(AppacitiveUser user, Exception e) {
                                                Log.e("exce", e.toString());
                                            }
                                        });
                                    }
                                });


//                                sharedPreferenceModel.insertData(Constants.user_name, object.optString("name"));
//                                sharedPreferenceModel.insertData(Constants.user_fb_id,object.optString("id"));

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                SplashScreen.this.finish();
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(getApplicationContext(), "Facebook error", Toast.LENGTH_LONG).show();
                Log.e("facebook error", "" + error);
            }
        });

    }

    private boolean checkLoginStatus() {

        SharedPreferenceModel sharedPreferenceModel = new SharedPreferenceModel(SplashScreen.this);

        return sharedPreferenceModel.retreiveData(Constants.LoginStatus, false);
    }

    private void goToMainActivity() {

        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
        finish();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}


