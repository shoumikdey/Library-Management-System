package library.db.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sql.properties")
public class Author {
	Integer id;
	String fname;
	String lname;
	

	@Value("${author.getall}")
	private String ALL;
	
	@Value("${author.getbyid}")
	private String BY_ID;

	@Value("${author.getbyfirst}")
	private String BY_FIRST;

	@Value("${author.getbylast}")
	private String BY_LAST;

	@Value("${author.getbyname}")
	private String BY_NAME;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	@Override
	public String toString() {
		return "Author [id=" + id + ", fname=" + fname + ", lname=" + lname + "]";
	}

	public Author() {
	}
	
	public Author(Integer id, String fname, String lname) {
		super();
		this.id = id;
		this.fname = fname;
		this.lname = lname;
	}
	
	public ArrayList<Author> getAllAuthors(Connection connection) throws SQLException {
		PreparedStatement stmntGetAll = connection.prepareStatement(ALL);
		ResultSet rs = stmntGetAll.executeQuery();
		ArrayList<Author> authors = new ArrayList<Author>();
		while(rs.next()) {
			Integer id = rs.getInt("id");
			String fname = rs.getString("fname");
			String lname = rs.getString("lname");
			Author author = new Author(id, fname, lname);
			authors.add(author);
		}
		return authors;
	}

	public Author getAuthorById(Connection connection, Integer id) throws SQLException {
		PreparedStatement stmntGetById = connection.prepareStatement(BY_ID);
		stmntGetById.setInt(1, id);
		ResultSet rs = stmntGetById.executeQuery();
		Author author = null;
		while(rs.next()) {
			Integer ida = rs.getInt("id");
			String fname = rs.getString("fname");
			String lname = rs.getString("lname");
			author = new Author(ida, fname, lname);
		}
		return author;
	}

}
