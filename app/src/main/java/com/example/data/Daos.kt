package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.AuditLog
import com.example.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY registrationDate DESC")
    fun getAllPeople(): Flow<List<Person>>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: Long): Person?

    @Query("SELECT * FROM people WHERE uniqueId = :uniqueId LIMIT 1")
    suspend fun getPersonByUniqueId(uniqueId: String): Person?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(people: List<Person>)

    @Update
    suspend fun updatePerson(person: Person)

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("SELECT COUNT(*) FROM people")
    suspend fun getPeopleCount(): Int

    @Query("SELECT uniqueId FROM people ORDER BY id DESC LIMIT 1")
    suspend fun getLatestUniqueId(): String?

    @Query("DELETE FROM people")
    suspend fun deleteAll()
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<AuditLog>)

    @Query("DELETE FROM audit_logs")
    suspend fun clearLogs()
}
