package library.db.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sql.properties")
public class Member {
	@Override
	public String toString() {
		return "Member [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email=" + email + "]";
	}

	Integer id;
    String firstname;
    String lastname;
    String email;
    
    @Value("${member.getall}")
	private String ALL;
	
	@Value("${member.getbyid}")
	private String BY_ID;

	@Value("${member.getbyfirst}")
	private String BY_FIRST;

	@Value("${member.getbylast}")
	private String BY_LAST;

	@Value("${member.new}")
	private String NEW;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Member create(Connection connection) throws SQLException {
		PreparedStatement stmntNew = connection.prepareStatement(NEW, Statement.RETURN_GENERATED_KEYS);
		stmntNew.setString(1, this.firstname);
		stmntNew.setString(2, this.lastname);
		stmntNew.setString(3, this.email);
		stmntNew.executeUpdate();
		ResultSet rs = stmntNew.getGeneratedKeys();
        if(rs.next())
            this.setId(rs.getInt(1));
		return this;
	}
	
	public Member getById(Connection connection, Integer id) throws SQLException {
		if(id == null)
			return null;
		PreparedStatement stmntGetById = connection.prepareStatement(BY_ID);
		stmntGetById.setInt(1, id);
		ResultSet rs = stmntGetById.executeQuery();
		Member member = null;
		while(rs.next()) {
			Integer ida = rs.getInt("id");
			String fname = rs.getString("firstname");
			String lname = rs.getString("lastname");
			String email = rs.getString("email");
			member = new Member(ida, fname, lname, email);
		}
		return member;
	}
	
	public List<Member> getAllMembers(Connection connection) throws SQLException {
		PreparedStatement stmntGetAll = connection.prepareStatement(ALL);
		ResultSet rs = stmntGetAll.executeQuery();
		ArrayList<Member> members = new ArrayList<Member>();
		while(rs.next()) {
			Integer id = rs.getInt("id");
			String firstName = rs.getString("firstname");
			String lastName = rs.getString("lastname");
			String email = rs.getString("email");
			members.add(new Member(id, firstName, lastName, email));
		}
		return members;
		
	}

	public Member() {
		super();
	}

	public Member(Integer id, String firstname, String lastname, String email) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	public String getALL() {
		return ALL;
	}

	public String getBY_ID() {
		return BY_ID;
	}

	public String getBY_FIRST() {
		return BY_FIRST;
	}

	public String getBY_LAST() {
		return BY_LAST;
	}

	public String getNEW() {
		return NEW;
	}

	public void setALL(String aLL) {
		ALL = aLL;
	}

	public void setBY_ID(String bY_ID) {
		BY_ID = bY_ID;
	}

	public void setBY_FIRST(String bY_FIRST) {
		BY_FIRST = bY_FIRST;
	}

	public void setBY_LAST(String bY_LAST) {
		BY_LAST = bY_LAST;
	}

	public void setNEW(String nEW) {
		NEW = nEW;
	}

}
