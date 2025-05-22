package ee.taltech.iti0202.bookscraper.repository;

import ee.taltech.iti0202.bookscraper.models.Author;
import ee.taltech.iti0202.bookscraper.models.Book;
import ee.taltech.iti0202.bookscraper.models.BookGenre;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Optional;

public class AuthorBookRepository {

    private final SessionFactory sessionFactory;

    /**
     * Constructor
     */
    public AuthorBookRepository() {
        Configuration configuration = new Configuration().addAnnotatedClass(Author.class).addAnnotatedClass(Book.class);
        this.sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * Get all the books from database
     * @return list of books
     */
    public List<Book> getAllBooks() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            return session.createQuery("FROM Book", Book.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Get author from DB by his name and surname
     * @param name name
     * @param surname surname
     * @return optional of author
     */
    public Optional<Author> getAuthorByNameAndSurname(String name, String surname) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            return Optional.of(
                    session.createQuery(
                                    "FROM Author a WHERE a.firstName=:firstName AND a.lastName=:lastName",
                                    Author.class
                            )
                            .setParameter("firstName", name)
                            .setParameter("lastName", surname)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Get all the books of a provided author
     * @param author author
     * @return list of author's books
     */
    public List<Book> getAuthorBooks(Author author) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            return session.createQuery(
                    "FROM Book b join Author a ON b.author.id = a.id "
                            + "WHERE a.firstName=:name AND a.lastName=:lastName", Book.class)
                    .setParameter("name", author.getFirstName())
                    .setParameter("lastName", author.getLastName())
                    .getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Get a book by its url
     * @param url ur
     * @return optional of book
     */
    public Optional<Book> getBookByUrl(String url) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            return Optional.of(
                    session.createQuery("FROM Book b WHERE b.url=:url", Book.class)
                            .setParameter("url", url)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Add a book to an author with provided ID
     * @param authorId Author's ID
     * @param book book to add
     */
    public void addBookToAuthor(int authorId, Book book) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Author author = session.get(Author.class, authorId);
        author.addBook(book);

        session.persist(book);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Add an author to DB
     * @param author author to add
     */
    public void addAuthor(Author author) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        session.persist(author);

        session.getTransaction().commit();
        session.close();
    }

    /**
     * Get books list by genre
     * @param genre genre
     * @return list of books
     */
    public List<Book> getBooksByGenre(BookGenre genre) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            return session.createQuery("FROM Book b WHERE b.genre=:genre", Book.class)
                    .setParameter("genre", genre)
                    .getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Get current page for genre url and increase it by 1
     * @param url url
     * @return page
     */
    public int getCurrentPageForUrlAndIncrease(String url) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            boolean isFirstCall = innerAddUrl(url);
            if (!isFirstCall) {
                setCurrentPageForUrlPrivate(url, getCurrentPageForUrlPrivate(url) + 1);
            }

            return session.createNativeQuery("SELECT page FROM url_page WHERE url=?", Integer.class)
                    .setParameter(1, url)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Get current page for url
     * @param url url
     * @return page
     */
    public int getCurrentPageForUrl(String url) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            // innerAddUrl(url);
            return session.createNativeQuery("SELECT page FROM url_page WHERE url=?", Integer.class)
                    .setParameter(1, url)
                    .getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Private method to get current page for genre's url
     * @param url url
     * @return page
     */
    private int getCurrentPageForUrlPrivate(String url) {
        Session session = sessionFactory.getCurrentSession();

        try {
            return session.createNativeQuery("SELECT page FROM url_page WHERE url=?", Integer.class)
                    .setParameter(1, url)
                    .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Private method to set current page for genre's url. Doesn't open a new transaction. Helper method
     * @param url url
     * @param page page
     */
    private void setCurrentPageForUrlPrivate(String url, int page) {
        Session session = sessionFactory.getCurrentSession();

        try {
            session.createNativeQuery("UPDATE url_page SET page=? WHERE url=?", Void.class)
                    .setParameter(1, page)
                    .setParameter(2, url)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Private method to set current page for genre's url
     * @param url url
     * @param page page
     */
    public void setCurrentPageForUrl(String url, int page) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            session.createNativeQuery("UPDATE url_page SET page=? WHERE url=?", Void.class)
                    .setParameter(1, page)
                    .setParameter(2, url)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    /**
     * Add an url to database. Does not begin new transaction. Helper method
     * @param url url
     * @return true if and only if it's first call of the method otherwise false
     */
    private boolean innerAddUrl(String url) {
        Session session = sessionFactory.getCurrentSession();

        try {
            session.createNativeQuery(
                    "SELECT url FROM url_page WHERE url=?", String.class)
                    .setParameter(1, url)
                    .getSingleResult();
            return false;
        } catch (Exception e) {
            System.out.printf("Url %s does not exist in database. Adding...\n", url);
            session.createNativeQuery("INSERT INTO url_page VALUES(?, 1)", Void.class)
                    .setParameter(1, url)
                    .executeUpdate();
            return true;
        }
    }

    /**
     * Add url to database if it does not exist there yet
     * @param url url
     */
    public void addUrl(String url) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        try {
            session.createNativeQuery(
                            "SELECT url FROM url_page WHERE url=?", String.class)
                    .setParameter(1, url)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.printf("Url %s does not exist in database. Adding...\n", url);
            session.createNativeQuery("INSERT INTO url_page VALUES(?, 1)", Void.class)
                    .setParameter(1, url)
                    .executeUpdate();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

}
