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
public class Publisher {
	Integer id;
	String name;
	
	@Value("${publisher.getall}")
	private String ALL;
	
	@Value("${publisher.getbyid}")
	private String BY_ID;

	@Value("${publisher.getbyname}")
	private String BY_NAME;

	
	public Publisher() {
		
	}
	
	public Publisher(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public ArrayList<Publisher> getAllPublishers(Connection connection) throws SQLException {
		PreparedStatement stmntGetAll = connection.prepareStatement(ALL);
		ResultSet rs = stmntGetAll.executeQuery();
		ArrayList<Publisher> publishers = new ArrayList<Publisher>();
		while(rs.next()) {
			Integer id = rs.getInt("id");
			String name = rs.getString("name");
			Publisher publisher = new Publisher(id, name);
			publishers.add(publisher);
		}
		return publishers;
	}

	public Publisher getPublisherById(Connection connection, Integer id) throws SQLException {
		PreparedStatement stmntGetById = connection.prepareStatement(BY_ID);
		stmntGetById.setInt(1, id);
		ResultSet rs = stmntGetById.executeQuery();
		Publisher publisher = null;
		while(rs.next()) {
			Integer ida = rs.getInt("id");
			String name = rs.getString("name");
			publisher = new Publisher(ida, name);
		}
		return publisher;
	}
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Publisher [id=" + id + ", name=" + name + "]";
	}


}
