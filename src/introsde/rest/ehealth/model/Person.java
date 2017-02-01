package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import introsde.rest.ehealth.dao.LifeCoachDao;

@Entity
@Table(name = "\"Person\"")
@NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p")
@XmlRootElement
public class Person implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "sqlite_person")
	@TableGenerator(name = "sqlite_person", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Person")
	@Column(name = "\"idPerson\"")
	private int idPerson;

	@Column(name = "\"firstname\"")
	private String firstname;

	@Column(name = "\"lastname\"")
	private String lastname;

	@Temporal(TemporalType.DATE)
	@Column(name = "\"birthdate\"")
	private Date birthdate;

	@Column(name = "\"username\"")
	private String username;

	@Column(name = "\"email\"")
	private String email;

	// ----------------------------------------JOIN--------------------------------------------
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<LifeStatus> lifeStatus;
	// -----------------------------------------------------------------------------------------

	public Person() {
	}

	public int getIdPerson() {
		return idPerson;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getBirthdate() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		if (birthdate == null) {
			return null;
		}
		return df.format(birthdate);
	}

	public String getEmail() {
		return this.email;
	}

	public String getUsername() {
		return this.username;
	}

	public void setIdPerson(int idPerson) {
		this.idPerson = idPerson;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	@XmlElementWrapper(name = "healthprofile")
	@XmlElement(name = "measureType")
	public List<LifeStatus> getLifeStatus() {
		return lifeStatus;
	}

	public static Person getPersonById(int personId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Person p = em.find(Person.class, personId);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}

	public void setBirthdate(String bd) throws ParseException {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date date = format.parse(bd);
		this.birthdate = date;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setLifeStatus(List<LifeStatus> lifestatus) {
		this.lifeStatus = lifestatus;
	}

	public static List<Person> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	public static Person savePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);

		List<LifeStatus> list = p.getLifeStatus();
		for (LifeStatus l : list) {
			l.setPerson(p);
			LifeStatus.updateLifestatus(l);
		}

		return p;
	}

	public static Person updatePerson(Person p) {
		p.setLifeStatus(LifeStatus.getAllLifeStatusByPersonId(p.idPerson));
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p = em.merge(p);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}

	public static void removePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p = em.merge(p);
		em.remove(p);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
	}

	@Override
	public String toString() {
		return "Name: '" + this.firstname + " " + this.lastname + "', Birthday: '" + this.birthdate + "'";
	}
}
