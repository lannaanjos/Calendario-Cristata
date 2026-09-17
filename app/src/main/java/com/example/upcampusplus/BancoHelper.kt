import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context) :
    SQLiteOpenHelper(context, "campus.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
                    CREATE TABLE eventos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    data TEXT,
                    local TEXT,
                    descricao TEXT,
                    favorito INTEGER DEFAULT 0
                    )
                    """.trimIndent())
                        }
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) { }


    }