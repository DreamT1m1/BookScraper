package ee.taltech.iti0202.bookscraper.models;

public class BookGenreParser {

    /**
     * Parse read from html genre's name to an enum
     * @param genre genre
     * @return genre's enum
     */
    public static BookGenre parse(String genre) {
        return switch (genre) {
            case "Fiction" -> BookGenre.FICTION;
            case "History" -> BookGenre.HISTORY;
            case "Computers and the Internet" -> BookGenre.COMPUTERS_AND_INTERNET;
            case "Astrology and Sorcery" -> BookGenre.ASTROLOGY_AND_SORCERY;
            case "Architecture and Interior Design" -> BookGenre.ARCHITECTURE_AND_INTERIOR_DESIGN;
            case "Biography and Memoirs" -> BookGenre.BIOGRAPHY_AND_MEMOIRS;
            case "About Estonia" -> BookGenre.ABOUT_ESTONIA;
            case "The Humanities" -> BookGenre.THE_HUMANITIES;
            case "Economics and Law" -> BookGenre.ECONOMICS_AND_LAW;
            case "Textbooks and Education" -> BookGenre.TEXTBOOKS_AND_EDUCATION;
            case "Languages and Dictionaries" -> BookGenre.LANGUAGES_AND_DICTIONARIES;
            case "House and Garden" -> BookGenre.HOUSE_AND_GARDEN;
            case "Cooking" -> BookGenre.COOKING;
            case "Art and Music" -> BookGenre.ART_AND_MUSIC;
            case "Theatre and Cinema" -> BookGenre.THEATRE_AND_CINEMA;
            case "Children's Literature" -> BookGenre.CHILDREN_LITERATURE;
            case "Young Adult" -> BookGenre.YOUNG_ADULT;
            case "Natural Sciences" -> BookGenre.NATURAL_SCIENCES;
            case "Entertainment" -> BookGenre.ENTERTAINMENT;
            case "Psychology" -> BookGenre.PSYCHOLOGY;
            case "Travelling and Tourism" -> BookGenre.TRAVELLING_AND_TOURISM;
            case "Religion and Mythology" -> BookGenre.RELIGION_AND_MYTHOLOGY;
            case "Relationships and family" -> BookGenre.RELATIONSHIPS_AND_FAMILY;
            case "Reference books" -> BookGenre.REFERENCE_BOOKS;
            case "Health" -> BookGenre.HEALTH;
            case "Society and Politics" -> BookGenre.SOCIETY_AND_POLITICS;
            default -> BookGenre.UNKNOWN_GENRE;
        };
    }
}
