package br.com.monitorfan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.monitorfan.data.local.dao.DuvidaDao
import br.com.monitorfan.data.local.dao.MonitoriaDao
import br.com.monitorfan.data.local.dao.RespostaDao
import br.com.monitorfan.data.local.dao.UsuarioDao
import br.com.monitorfan.data.local.entity.DuvidaEntity
import br.com.monitorfan.data.local.entity.MonitoriaEntity
import br.com.monitorfan.data.local.entity.RespostaEntity
import br.com.monitorfan.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        MonitoriaEntity::class,
        DuvidaEntity::class,
        RespostaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun monitoriaDao(): MonitoriaDao
    abstract fun duvidaDao(): DuvidaDao
    abstract fun respostaDao(): RespostaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN fotoUri TEXT")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "monitorfan.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
