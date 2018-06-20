package app.sms.checkmy.com.smsverificat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerificationActivity extends AppCompatActivity {

    Button verif;
    EditText codeverif;
    FirebaseAuth phoneAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        codeverif = findViewById(R.id.code_verif);
        verif = findViewById(R.id.btn_verify);

        phoneAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        final String verificated = intent.getExtras().getString("poneverifId");


        verif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeverif.getText().toString().trim();

                PhoneAuthCredential authCredential= PhoneAuthProvider.getCredential(verificated,code);
                signInWithPhoneAuthCredential(authCredential);

            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential authCredential) {
        phoneAuth.signInWithCredential(authCredential).addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                    String phoneNumber = user.getPhoneNumber();

                    Intent intent = new Intent(VerificationActivity.this, WelcomeActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);
                    finish();
                }
                else {

                }

            }
        });
    }
}
