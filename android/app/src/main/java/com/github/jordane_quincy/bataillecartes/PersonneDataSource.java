package com.github.jordane_quincy.bataillecartes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PersonneDataSource {

    // Champs de la base de données
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NOM, MySQLiteHelper.COLUMN_PRENOM, MySQLiteHelper.COLUMN_AGE, MySQLiteHelper.COLUMN_SEXE };

    public PersonneDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Personne createPersonne(Personne personne) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, personne.getId());
        values.put(MySQLiteHelper.COLUMN_NOM, personne.getNom());
        values.put(MySQLiteHelper.COLUMN_PRENOM, personne.getPrenom());
        values.put(MySQLiteHelper.COLUMN_AGE, personne.getAge());
        values.put(MySQLiteHelper.COLUMN_SEXE, personne.getSexe());
        long insertId = database.insertWithOnConflict(MySQLiteHelper.TABLE_PERSONNE, null,
                values, SQLiteDatabase.CONFLICT_REPLACE); // SQLiteDatabase.CONFLICT_REPLACE == insert or update si id existant
        Log.d("LoginActivity - Db", "personne.getAge() : " + personne.getAge());
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PERSONNE,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Personne newPersonne = cursorToPersonne(cursor);
        Log.d("LoginActivity - Db", "newPersonne .getAge() : " + newPersonne.getAge());
        cursor.close();
        return newPersonne;
    }

    public void deletePersonne(Personne personne) {
        long id = personne.getId();
        System.out.println("personne deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_PERSONNE, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    private Personne cursorToPersonne(Cursor cursor) {
        Personne personne = new Personne();
        personne.setId(cursor.getInt(0));
        personne.setNom(cursor.getString(1));
        personne.setPrenom(cursor.getString(2));
        personne.setAge(cursor.getInt(3));
        personne.setSexe(cursor.getString(4));
        return personne;
    }

    public List<Personne> getAllPersonneInDb() {
        List<Personne> personneLst = new ArrayList<Personne>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PERSONNE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Personne personne = cursorToPersonne(cursor);
            personneLst.add(personne);
            cursor.moveToNext();
        }

        cursor.close();

        return personneLst;
    }

    public Personne getPersonneInDb() {
        List<Personne> personneLst = getAllPersonneInDb();

        Personne joueur = null;
        for(Personne p : personneLst){
            if(0 == p.getId()){
                joueur = p;
            }
        }
        return joueur;
    }
}
