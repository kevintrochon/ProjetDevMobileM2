package nc.unc.ktrochon.festivalnotification.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASENAME = "festival-notification";
    private static final int DATABASE_VERSION = 1;

    public DatabaseManager(Context context) {
        super(context, DATABASENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String strSQL = "CREATE TABLE Favori ("
                        + "idFavori integer primary key autoincrement,"
                        + "name TEXT not null,"
                        + "time TEXT not null,"
                        + "heure TEXT not null,"
                        + "jours TEXT not null,"
                        + "isFavory INTEGER"
                        + ")";
        sqLiteDatabase.execSQL(strSQL);
        Log.i("DatabaseMAnager :","Creation de la base de donnees.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String strSql = "drop table Favori";
        sqLiteDatabase.execSQL(strSql);
        this.onCreate(sqLiteDatabase);
    }
}
