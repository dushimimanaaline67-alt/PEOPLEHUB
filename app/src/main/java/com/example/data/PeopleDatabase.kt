package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AuditLog
import com.example.model.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Person::class, AuditLog::class], version = 1, exportSchema = false)
abstract class PeopleDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: PeopleDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PeopleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PeopleDatabase::class.java,
                    "peoplehub_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PeopleDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PeopleDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.personDao(), database.auditLogDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.personDao().getPeopleCount() == 0) {
                            populateInitialData(database.personDao(), database.auditLogDao())
                        }
                    }
                }
            }

            suspend fun populateInitialData(personDao: PersonDao, auditLogDao: AuditLogDao) {
                personDao.insertAll(SampleDataProvider.samplePeople)
                auditLogDao.insertAll(SampleDataProvider.sampleLogs)
            }
        }
    }
}
