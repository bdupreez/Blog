package net.briandupreez.blog.ml.nlp;

/**
 * Parts of Speech
 * Created by Brian on 2013/12/07.
 * <p/>
 * Penn Treebank Tagset
 * Used by the Stanford NLP lib
 */
public enum PartsOfSpeech {

    CC(0, "CC", "Coordinating conjunction e.g. and"),
    CD(1, "CD", "Cardinal Number e.g 1, Third"),
    DT(2, "DT", "Determiner e.g. the "),
    EX(3, "EX", "Existential e.g. there"),
    FW(4, "FW", "Foreign Word e.g. d'hoevre"),
    IN(5, "IN", "Preposition or subordinating conjunction e.g. in, of, like"),
    JJ(6, "JJ", "Adjective e.g. big"),
    JJR(7, "JJR", "Adjective, comparative e.g. bigger"),
    JJS(8, "JJS", "Adjective, superlative e.g. biggest"),
    LS(9, "LS", "List Item Marker e.g. 1)"),
    MD(10, "MD", "Modal  e.g. can, could, might, may..."),
    NN(11, "NN", "Noun, singular or mass e.g. door"),
    NNP(12, "NNP", "Proper Noun, singular e.g. Brian"),
    NNPS(13, "NNPS", " Proper Noun, plural e.g. Vikings"),
    NNS(14, "NNS", "Noun, plural e.g. doors"),
    PDT(15, "PDT", "Predeterminer  e.g. all, both ... when they precede an article"),
    POS(16, "POS", "Possessive Ending  e.g. Nouns ending in 's"),
    PRP(17, "PRP", "Personal Pronoun  e.g. I, me, you, he..."),
    PRP$(18, "PRP$", "Possessive Pronoun  e.g. my, your, mine, yours..."),
    RB(19, "RB", "Adverb Most words that end in -ly as well as degree words like quite, too and very"),
    RBR(20, "RBR", "Adverb, comparative  Adverbs with the comparative ending -er, with a strictly comparative meaning. e.g. Better"),
    RBS(21, "RBS", "Adverb, superlative e.g. best"),
    RP(22, "RP", "Particle e.g. give up "),
    SYM(23, "SYM", "Symbol Should be used for mathematical, scientific or technical symbols"),
    TO(24, "TO", "to "),
    UH(25, "UH", "Interjection e.g. uh, well, yes, my..."),
    VB(26, "VB", "Verb, base form subsumes imperatives, infinitives and subjunctives e.g. take"),
    VBD(27, "VBD", "Verb, past tense includes the conditional form of the verb to be e.g. took"),
    VBG(28, "VBG", "Verb, gerund or present participle e.g. taking"),
    VBN(29, "VBN", "Verb, past participle e.g. taken"),
    VBP(30, "VBP", "Verb, non-3rd person singular present e.g. take"),
    VBZ(31, "VBZ", "Verb, 3rd person singular present e.g. takes"),
    WDT(32, "WDT", "Wh-determiner e.g. which, and that when it is used as a relative pronoun"),
    WP(33, "WP", "Wh-pronoun e.g. what, who, whom..."),
    WP$(34, "WP$", "Possessive wh-pronoun e.g. whose"),
    WRB(35, "WRB", "Wh-adverb e.g. how, where why"),

    // punctuation
    HASH(36, "#", "hashtag", true),
    DOLLAR(37, "$", "Dollar", true),
    QUOTE(38, "''", "Quote", true),
    OPEN_BRACE(39, "(", "Open Bracket", true),
    CLOSE_BRACE(40, ")", "Close Bracket", true),
    COMMA(41, ",", "Comma", true),
    FULL_STOP(42, ".", "Full stop, period", true),
    COLON(43, ":", "Colon", true),
    ODD_QUOTE(44, "``", "Unsure", true),

    //Extra
    LRB(45, "-LRB-", "LRB"),
    RRB(46, "-RRB-", "RRB");

    private final int id;
    private final String description;
    private final String symbol;
    private boolean isPunctuation = false;

    /**
     * Constructor
     *
     * @param id          the id
     * @param symbol      the symbol
     * @param description the description
     */
    private PartsOfSpeech(final int id, final String symbol, final String description) {
        this.id = id;
        this.symbol = symbol;
        this.description = description;
    }

    /**
     * Punctuation Constructor
     *
     * @param id          the id
     * @param description the description
     */
    private PartsOfSpeech(final int id, final String symbol, final String description, final boolean isPunctuation) {
        this.id = id;
        this.symbol = symbol;
        this.isPunctuation = isPunctuation;
        this.description = description;
    }

    /**
     * The id
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPunctuation() {
        return isPunctuation;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns  the Symbol for the category id
     *
     * @param id the id
     * @return the symbol
     */
    public static String getSymbolForId(final int id) {
        for (final PartsOfSpeech v : values()) {
            if (v.getId() == id) {
                return v.getSymbol();
            }
        }
        throw new IllegalArgumentException("Invalid POS id");
    }

    /**
     * Returns  the Symbol for the category id
     *
     * @param id the id
     * @return the symbol
     */
    public static PartsOfSpeech getPartOfSpeechForId(final int id) {
        for (final PartsOfSpeech v : values()) {
            if (v.getId() == id) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid POS id [" + id + "]");
    }

    /**
     * Get the Id for the Symbol
     *
     * @param symbol the symbol
     * @return the category
     */
    public static int getIdForSymbol(final String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Null POS Symbol");
        }

        for (final PartsOfSpeech v : values()) {
            if (v.getSymbol().equals(symbol)) {
                return v.getId();
            }
        }
        throw new IllegalArgumentException("Invalid POS Symbol");
    }

}
