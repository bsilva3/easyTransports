package com.transports.account_management;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.transports.MainActivity;
import com.transports.R;
import com.transports.utils.Constants;
import com.transports.utils.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.transports.utils.Constants.PAYMENT_CURRENCY;
import static com.transports.utils.Constants.PAYMENT_CURRENCY_EUR;
import static com.transports.utils.Constants.PAYMENT_MESSAGE;
import static com.transports.utils.Constants.PAYMENT_PASSWORD;
import static com.transports.utils.Constants.PAYMENT_SHAREDPREFERENCES_PASS;
import static com.transports.utils.Constants.PAYMENT_STATUS;
import static com.transports.utils.Constants.PAYMENT_STATUS_SUCCESSFULL;
import static com.transports.utils.Constants.PAYMENT_USER_ID;
import static com.transports.utils.URLs.PAYMENTS_CREATE_ACCOUNT;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //Register user in payments service
                                    registerUserInPayments(auth.getCurrentUser().getEmail());
                                    //sign in user
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra(Constants.MENU_INTENT, R.id.bottom_menu_tickets);//start in the "my tickets menu"
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void registerUserInPayments(String email){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //create list of request ticket creation json objects
        JSONObject jsonBody = new JSONObject();
        String uuid = UtilityFunctions.generateString();
        try{
            jsonBody.put(PAYMENT_USER_ID, email);
            jsonBody.put(PAYMENT_PASSWORD, Constants.PAYMENT_DEFAULT_PASS);
            jsonBody.put(PAYMENT_CURRENCY, PAYMENT_CURRENCY_EUR);
        } catch (JSONException e){ }
        Log.d("paymentRequest", jsonBody+"");

        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonArrayRequest  = new JsonObjectRequest(
                Request.Method.POST,
                PAYMENTS_CREATE_ACCOUNT,
                jsonBody,
                response -> {
                    Log.d("paymentRegister", response+"");

                    try {
                         String status = response.getJSONObject(PAYMENT_MESSAGE).getString(PAYMENT_STATUS);
                        if (!status.equalsIgnoreCase(PAYMENT_STATUS_SUCCESSFULL)){
                            Toast.makeText(getApplication(), "Could not register user in payment service", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("errorPayment", error+"");
                    Toast.makeText(getApplication(), "Could not register user in payment service", Toast.LENGTH_SHORT);
                    /*new AlertDialog.Builder(getApplication())
                            .setTitle(getString(R.string.ticket_purchase_error_title))
                            .setMessage(getString(R.string.ticket_purchase_error_message))
                            .setIcon(android.R.drawable.ic_dialog_alert).setNeutralButton("OK", null).show();*/
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

}
