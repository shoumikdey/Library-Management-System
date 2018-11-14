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
public class Subject {
	Integer id;
	String name;
	
	@Value("${subject.getall}")
	private String ALL;
	
	@Value("${subject.getbyid}")
	private String BY_ID;

	@Value("${subject.getbyname}")
	private String BY_NAME;

	
	public Subject() {
		
	}
	
	public Subject(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public ArrayList<Subject> getAllSubjects(Connection connection) throws SQLException {
		PreparedStatement stmntGetAll = connection.prepareStatement(ALL);
		ResultSet rs = stmntGetAll.executeQuery();
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		while(rs.next()) {
			Integer id = rs.getInt("id");
			String name = rs.getString("name");
			Subject subject = new Subject(id, name);
			subjects.add(subject);
		}
		return subjects;
	}

	public Subject getSubjectById(Connection connection, Integer id) throws SQLException {
		PreparedStatement stmntGetById = connection.prepareStatement(BY_ID);
		stmntGetById.setInt(1, id);
		ResultSet rs = stmntGetById.executeQuery();
		Subject subject = null;
		while(rs.next()) {
			Integer ida = rs.getInt("id");
			String name = rs.getString("name");
			subject = new Subject(ida, name);
		}
		return subject;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Subject [id=" + id + ", name=" + name + "]";
	}
	
}
