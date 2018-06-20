package app.sms.checkmy.com.smsverificat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    Button btn_sendCode;
    TextView number;
    private String phoneVerificationId;
    private static final String TAG = "PhoneAuth";
    FirebaseAuth phoneAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_sendCode = findViewById(R.id.btn_btn_sendCode);
        number = findViewById(R.id.phone_number);

        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);

        phoneAuth = FirebaseAuth.getInstance();


        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccp.registerCarrierNumberEditText(editTextCarrierNumber);
                String ee = ccp.getFullNumberWithPlus().toString().trim();
                number.setText(ee);
            }
        });


        btn_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String num = editTextCarrierNumber.getText().toString().trim();

                if (TextUtils.isEmpty(num)) {

                    Toast.makeText(MainActivity.this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                } else {




                    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {

                        }

                        @Override
                        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            phoneVerificationId = s;
                            resendToken = forceResendingToken;




                        }

                    };
                    String ee = ccp.getFullNumberWithPlus().toString().trim();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(ee,60,TimeUnit.SECONDS,MainActivity.this,mCallbacks);
                    Toast.makeText(MainActivity.this, "Sending Verification Code", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        phoneAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            String phoneNumber = user.getPhoneNumber();
                            Intent intent = new Intent(MainActivity.this,VerificationActivity.class);
                            intent.putExtra("poneverifId",phoneVerificationId);
                            startActivity(intent);


                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    }
