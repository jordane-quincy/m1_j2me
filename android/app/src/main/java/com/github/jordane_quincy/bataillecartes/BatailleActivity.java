package com.github.jordane_quincy.bataillecartes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class BatailleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bataille);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        String prenom = intent.getStringExtra("prenom");
        Toast.makeText(getApplicationContext(), "login message : " + nom +", prenom : "+prenom, Toast.LENGTH_SHORT).show();

    }

}
