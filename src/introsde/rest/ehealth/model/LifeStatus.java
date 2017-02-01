package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import introsde.rest.ehealth.dao.LifeCoachDao;

@Entity
@Table(name = "\"LifeStatus\"")
@NamedQuery(name = "LifeStatus.findAll", query = "SELECT lf FROM LifeStatus lf")
@XmlRootElement(name = "measureType")
public class LifeStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "sqlite_lifestatus")
	@TableGenerator(name = "sqlite_lifestatus", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "LifeStatus")
	@Column(name = "\"idMeasure\"")
	private int idMeasure;

	@Column(name = "\"value\"")
	private String value;

	@Column(name = "\"measure\"")
	private String measure;

	// ---------------------------JOIN-----------------------------
	@ManyToOne
	@JoinColumn(name = "\"idPerson\"", referencedColumnName = "\"idPerson\"")
	private Person person;
	// ----------------------------------------------------------

	@XmlTransient
	public int getIdMeasure() {
		return idMeasure;
	}

	public void setIdMeasure(int idMeasure) {
		this.idMeasure = idMeasure;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person p) {
		this.person = p;
	}
	
	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}



	/* Return the lifestatus of a person. Search by TYPE */

	public static LifeStatus getLifeStatusByPersonIdAndType(int personId, String measureType) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<LifeStatus> list = em
				.createQuery("SELECT l FROM LifeStatus l WHERE l.person.idPerson = :id and l.measure = :measureType",
						LifeStatus.class)
				.setParameter("id", personId).setParameter("measureType", measureType).getResultList();
		return list.get(0);
	}

	/* Return the lifestatus of a person. Search by ID */
	public static List<LifeStatus> getAllLifeStatusByPersonId(int personId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<LifeStatus> list = em
				.createQuery("SELECT l FROM LifeStatus l WHERE l.person.idPerson = :id", LifeStatus.class)
				.setParameter("id", personId).getResultList();
		return list;
	}

	/* Return the lifestatus of all people in DB. */
	public static List<LifeStatus> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<LifeStatus> list = em.createNamedQuery("LifeStatus.findAll", LifeStatus.class).getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	/* RETURN New lifestatus - old one saved in History */
	public static LifeStatus saveLifeStatusAndStore(LifeStatus lifestatus, int personId, String measureType) {

		LifeStatus old = LifeStatus.getLifeStatusByPersonIdAndType(personId, measureType);
		if (old != null) {
			// remove old lifestatus
			LifeStatus.removeLifeStatus(old);
			// save old lifestatus in history
			HealthMeasureHistory.saveLifestatusIntoHistory(old, personId);
		}

		// create new lifestatus
		Person p = Person.getPersonById(personId);
		lifestatus.setPerson(p);
		lifestatus.setMeasure(measureType);
		// store new lifestatus
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(lifestatus);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		// up lifestatus reference in person
		Person.updatePerson(p);

		return lifestatus;
	}

	public static LifeStatus updateLifestatus(LifeStatus lf) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		lf = em.merge(lf);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		return lf;
	}

	public static void removeLifeStatus(LifeStatus lf) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		lf = em.merge(lf);
		em.remove(lf);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
	}

}
