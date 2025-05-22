package ee.taltech.iti0202.bookscraper.scraper;

import ee.taltech.iti0202.bookscraper.models.Author;
import ee.taltech.iti0202.bookscraper.models.Book;
import ee.taltech.iti0202.bookscraper.models.BookGenre;
import ee.taltech.iti0202.bookscraper.repository.AuthorBookRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.Objects;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {

    private final AuthorBookRepository repository;

    /**
     * Constructor
     * @param repository repository
     */
    public Scraper(AuthorBookRepository repository) {
        this.repository = repository;

    }

    /**
     * Get document (HTML) of provided url
     * @param url url
     * @return document
     */
    private Document getDocumentFromUrl(String url) {
        try {
            return Jsoup.parse(new URI(url).toURL().openStream(), "UTF-8", url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get book main info as HTML from a document
     * @param bookHtml document got with getDocumentFromUrl
     * @return element
     */
    private Element getBookMainInfo(Document bookHtml) {
        return bookHtml.getElementsByAttributeValue("aria-label", "Product Section").getFirst();
    }

    /**
     * Helper method to parse book price
     * @param string string price
     * @return double price
     */
    private double parseDouble(String string) {
        String pattern = "(\\d+)(,\\d+)?";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);

        if (m.find()) {
            return Double.parseDouble(m.group(0).replace(",", "."));
        }
        return -1;
    }

    /**
     * Helper method to create book instance from html
     * @param bookSection html code
     * @return new book
     */
    private Book createBookFromHtmlElement(Element bookSection) {
        Elements title = bookSection.getElementsByClass("styles_heading__title__Xym7A");
        Element publisher = bookSection.getElementById("meta-publisher");
        Element format = bookSection.getElementById("meta-format");
        Element language = bookSection.getElementById("meta-language");
        Element publishedAt = bookSection.getElementById("meta-published_at");
        Element pages = bookSection.getElementById("meta-pages");
        Elements price = bookSection.getElementsByClass("styles_product-price-value__Ai9c8");
        Element genre = bookSection.selectFirst(".styles_breadcrumbs__NarXt ol li:nth-of-type(4)");

        return Book.builder()
                .withTitle(!title.isEmpty() ? title.getFirst().text() : "")
                .withFormat(format != null ? format.text() : "")
                .withLanguage(language != null ? language.text() : "")
                .withPages(pages != null ? Integer.parseInt(pages.text()) : -1)
                .withPrice(!price.isEmpty() ? parseDouble(price.text()) : -1)
                .withPublisher(publisher != null ? publisher.text() : "")
                .withPublishingYear(publishedAt != null ? publishedAt.text() : "")
                .withGenre(genre != null ? genre.text() : "")
                .build();
    }

    /**
     * Create book instance from it's url
     * @param bookUrl url
     * @return book instance
     */
    private Book createBookFromUrl(String bookUrl) {

        // If book wasn't found in database
        Document document = getDocumentFromUrl(bookUrl);

        if (document == null) {
            return null;
        }

        Element bookSection = getBookMainInfo(document);

        // Build a book

        Book realBook = createBookFromHtmlElement(bookSection);
        realBook.setUrl(bookUrl);

        // Build an author

        Element authorNameElement = bookSection.getElementById("meta-author");
        String[] authorName;
        if (authorNameElement != null) {
            authorName = authorNameElement.text().split("[, ]");
        } else {
            authorName = new String[]{""};
        }

        String firstName, lastName;
        if (authorName.length == 1) {
            firstName = authorName[0].trim();
            lastName = "";
        } else {
            firstName = authorName[0].trim();
            lastName = String.join(" ",
                    Arrays.stream(authorName).toList().subList(1, authorName.length)
            ).trim();
        }

        Optional<Author> authorFromDB = repository.getAuthorByNameAndSurname(firstName, lastName);

        if (authorFromDB.isPresent()) {
            repository.addBookToAuthor(authorFromDB.get().getId(), realBook);
        } else {
            Author newAuthor = Author.builder()
                    .withFirstName(firstName)
                    .withLastName(lastName)
                    .withBooks(new ArrayList<>())
                    .build();
            repository.addAuthor(newAuthor);
            repository.addBookToAuthor(newAuthor.getId(), realBook);
        }
        return realBook;
    }

    /**
     * Get book by url
     * A book from database will be returned, if exists
     * Otherwise the book will be created with createBookFromUrl method
     * @param url url
     * @return book instance
     */
    public Book getBookByUrl(String url) {
        Optional<Book> book = repository.getBookByUrl(url);

        if (book.isPresent()) {
            System.out.println("Book taken from database");
            return book.get();
        }

        return createBookFromUrl(url);
    }

    /**
     * Get books list by genre
     * @param bookGenre genre
     * @return list of books
     */
    public List<Book> getBooksByGenre(BookGenre bookGenre) {
        return repository.getBooksByGenre(bookGenre);
    }

    /**
     * Get list of books from a provided url.
     * Url must be like 'https://www.apollo.ee/en/raamatud/ingliskeelsed-raamatud/{genre}'
     * Increases page with every successful method call
     * @param pageUrl url
     * @param page page number
     * @param size books amount to get
     * @param processingType either synchronized (slower) or parallel (faster)
     * @return list of books
     */
    public List<Book> getSingleBookPage(String pageUrl, int page, int size, ProcessingType processingType) {
        int oldPageValue = repository.getCurrentPageForUrl(pageUrl);
        int currentPage = page == 0 ? repository.getCurrentPageForUrlAndIncrease(pageUrl) : page;
        String finalUrl = pageUrl + "?page=" + currentPage;

        // If provided page is not next to the existing one in database, don't increase current page
        if (page != 0) {
            repository.addUrl(pageUrl);
            if (page - repository.getCurrentPageForUrl(pageUrl) == 1) {
                repository.setCurrentPageForUrl(pageUrl, page);
            }
        }

        Document pageDocument = getDocumentFromUrl(finalUrl);
        System.out.printf("Loading %d page\n", currentPage);
        if (pageDocument == null) {
            System.out.printf("Error while loading url %s", finalUrl);
            return null;
        }

        // Get list of books on the page
        Element listOfBooks = pageDocument.getElementsByClass("product-list styles_product-list__vruLH")
                .first();
        if (listOfBooks == null) {
            System.out.printf("Url with page %d doesn't have any book\n", currentPage);
            repository.setCurrentPageForUrl(
                    pageUrl,
                    oldPageValue
            );
            return null;
        }

        // Calculate how many books to show
        List<Element> allBooks = listOfBooks.getElementsByClass("styles_product-list__item__rjWBQ").asList();
        List<Element> books;

        if (page > 0 && size > 0) {
            books = size < allBooks.size() ? allBooks.subList(0, size) : allBooks;
        } else {
            books = allBooks;
        }

        return switch (processingType) {
            case SYNCHRONIZED -> processBookElementsListSynchronized(books);
            case PARALLEL -> processBookElementsListParallel(books);
        };
    }

    /**
     * Process received books elements list parallel (faster, asynchronous)
     * @param books books
     * @return list of books
     */
    private List<Book> processBookElementsListParallel(List<Element> books) {
        return books.parallelStream()
                .map(bookHtml -> {
                    Element bookUrlElement = bookHtml.getElementsByTag("a").first();
                    if (bookUrlElement != null) {
                        String bookUrl = "https://www.apollo.ee" + bookUrlElement.attr("href");
                        return getBookByUrl(bookUrl);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Process received books elements list synchronized (slower, synchronized)
     * @param books books
     * @return books list
     */
    private List<Book> processBookElementsListSynchronized(List<Element> books) {
        List<Book> booksFromPage = new ArrayList<>();

        int booksProcessed = 0;

        for (Element bookHtml : books) {
            Element bookUrlElement = bookHtml.getElementsByTag("a").first();
            if (bookUrlElement != null) {
                String bookUrl = "https://www.apollo.ee/"
                        + bookUrlElement.attr("href");
                booksFromPage.add(getBookByUrl(bookUrl));
                System.out.printf("Books processed: %d\n", ++booksProcessed);
            }
        }
        return booksFromPage;
    }

    /**
     * Get all books of provided author
     * @param author author
     * @return list of author's books
     */
    public List<Book> getBooksByAuthor(Author author) {
        return repository.getAuthorBooks(author);
    }

    /**
     * Get all books from database
     * @return list of books
     */
    public List<Book> getAllBooks() {
        return repository.getAllBooks();
    }

}
