package library;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import library.db.Connector;
import library.db.model.Author;
import library.db.model.Book;
import library.db.model.Member;
import library.db.model.Publisher;
import library.db.model.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApllicationController {

	@Autowired
	Connector connector;

	@Autowired
	Book book;

	@Autowired
	Author author;

	@Autowired
	Publisher publisher;

	@Autowired
	Subject subject;

	@Autowired
	Member member;
	
	@Autowired
	MailConfiguration mailConfig;


	@RequestMapping("/library")
	public String home(Model model) throws SQLException {
		Connection connection = connector.getConnection();
		ArrayList<Book> allBooks = book.getAllBooks(connection);
		connector.closeConnection();
		model.addAttribute("books", allBooks);
		return "catalog";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String greetingForm(Model model) {
		model.addAttribute("member", new Member());
		return "addmember";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String newMember(@ModelAttribute Member memberModel, Model model) throws SQLException {
		memberModel.setNEW(member.getNEW());
		Connection conn = connector.getConnection();
		memberModel.create(conn);
		model.addAttribute("members", member.getAllMembers(conn));
		connector.closeConnection();
		return "members";
	}

	@RequestMapping(value = "/newbook", method = RequestMethod.GET)
	public String bookForm(Model model) {
		model.addAttribute("book", new Book());
		return "addbook";
	}

	@RequestMapping(value = "/newbook", method = RequestMethod.POST)
	public String newBook(@RequestParam(value="title", required = true) String title,
			@RequestParam(value="subject", required = true) Integer subjectId,
			@RequestParam(value="author", required = true) Integer authorId,
			@RequestParam(value="publisher", required = true) Integer publisherId,
			Model model) throws SQLException {
		Connection conn = connector.getConnection();
		Book newBook = new Book();
		newBook.BOOK_NEW = book.BOOK_NEW;
		newBook.title = title;
		newBook.subject = subject.getSubjectById(conn, subjectId);
		newBook.author = author.getAuthorById(conn, authorId);
		newBook.publisher = publisher.getPublisherById(conn, publisherId);
		newBook.create(conn);
		ArrayList<Book> allBooks = book.getAllBooks(conn);
		connector.closeConnection();
		model.addAttribute("books", allBooks);
		return "catalog";
	}

	@ModelAttribute("members")
	public List<Member> populateMembers() throws SQLException {
		List<Member> members = member.getAllMembers(connector.getConnection());
		connector.closeConnection();
		return members;
	}

	@ModelAttribute("authors")
	public List<Author> populateAuthors() throws SQLException {
		List<Author> authors = author.getAllAuthors(connector.getConnection());
		connector.closeConnection();
		return authors;
	}

	@ModelAttribute("publishers")
	public List<Publisher> populatePublishers() throws SQLException {
		List<Publisher> publishers = publisher.getAllPublishers(connector.getConnection());
		connector.closeConnection();
		return publishers;
	}

	@ModelAttribute("subjects")
	public List<Subject> populateSubjects() throws SQLException {
		List<Subject> subjects = subject.getAllSubjects(connector.getConnection());
		connector.closeConnection();
		return subjects;
	}

	@RequestMapping(value = "/issue", method = RequestMethod.GET)
	public String issueForm(@RequestParam(value="id", required = true) Integer bookId, Model model) throws SQLException {
		Book bookToIssue = book.getBookById(connector.getConnection(), bookId);
		model.addAttribute("book", bookToIssue);
		return "issue";
	}

	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public String issueBook(@RequestParam(value="member", required = true) Integer memberId, @RequestParam(value="id", required = true) Integer bookId, Model model) throws SQLException {
		Connection c = connector.getConnection();
		Book issuedBook = book.getBookById(c, bookId);
		issuedBook.ISSUE = book.ISSUE;
		Member selectedMember = member.getById(c, memberId);
		issuedBook.issue(c, selectedMember, mailConfig);
		ArrayList<Book> allBooks = book.getAllBooks(c);
		connector.closeConnection();
		model.addAttribute("books", allBooks);
		return "catalog";
	}

	@RequestMapping(value = "/return", method = RequestMethod.GET)
	public String returnBook(@RequestParam(value="id", required = true) Integer bookId, Model model) throws SQLException {
		Book returnedBook = book.getBookById(connector.getConnection(), bookId);
		connector.closeConnection();
		returnedBook.FINE_RATE = book.FINE_RATE;
		returnedBook.BORROW_TIME = book.BORROW_TIME;
		Long fine = returnedBook.calculateFine();
		returnedBook.fine = fine;
		model.addAttribute("book", returnedBook);
		return "return";
	}

	@RequestMapping(value = "/return", method = RequestMethod.POST)
	public String returnConfirm(@RequestParam(value="id", required = true) Integer bookId, Model model) throws SQLException {
		Connection connection = connector.getConnection();
		Book returnedBook = book.getBookById(connection, bookId);
		returnedBook.BOOK_RETURN = book.BOOK_RETURN;
		returnedBook.returnBook(connection);
		ArrayList<Book> allBooks = book.getAllBooks(connection);
		connector.closeConnection();
		model.addAttribute("books", allBooks);
		return "catalog";
	}
}
