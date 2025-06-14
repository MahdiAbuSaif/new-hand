package com.example.handremiapp.data.model

/**
 * يمثل حالة اللعبة الحالية.
 * يحتاج إلى تفاصيل أكثر بناءً على اللعبة المحددة.
 */
data class GameState(
    val players: List<Player>,
    val currentPlayerIndex: Int,
    val deck: List<Card>,
    val discardPileTopCard: Card?,
    // ... حقول أخرى خاصة بحالة اللعبة (مثل النقاط، نوع الطرنيب، المملكة الحالية في تركس)
)

data class Player(
    val id: String,
    val name: String,
    val hand: List<Card>,
    val score: Int,
    val isAI: Boolean
)

