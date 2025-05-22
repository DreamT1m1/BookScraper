package ee.taltech.iti0202.bookscraper;

import ee.taltech.iti0202.bookscraper.models.Book;
import ee.taltech.iti0202.bookscraper.models.Author;
import ee.taltech.iti0202.bookscraper.models.BookGenre;
import ee.taltech.iti0202.bookscraper.models.BookGenreParser;
import ee.taltech.iti0202.bookscraper.models.BookGenreUrlParser;
import ee.taltech.iti0202.bookscraper.repository.AuthorBookRepository;
import ee.taltech.iti0202.bookscraper.scraper.ProcessingType;
import ee.taltech.iti0202.bookscraper.scraper.Scraper;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {

    private final Scraper scraper;

    /**
     * Constructor
     * @param scraper scraper
     */
    public App(Scraper scraper) {
        this.scraper = scraper;
    }

    /**
     * Show book info
     * @param url url of the book
     */
    public void showBookInfo(String url) {
        Book book = scraper.getBookByUrl(url);

        if (book != null) {
            book.presentBook();
        } else {
            System.out.println("Error while processing provided url!");
        }
    }

    /**
     * Get books by genre from local database
     * @param bookGenre genre
     */
    public void showBooksByGenreLocal(BookGenre bookGenre) {
        List<Book> books = scraper.getBooksByGenre(bookGenre);
        if (!books.isEmpty()) {
            for (Book book : books) {
                book.presentBook();
                System.out.print("\n");
            }
            System.out.printf("%d books were shown from database\n\n", books.size());
        } else {
            System.out.printf("No books of genre %s are found in database", bookGenre);
        }
    }

    /**
     * Get books by genre from the web
     * @param genre genre
     */
    public void showBooksByGenre(BookGenre genre) {
        List<Book> books = scraper.getSingleBookPage(
                BookGenreUrlParser.getUrlByGenre(genre), 0, 0, ProcessingType.PARALLEL
        );
        if (books != null) {
            books.forEach(Book::presentBook);
            System.out.printf("%d books were shown\n\n", books.size());
        }
    }

    /**
     * Get books by genre from provided page with provided size
     * @param genre genre
     * @param page page number
     * @param size page size (books amount)
     */
    public void showBooksByGenre(BookGenre genre, int page, int size) {
        List<Book> books = scraper.getSingleBookPage(
                BookGenreUrlParser.getUrlByGenre(genre), page, size, ProcessingType.PARALLEL
        );
        if (books != null) {
            books.forEach(Book::presentBook);
            System.out.printf("%d books were shown\n\n", books.size());
        }
    }

    /**
     * Show all books of provided author
     * @param author author
     */
    public void showBooksByAuthor(Author author) {
        List<Book> books = scraper.getBooksByAuthor(author);
        if (books.isEmpty()) {
            System.out.println("No books found of provided author");
        } else {
            books.forEach(Book::presentBook);
        }
    }

    /**
     * Show all books from database
     */
    public void showAllBooks() {
        List<Book> books = scraper.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books in database yet");
        } else {
            for (Book book : books) {
                book.presentBook();
                System.out.print("\n");
            }
        }
    }

    /**
     * Main entry point
     */
    public static void mainEntry() {
        AuthorBookRepository repository = new AuthorBookRepository();
        Scraper scraper = new Scraper(repository);
        App app = new App(scraper);

        Scanner sc = new Scanner(System.in);

        outerLoop:
        while (true) {
            System.out.println("Choose an option:\n");
            System.out.println("0 - Exit the program");
            System.out.println("1 - Show book info by it's url");
            System.out.println("2 - Show books from database by genre");
            System.out.println("3 - Show books by genre from web");
            System.out.println("4 - Show books of an author");
            System.out.println("5 - Show all the books");
            System.out.println("6 - Show books page with exact amount");
            System.out.print("\n");

            int choice = -1;
            System.out.print("Waiting for Your input: ");

            do {
                try {
                    choice = sc.nextInt();
                    sc.nextLine();
                    break;
                } catch (Exception e) {
                    System.out.println("Wrong input!");
                    sc.nextLine();
                }
            } while (choice < 0);

            switch (choice) {
                case 0:
                    break outerLoop;
                case 1:
                    System.out.println("Please provide an url of book:");
                    String url = sc.nextLine();
                    System.out.print("\n");
                    app.showBookInfo(url);
                    break;
                case 2:
                    System.out.println("Please provide a genre:");
                    BookGenre genre1 = BookGenreParser.parse(sc.nextLine());
                    System.out.print("\n");
                    app.showBooksByGenreLocal(genre1);
                    break;
                case 3:
                    System.out.println("Please provide a genre:");
                    BookGenre genre2 = BookGenreParser.parse(sc.nextLine());
                    System.out.print("\n");
                    app.showBooksByGenre(genre2);
                    break;
                case 4:
                    System.out.println("Please enter an author's name and surname");
                    String[] nameAndLastName = sc.nextLine().split(" ");
                    Author author;
                    if (nameAndLastName.length == 1) {
                        author = Author.builder()
                                .withFirstName(nameAndLastName[0])
                                .withLastName("")
                                .build();
                    } else {
                        String firstName = nameAndLastName[0].trim();
                        String lastName = String.join(" ",
                                Arrays.stream(nameAndLastName).toList().subList(1, nameAndLastName.length)
                        ).trim();
                        author = Author.builder()
                                .withFirstName(firstName)
                                .withLastName(lastName)
                                .build();
                    }
                    System.out.print("\n");
                    app.showBooksByAuthor(author);
                    break;
                case 5:
                    app.showAllBooks();
                    break;
                case 6:
                    System.out.println("Please enter a genre, page and books amount to show");
                    String[] info = sc.nextLine().split(" ");
                    try {
                        BookGenre genre3 = BookGenreParser.parse(info[0]);
                        int page = Integer.parseInt(info[1]);
                        int amount = Integer.parseInt(info[2]);
                        app.showBooksByGenre(genre3, page, amount);
                        break;
                    } catch (Exception e) {
                        System.out.println("Wrong input!");
                        break;
                    }
                default:
                    System.out.println("No such option");
            }
        }
    }

    public static void main(String[] args) {
        mainEntry();
    }
}
