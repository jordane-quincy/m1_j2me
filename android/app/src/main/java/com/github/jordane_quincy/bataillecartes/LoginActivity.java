package com.github.jordane_quincy.bataillecartes;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

/**
 * A login screen that offers login
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mNomView;
    private AutoCompleteTextView mPrenomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mNomView = (AutoCompleteTextView) findViewById(R.id.nom);
        mPrenomView = (AutoCompleteTextView) findViewById(R.id.prenom);

        // populateAutoComplete();

        Button mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfos();
            }
        });

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void saveInfos() {
        // Reset errors.
        mNomView.setError(null);
        mPrenomView.setError(null);

        // Store values at the time of the nom attempt.
        String nom = mNomView.getText().toString();
        String prenom = mPrenomView.getText().toString();

        boolean error = false;

        // Check infos
        if (TextUtils.isEmpty(nom)) {
            mNomView.setError(getString(R.string.error_field_required));
            error = true;
        }
        if (TextUtils.isEmpty(prenom)) {
            mPrenomView.setError(getString(R.string.error_field_required));
            error = true;
        }

        if(!error){
            // Show a progress spinner, and kick off a background task to
            // perform the user nom attempt.


            Toast.makeText(getApplicationContext(),"nom : "+nom, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getApplicationContext(), BatailleActivity.class);
            i.putExtra("nom", nom);
            i.putExtra("prenom", prenom);
                    startActivity(i);
        }
    }

}

