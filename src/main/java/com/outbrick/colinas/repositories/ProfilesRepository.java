package com.outbrick.colinas.repositories;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.outbrick.colinas.entities.Profiles;

public interface ProfilesRepository extends JpaRepository<Profiles, Long>{
    
    @Query(value = "SELECT * FROM PROFILES ORDER BY PROFILE_ID DESC LIMIT 1", nativeQuery = true)
    public Profiles getLastProfileAdded();

    @Query(value = "SELECT * FROM PROFILES WHERE PROFILE_EMAIL = :EMAIL", nativeQuery = true)
    public Profiles existsByProfileEmail(@Param(value = "EMAIL") String profileEmail);

    @Query(value = "SELECT * FROM PROFILES WHERE PROFILE_EMAIL = :EMAIL", nativeQuery = true)
    public Optional<Profiles> getProfileByEmail(@Param(value = "EMAIL") String profileEmail);


    @Query(value = "SELECT * FROM PROFILES WHERE PROFILE_TAG = :TAG", nativeQuery = true)
    public Optional<Profiles> findProfileByTag(@Param(value = "TAG") String profileTag);

    @Query(value = "SELECT * FROM PROFILES WHERE PROFILE_EMAIL = :EMAIL AND PROFILE_TAG <> :TAG", nativeQuery = true)
    public Profiles existingProfileBeforeUpdate(@Param(value = "EMAIL") String profileEmail, @Param(value = "TAG") String profileTag);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PROFILES WHERE PROFILE_TAG = :TAG", nativeQuery = true)
    public void deleteByTag(@Param(value = "TAG") String profileTag);
}
