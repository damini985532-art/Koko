package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TeamMemberEntity
import com.example.data.model.TeamProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM team_profiles LIMIT 1")
    fun getTeamProfile(): Flow<TeamProfileEntity?>

    @Query("SELECT * FROM team_profiles LIMIT 1")
    suspend fun getTeamProfileOnce(): TeamProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTeamProfile(profile: TeamProfileEntity)

    @Query("DELETE FROM team_profiles")
    suspend fun clearTeamProfile()

    @Query("SELECT * FROM team_members WHERE teamId = :teamId ORDER BY joinedAt DESC")
    fun getTeamMembers(teamId: String): Flow<List<TeamMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: TeamMemberEntity)

    @Update
    suspend fun updateMember(member: TeamMemberEntity)

    @Query("UPDATE team_members SET isRevoked = :isRevoked WHERE memberId = :memberId")
    suspend fun setMemberRevocation(memberId: String, isRevoked: Boolean)

    @Query("DELETE FROM team_members WHERE memberId = :memberId")
    suspend fun deleteMember(memberId: String)
}
