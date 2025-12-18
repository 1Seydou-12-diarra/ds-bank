package com.said.dsbank.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VirtualCardRepository extends JpaRepository<VirtualCard, Long> {

    // Récupérer toutes les cartes d'un utilisateur
    List<VirtualCard> findByUserId(Long userId);

}
