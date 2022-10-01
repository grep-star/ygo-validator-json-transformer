class SetClassification {

    private static final String CORE = 'core'
    private static final String SIDE = 'side'
    private static final String DECK = 'deck'

    CenterValidContainer centerValid
    CenterNotValidContainer other
    List<String> suppress

    class CenterValidContainer {
        List<String> core
        List<String> side
    }

    class CenterNotValidContainer {
        List<String> core
        List<String> deck
        List<String> side
    }

    void classifySets(List<CardSet> sets, List<Card> cards) {
        centerValid.core.each { setName ->
            findSet(sets, setName).markCenterValid().type(CORE)
        }
        centerValid.side.each { setName ->
            findSet(sets, setName).markCenterValid().type(SIDE)
        }
        other.core.each { setName ->
            findSet(sets, setName).type(CORE)
        }
        other.deck.each { setName ->
            findSet(sets, setName).type(DECK)
        }
        other.side.each { setName ->
            findSet(sets, setName).type(SIDE)
        }
        cards.each { card ->
            if (card.cardSets != null) {
                card.cardSets.removeIf{ set ->
                    set.setName in suppress
                }
            }
        }
        sets.removeIf { set ->
            set.setName in suppress
        }
    }

    private static CardSet findSet(List<CardSet> sets, String name) {
        sets.find { candidate ->
            candidate.setName == name
        }
    }

}
