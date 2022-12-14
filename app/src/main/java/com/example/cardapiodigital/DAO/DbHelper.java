package com.example.cardapiodigital.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "DB_MENY";

    public static final String TABELA_EMPRESA = "empresa";
    public static final String TABELA_ITEM_PEDIDO = "item_pedido";

    public static final String COLUNA_ID = "id";
    public static final String COLUNA_ID_FIREBASE = "id_firebase";
    public static final String COLUNA_NOME = "nome";
    public static final String COLUNA_VALOR = "valor";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String COLUNA_URL_IMAGEM = "url_imagem";
    public static final String COLUNA_QUANTIDADE = "quantidade";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String TABLE_EMPRESA = " CREATE TABLE IF NOT EXISTS " + TABELA_EMPRESA
                + " (id_firebase TEXT NOT NULL, " +
                " nome TEXT NOT NULL, " +
                " descricao TEXT NOT NULL," +
                " url_imagem TEXT NOT NULL); ";

        String TABLE_ITEM_PEDIDO = " CREATE TABLE IF NOT EXISTS " + TABELA_ITEM_PEDIDO
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " id_firebase TEXT NOT NULL, " +
                " nome TEXT NOT NULL, " +
                " url_imagem TEXT NOT NULL, " +
                " valor DOUBLE NOT NULL, " +
                " quantidade INTEGER NOT NULL); ";

        try {
            db.execSQL(TABLE_EMPRESA);
            db.execSQL(TABLE_ITEM_PEDIDO);
            Log.i("INFO_DB", "onCreate: Tabela criada com sucesso.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao criar tabela.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
