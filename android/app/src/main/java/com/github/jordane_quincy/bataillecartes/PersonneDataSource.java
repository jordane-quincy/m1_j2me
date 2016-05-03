package com.github.jordane_quincy.bataillecartes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PersonneDataSource {

    // Champs de la base de données
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NOM, MySQLiteHelper.COLUMN_PRENOM };

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
        values.put(MySQLiteHelper.COLUMN_NOM, personne.getNom());
        values.put(MySQLiteHelper.COLUMN_PRENOM, personne.getPrenom());
        long insertId = database.insert(MySQLiteHelper.TABLE_PERSONNE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PERSONNE,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Personne newPersonne = cursorToPersonne(cursor);
        cursor.close();
        return newPersonne;
    }
/*
    public void deleteComment(Comment comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }
*/
    private Personne cursorToPersonne(Cursor cursor) {
        Personne personne = new Personne();
        personne.setId(cursor.getInt(0));
        personne.setNom(cursor.getString(1));
        personne.setPrenom(cursor.getString(2));
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

        return personneLst.isEmpty() ? null : personneLst.get(0);
    }
}
