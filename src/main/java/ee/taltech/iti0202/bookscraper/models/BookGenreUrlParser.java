package ee.taltech.iti0202.bookscraper.models;

public class BookGenreUrlParser {

    /**
     * Parse genre to it's url
     * @param genre genre
     * @return url
     */
    public static String getUrlByGenre(BookGenre genre) {
        return switch (genre) {
            case HISTORY ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/ajalugu";
            case COMPUTERS_AND_INTERNET ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/arvuti-ja-internet";
            case ASTROLOGY_AND_SORCERY ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/astroloogia-ja-maagia";
            case ARCHITECTURE_AND_INTERIOR_DESIGN ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/arhitektuur-ja-sisekujundus";
            case BIOGRAPHY_AND_MEMOIRS ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/biograafia-ja-memuaarid";
            case ABOUT_ESTONIA ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/eestit-tutvustav";
            case THE_HUMANITIES ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/humanitaarteadused";
            case FICTION ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/ilukirjandus";
            case ECONOMICS_AND_LAW ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/majandus";
            case TEXTBOOKS_AND_EDUCATION ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/opikud";
            case LANGUAGES_AND_DICTIONARIES ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/keeled";
            case HOUSE_AND_GARDEN ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/kodu-ja-aed";
            case COOKING ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/kokandus";
            case ART_AND_MUSIC ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/kunst-ja-muusika";
            case THEATRE_AND_CINEMA ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/teater-ja-kino";
            case CHILDREN_LITERATURE ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/lastekirjandus";
            case YOUNG_ADULT ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/noortele";
            case NATURAL_SCIENCES ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/loodusteadused";
            case ENTERTAINMENT ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/meelelahutus";
            case PSYCHOLOGY ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/psuhholoogia";
            case TRAVELLING_AND_TOURISM ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/reisimine-ja-turism";
            case RELIGION_AND_MYTHOLOGY ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/religioon-ja-mutoloogia";
            case RELATIONSHIPS_AND_FAMILY ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/suhted-ja-perekond";
            case REFERENCE_BOOKS ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/teatmeteosed";
            case HEALTH ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/tervis";
            case SOCIETY_AND_POLITICS ->
                    "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/uhiskond-ja-poliitika";
            default -> "";
        };
    }
}
