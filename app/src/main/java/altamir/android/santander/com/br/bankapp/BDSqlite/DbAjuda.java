package altamir.android.santander.com.br.bankapp.BDSqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Criando rotinas p/ gravar e consultar o banco de dados SQL Lite
public class DbAjuda extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Bankapp.db";
    private static final int DATABASE_VERSION = 1;

    public DbAjuda(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Ultimologin (ID INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}