package altamir.android.santander.com.br.bankapp.BDSqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

//Criando rotinas p/ gravar e consultar o banco de dados SQL Lite
public class DbConecxao {

    private static DbConecxao gw;
    private SQLiteDatabase db;

    private DbConecxao(Context ctx){
        DbAjuda helper = new DbAjuda(ctx);
        db = helper.getWritableDatabase();
    }

    public static DbConecxao getInstance(Context ctx){
        if(gw == null)
            gw = new DbConecxao(ctx);
        return gw;
    }

    public SQLiteDatabase getDatabase(){
        return this.db;
    }
}