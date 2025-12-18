package com.said.dsbank.card;

import com.said.dsbank.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class VirtualCardService {

    private final VirtualCardRepository cardRepository;
    private final Random random = new Random();

    public VirtualCardService(VirtualCardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Création d'une carte virtuelle
     */
    public VirtualCard createCard(Long userId, Long accountId, BigDecimal balanceLimit) {
        VirtualCard card = VirtualCard.builder()
                .userId(userId)
                .accountId(accountId)
                .cardNumber(generateCardNumber())
                .cvv(generateCVV())
                .expiryDate(LocalDate.now().plusYears(2))
                .balanceLimit(balanceLimit)
                .status(VirtualCard.Status.ACTIVE)
                .build();

        return cardRepository.save(card);
    }

    /**
     * Trouver une carte par son ID
     */
    public Optional<VirtualCard> findById(Long cardId) {
        return cardRepository.findById(cardId);
    }

    /**
     * Récupérer toutes les cartes appartenant à un utilisateur
     */
    public List<VirtualCard> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    /**
     * Bloquer une carte virtuelle
     */
    public void blockCard(Long cardId) {
        VirtualCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Carte virtuelle non trouvée"));

        card.setStatus(VirtualCard.Status.BLOCKED);
        cardRepository.save(card);
    }

    /**
     * Générer un numéro de carte à 16 chiffres
     */
    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Générer un CVV à 3 chiffres
     */
    private String generateCVV() {
        int cvv = 100 + random.nextInt(900); // entre 100 et 999
        return String.valueOf(cvv);
    }
}
