package com.example.practicalab9.repository;

import com.example.practicalab9.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player,Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM players where region=?1 ORDER BY position ASC")
    List<Player> listarLeaderbordXregion(String region);
    @Query(nativeQuery = true, value = "SELECT * FROM players where region=?1 ORDER BY mmr DESC")
    List<Player> findByRegionOrderByMmrDesc(String region);
}
