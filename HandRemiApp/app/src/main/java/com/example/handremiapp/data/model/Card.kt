package com.example.handremiapp.data.model

/**
 * يمثل ورقة لعب قياسية.
 */
data class Card(val suit: Suit, val rank: Rank)

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
}

