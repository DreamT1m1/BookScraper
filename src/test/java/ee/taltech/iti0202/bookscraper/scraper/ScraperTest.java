package ee.taltech.iti0202.bookscraper.scraper;

import ee.taltech.iti0202.bookscraper.App;
import ee.taltech.iti0202.bookscraper.models.Author;
import ee.taltech.iti0202.bookscraper.models.Book;
import ee.taltech.iti0202.bookscraper.models.BookGenre;
import ee.taltech.iti0202.bookscraper.repository.AuthorBookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ScraperTest {

    private static AuthorBookRepository repository;
    private static Scraper scraper;
    private static App app;

    @BeforeAll
    static void setUp() {
       repository = Mockito.mock(AuthorBookRepository.class);
       scraper = new Scraper(repository);
       app = new App(scraper);
    }

    @Test
    void scraperGetsBookByUrlFromDataBase() {
        String url = "SomeUrl";
        Book book = Book.builder().withTitle("SomeTitle").withUrl(url).build();

        Mockito.when(repository.getBookByUrl(url)).thenReturn(Optional.of(book));

        assertEquals(
                book,
                scraper.getBookByUrl(url)
        );

        Mockito.verify(repository, Mockito.atLeastOnce()).getBookByUrl(url);
    }

    @Test
    void scraperGetsBookByUrlFromWeb() {
        String url = "https://www.apollo.ee/en/my-hero-academia-vol-40.html";
        Author author = Author.builder()
                .withFirstName("Kohei")
                .withLastName("Horikoshi")
                .build();
        Book book = Book.builder()
                .withTitle("My Hero Academia, Vol. 40")
                .withAuthor(author)
                        .build();

        Mockito.when(repository.getBookByUrl(url)).thenReturn(Optional.empty());

        assertEquals(
                book.getTitle(),
                scraper.getBookByUrl(url).getTitle()
        );

        Mockito.verify(repository, Mockito.atLeastOnce()).getBookByUrl(url);
    }

   @Test
   void scraperGetsBooksByGenre() {
       Book book = Book.builder()
               .withTitle("My Hero Academia, Vol. 40")
               .withGenre("Fiction")
               .build();

       Mockito.when(repository.getBooksByGenre(BookGenre.FICTION)).thenReturn(List.of(book));

       assertEquals(
               List.of(book),
               scraper.getBooksByGenre(BookGenre.FICTION)
       );

       Mockito.verify(repository, Mockito.atLeastOnce()).getBooksByGenre(BookGenre.FICTION);
   }

   @Test
   void scraperGetsBooksPageParallel() {
        String pageUrl = "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/ilukirjandus";
        Book book = Book.builder()
                .withTitle("Ruination: A League of Legends Novel")
                .withGenre("Fiction")
                .build();

       Mockito.when(repository.getCurrentPageForUrl(pageUrl)).thenReturn(1);

       assertEquals(
               book.getTitle(),
               scraper.getSingleBookPage(pageUrl, 1, 1, ProcessingType.PARALLEL).getFirst().getTitle()
       );

       Mockito.verify(repository, Mockito.atLeastOnce()).getCurrentPageForUrl(pageUrl);
   }

    @Test
    void scraperGetsBooksPageSynchronized() {
        String pageUrl = "https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/ilukirjandus";
        Book book = Book.builder()
                .withTitle("Ruination: A League of Legends Novel")
                .withGenre("Fiction")
                .build();

        Mockito.when(repository.getCurrentPageForUrl(pageUrl)).thenReturn(1);

        assertEquals(
                book.getTitle(),
                scraper.getSingleBookPage(pageUrl, 1, 1, ProcessingType.SYNCHRONIZED).getFirst().getTitle()
        );

        Mockito.verify(repository, Mockito.atLeastOnce()).getCurrentPageForUrl(pageUrl);
    }

   @Test
   void scraperGetsBooksByAuthor() {
        Author author = Author.builder()
                .withFirstName("Buronson")
                .withLastName("Tetsuo Hara")
                .withBooks(new ArrayList<>())
                .build();
        Book book = Book.builder()
                .withTitle("Fist of the North Star, Vol. 16")
                .build();
        author.addBook(book);

        Mockito.when(repository.getAuthorBooks(author)).thenReturn(List.of(book));

        assertEquals(
                List.of(book),
                scraper.getBooksByAuthor(author)
        );

       Mockito.verify(repository, Mockito.atLeastOnce()).getAuthorBooks(author);
   }

   @Test
   void scraperGetsAllBooks() {
       Book book = Book.builder()
               .withTitle("Fist of the North Star, Vol. 16")
               .build();

       Mockito.when(repository.getAllBooks()).thenReturn(List.of(book));

       assertEquals(
               List.of(book),
               scraper.getAllBooks()
       );

       Mockito.verify(repository, Mockito.atLeastOnce()).getAllBooks();
   }
}
