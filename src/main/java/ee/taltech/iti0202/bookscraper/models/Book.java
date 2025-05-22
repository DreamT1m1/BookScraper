package ee.taltech.iti0202.bookscraper.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @Column(name = "title")
    private String title;
    @Column(name = "language")
    private String language;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "url")
    private String url;
    @Column(name = "published_at")
    private String publishedAt;
    @Column(name = "pages")
    private Integer pages;
    @Column(name = "price")
    private Double price;
    @Column(name = "format")
    private String format;
    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    private Book(String title, Author author, String language, String publisher, String url, String publishedAt,
                Integer pages, Double price, String format, BookGenre genre) {
        this.title = title;
        this.author = author;
        this.language = language;
        this.publisher = publisher;
        this.url = url;
        this.publishedAt = publishedAt;
        this.pages = pages;
        this.price = price;
        this.format = format;
        this.genre = genre;
    }

    public Book() {
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, %s)",
                getAuthor().fullName(), getTitle(), getPublishedAt(), getGenre());
    }

    public void presentBook() {
        System.out.printf("""
                        \nBook title: %s
                        Book publisher: %s
                        Book author: %s
                        Book genre: %s
                        Book format: %s
                        Book language: %s
                        Book price: %.2f
                        """,
                getTitle(),
                getPublisher(),
                getAuthor().fullName(),
                getGenre(),
                getFormat(),
                getLanguage(),
                getPrice());
    }

    public static BookBuilder builder() {
        return new BookBuilder();
    }

    public static class BookBuilder {

        private String language;
        private String title;
        private String publisher;
        private Author author;
        private String url;
        private String publishedAt;
        private Integer pages;
        private Double price;
        private String format;
        private BookGenre genre;

        public BookBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public BookBuilder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public BookBuilder withPublisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public BookBuilder withAuthor(Author author) {
            this.author = author;
            return this;
        }

        public BookBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public BookBuilder withPublishingYear(String publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public BookBuilder withPages(int pages) {
            this.pages = pages;
            return this;
        }

        public BookBuilder withPrice(double price) {
            this.price = price;
            return this;
        }

        public BookBuilder withFormat(String format) {
            this.format = format;
            return this;
        }

        public BookBuilder withGenre(String genre) {
            this.genre = BookGenreParser.parse(genre);
            return this;
        }

        public Book build() {
            return new Book(
                    title,
                    author,
                    language,
                    publisher,
                    url,
                    publishedAt,
                    pages,
                    price,
                    format,
                    genre
            );
        }
    }
}
