package library.db.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.mail.internet.MimeMessage;

import library.MailConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sql.properties")
public class Book {
	Integer id;
	public String title;
	
	@Autowired
	public Author author;
	
	@Autowired
	public Publisher publisher;
	
	@Autowired
	public Subject subject;
	
	@Autowired
	Member member;
	
	public Member getMember() {
		return member;
	}

	public void setMember(Member issuedTo) {
		this.member = issuedTo;
	}

	@Value("${book.getall}")
	public String ALL;
	
	@Value("${book.getbyid}")
	public String BY_ID;

	@Value("${book.issue}")
	public String ISSUE;
	
	@Value("${book.borrowtime}")
	public String BORROW_TIME;
	
	@Value("${book.finerate}")
	public String FINE_RATE;
	
	@Value("${book.return}")
	public String BOOK_RETURN;
	
	@Value("${book.new}")
	public String BOOK_NEW;

	public Long issueDate;
	
	public Long fine;
	public Long delay = 0L;

	public Book() {}
	
	public void create(Connection connection) throws SQLException {
		PreparedStatement stmntNew = connection.prepareStatement(BOOK_NEW, Statement.RETURN_GENERATED_KEYS);
		stmntNew.setString(1, this.title);
		stmntNew.setInt(2, this.publisher.id);
		stmntNew.setInt(3, this.author.id);
		stmntNew.setInt(4, this.subject.id);
		stmntNew.executeUpdate();
	}
	
	public Book(Integer id, String title, Author author, Publisher publisher, Subject subject, 
			Member member, Long issueDate) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.subject = subject;
		this.member = member;
		this.issueDate = issueDate;
	}
	
	public Book(Integer id, String title) {
		super();
		this.id = id;
		this.title = title;
		this.author = null;
		this.publisher = null;
		this.subject = null;
	}
	
	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Author getAuthor() {
		return author;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public Subject getSubject() {
		return subject;
	}

	public ArrayList<Book> getAllBooks(Connection connection) throws SQLException {
		PreparedStatement stmntGetAll = connection.prepareStatement(ALL);
		ResultSet rs = stmntGetAll.executeQuery();
		ArrayList<Book> books = new ArrayList<Book>();
		while(rs.next()) {
			Integer id = rs.getInt("id");
			String title = rs.getString("title");
			Author bookAuthor = author.getAuthorById(connection, rs.getInt("author"));
			Publisher bookPublisher = publisher.getPublisherById(connection, rs.getInt("publisher"));
			Subject bookSubject = subject.getSubjectById(connection, rs.getInt("subject"));
			Member bookIssuedTo = member.getById(connection, rs.getInt("issuedTo"));
			Long issueDate = rs.getLong("issuedate");
			Book book = new Book(id, title, bookAuthor, bookPublisher, bookSubject, bookIssuedTo, issueDate);
			books.add(book);
		}
		return books;
	}

	public Book getBookById(Connection connection, Integer id) throws SQLException {
		PreparedStatement stmntGetById = connection.prepareStatement(BY_ID);
		stmntGetById.setInt(1, id);
		ResultSet rs = stmntGetById.executeQuery();
		Book book = null;
		while(rs.next()) {
			Integer ida = rs.getInt("id");
			String title = rs.getString("title");
			Author bookAuthor = author.getAuthorById(connection, rs.getInt("author"));
			Publisher bookPublisher = publisher.getPublisherById(connection, rs.getInt("publisher"));
			Subject bookSubject = subject.getSubjectById(connection, rs.getInt("subject"));
			Member bookIssuedTo = member.getById(connection, rs.getInt("issuedTo"));
			Long issueDate = rs.getLong("issuedate");
			book = new Book(ida, title, bookAuthor, bookPublisher, bookSubject, bookIssuedTo, issueDate);
		}
		return book;
	}
	
	public void issue(Connection connection, Member member, MailConfiguration mailConfig) throws SQLException {
		PreparedStatement stmtIssue = connection.prepareStatement(ISSUE);
		stmtIssue.setInt(1, member.id);
		stmtIssue.setLong(2, System.currentTimeMillis() / 1000L);
		stmtIssue.setInt(3, this.id);
		stmtIssue.execute();
		String body = "";
		body += "Hi " + member.firstname + ",\n";
		body += "Book: " + this.title + " has been issued against your name.";
		body += " Enjoy your book and return it in time. \n Thanks, Bookworm Library Team";
		sendMail(member, "Book issued", body, mailConfig);
	}

	public Long calculateFine() throws SQLException {
		long grace = Long.parseLong(BORROW_TIME);
		long fineRate = Long.parseLong(FINE_RATE);
		long now = System.currentTimeMillis() / 1000L;
		long outFor = now - this.issueDate;
		
		if(outFor > grace) {
		this.delay  = (outFor -grace) / 60 ;
		return fineRate * (outFor - grace) / 60;
		}
		else
			return 0L;
	}

	public void returnBook(Connection connection) throws SQLException {
		PreparedStatement stmntGetById = connection.prepareStatement(BOOK_RETURN);
		stmntGetById.setInt(1, id);
		stmntGetById.execute();
	}
	
	private void sendMail(final Member mailTo, final String subject, final String body, MailConfiguration mailConfig) {
		JavaMailSender mailSender = mailConfig.javaMailService();
		try {
            mailSender.send(new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage)
                throws Exception {
                   MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                   message.addTo(mailTo.email);
                   message.setSubject(subject);
                   message.setText(body);
                 }
             });
     } catch (MailSendException e) {
        // your codes
         e.printStackTrace();
     }
    }

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + ", publisher=" + publisher + ", subject="
				+ subject + ", member=" + member + "]";
	}

	
}
