package com.said.dsbank.card;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/virtual-cards")
public class VirtualCardController {

    private final VirtualCardService cardService;

    public VirtualCardController(VirtualCardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Créer une carte virtuelle
     */
    @PostMapping("/create")
    public VirtualCard createCard(@RequestParam Long userId,
                                  @RequestParam Long accountId,
                                  @RequestParam BigDecimal balanceLimit) {
        return cardService.createCard(userId, accountId, balanceLimit);
    }

    /**
     * Récupérer toutes les cartes d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public List<VirtualCard> getUserCards(@PathVariable Long userId) {
        return cardService.getUserCards(userId);
    }

    /**
     * Bloquer une carte virtuelle
     */
    @PostMapping("/block/{cardId}")
    public void blockCard(@PathVariable Long cardId) {
        cardService.blockCard(cardId);
    }
}
