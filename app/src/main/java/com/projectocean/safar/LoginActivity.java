package com.projectocean.safar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectocean.safar.models.User;
import com.projectocean.safar.sql.DatabaseHelper;


public class LoginActivity extends AppCompatActivity {
    ProgressDialog pd;
    EditText etEmail, etPassword;
    TextView tvRegister;
    Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    FirebaseUser currentUser;
    SignInButton google;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    String personName, personEmail;
    ValueEventListener v;
    boolean signInStatus = false;
    FirebaseDatabase db;
    String uid;
    TextView adminLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_login);

        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        account = GoogleSignIn.getLastSignedInAccount(this);

        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        tvRegister = (TextView) findViewById(R.id.register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        google = findViewById(R.id.google);
        adminLogin = findViewById(R.id.admin_login);

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class));
                finish();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("Users");

        v = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("updateui", "mref value event listened");

                if (!dataSnapshot.child(mAuth.getCurrentUser().getUid()).exists()) {
                    User user = new User(personName, personEmail, null, false, null, 0, 0);
                    mRef.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(mAuth.getCurrentUser());
                        }
                    });
                } else {
                    updateUI(mAuth.getCurrentUser());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());
    }


    private boolean startSignIn(String email, String pass) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
        } else {

            pd.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login problem", Toast.LENGTH_LONG).show();
                    pd.dismiss();

                } else {
                    signInStatus = true;
                    updateUI(mAuth.getCurrentUser());
                }
                }
            });
        }
        return signInStatus;
    }


    void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
        pd.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        pd.dismiss();
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("abc", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        personName = acct.getDisplayName();
        String personGivenName = acct.getGivenName();
        final String personFamilyName = acct.getFamilyName();
        personEmail = acct.getEmail();

        Log.d("abc", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        pd.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("updateui", "auth complete listener is being called");

                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {
                    mRef.addValueEventListener(v);
                }

                }
            });
    }

    private boolean updateUI(FirebaseUser firebaseUser) {

        Log.d("updateui", "updateUI is getting called");

        if (firebaseUser != null) {

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            Cursor cursor = databaseHelper.getData();
            cursor.moveToNext();
            if (cursor.getCount() == 0) {
                if (pd.isShowing())
                    pd.dismiss();
                mRef.removeEventListener(v);
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            } else if (cursor.getString(cursor.getColumnIndex("TYPE")).equals("admin")) {
                if (pd.isShowing())
                    pd.dismiss();
                mRef.removeEventListener(v);
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                finish();
            } else {
                if (pd.isShowing())
                    pd.dismiss();
                mRef.removeEventListener(v);
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }
            return true;
        }

        return false;
    }

}
