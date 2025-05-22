package ee.taltech.iti0202.bookscraper.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    /**
     * Constructor
     * @param firstName author's first name
     * @param lastName author's last name
     * @param books author's books
     */
    public Author(String firstName, String lastName, List<Book> books) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.books = books;
    }

    /**
     * Empty constructor
     */
    public Author() {
    }

    /**
     * Represent author's instance as string
     * @return string
     */
    @Override
    public String toString() {
        return "Author{"
                + "firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", books=" + books
                + '}';
    }

    /**
     * Get author's full name
     * @return author's full name
     */
    public String fullName() {
        return String.format("%s %s", firstName, lastName);
    }

    /**
     * Present the author
     */
    public void presentAuthor() {
        System.out.printf("Author's name: %s\nAuthor's surname: %s\nAuthor's book list: %s",
                getFirstName(), getLastName().isBlank() ? "" : getLastName(), getBooks());
    }

    /**
     * Add a book to author's books list
     * @param book the book to add
     */
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    /**
     * Author's instance builder
     * @return AuthorBuilder
     */
    public static AuthorBuilder builder() {
        return new AuthorBuilder();
    }

    public static class AuthorBuilder {
        private String firstName;
        private String lastName;
        private List<Book> books;

        public AuthorBuilder withFirstName(String name) {
            this.firstName = name;
            return this;
        }

        public AuthorBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AuthorBuilder withBooks(List<Book> books) {
            this.books = books;
            return this;
        }

        public Author build() {
            return new Author(firstName, lastName, books);
        }

    }
}
