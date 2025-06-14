package com.example.handremiapp.data.model

/**
 * يمثل الإجراءات المحتملة التي يمكن للاعب اتخاذها.
 * يحتاج إلى تحديد أنواع إجراءات أكثر تفصيلاً لكل لعبة.
 */
sealed class PlayerAction {
    // إجراءات هاند ريمي
    data class DrawAndDiscard(val cardToDiscard: Card) : PlayerAction()
    data class TakeDiscardAndDiscard(val takenCard: Card, val cardToDiscard: Card) : PlayerAction()
    data class Meld(val meld: List<Card>) : PlayerAction() // إنزال مجموعة أو تسلسل
    object GoOut : PlayerAction() // إنهاء اليد

    // إجراءات طرنيب
    data class Bid(val amount: Int, val trumpSuit: Suit?) : PlayerAction() // مزايدة
    data class PlayCardTarneeb(val card: Card) : PlayerAction()

    // إجراءات تركس
    data class ChooseKingdom(val kingdom: TrixKingdom) : PlayerAction() // اختيار مملكة
    data class PlayCardTrix(val card: Card) : PlayerAction()
}

// تعريف ممالك تركس كمثال
enum class TrixKingdom {
    KING_OF_HEARTS, QUEENS, DIAMONDS, SLAPS, TRIX
}

