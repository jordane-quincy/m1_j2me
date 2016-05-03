package com.github.jordane_quincy.bataillecartes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
    private Personne joueurBdd = null ;
    private AutoCompleteTextView mNomView;
    private AutoCompleteTextView mPrenomView;
    private ProgressBar mSeekBarAge;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioBtnHomme;
    private RadioButton mRadioBtnFemme;

    private PersonneDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mNomView = (AutoCompleteTextView) findViewById(R.id.nom);
        mPrenomView = (AutoCompleteTextView) findViewById(R.id.prenom);
        mSeekBarAge = (SeekBar) findViewById(R.id.seekBar);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioBtnHomme = (RadioButton) findViewById(R.id.radioButton);
        mRadioBtnFemme = (RadioButton) findViewById(R.id.radioButton2);

        mRadioBtnHomme.setChecked(true); //default

        mSeekBarAge.setMax(99);
        mSeekBarAge.setProgress(18);
        mSeekBarAge.incrementProgressBy(1);

        datasource = new PersonneDataSource(this);
        datasource.open();

        Personne joueurEnBdd = datasource.getPersonneInDb();
        datasource.deletePersonne(joueurEnBdd);
        populateAutocomplete(joueurEnBdd);

        Button mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfos();
            }
        });

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

        Personne personne = joueurBdd == null ? new Personne() : joueurBdd;

        personne.setNom(mNomView.getText().toString());
        personne.setPrenom(mPrenomView.getText().toString());
        personne.setAge(mSeekBarAge.getProgress());
        personne.setSexe(mRadioBtnHomme.isChecked() ? mRadioBtnHomme.getText().toString() : mRadioBtnFemme.getText().toString());

        int idRadioBtnSexeSelected = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioSexeBtn = (RadioButton) findViewById(idRadioBtnSexeSelected);
        personne.setSexe(radioSexeBtn.getText().toString());

        boolean error = false;

        // Check infos
        if (TextUtils.isEmpty(personne.getNom())) {
            mNomView.setError(getString(R.string.error_field_required));
            error = true;
        }
        if (TextUtils.isEmpty(personne.getPrenom())) {
            mPrenomView.setError(getString(R.string.error_field_required));
            error = true;
        }

        if(!error){
            Toast.makeText(getApplicationContext(), personne.toString(), Toast.LENGTH_SHORT).show();

            savePersonneToDb(personne);

            Intent i = new Intent(getApplicationContext(), BatailleActivity.class);
            i.putExtra("personne", personne);
            startActivity(i);
        }
    }

    private void savePersonneToDb(Personne personne){
        datasource.createPersonne(personne);
    }

    private void populateAutocomplete(Personne personne){
        if(personne != null){
            joueurBdd = personne;
            mNomView.setText(personne.getNom());
            mPrenomView.setText(personne.getPrenom());

            mSeekBarAge.setProgress(personne.getAge());

            if("Homme".equals(String.valueOf(personne.getSexe()))){
                mRadioBtnFemme.setChecked(false);
                mRadioBtnHomme.setChecked(true);
            }else{
                mRadioBtnHomme.setChecked(false);
                mRadioBtnFemme.setChecked(true);
            }
        }
    }
}

