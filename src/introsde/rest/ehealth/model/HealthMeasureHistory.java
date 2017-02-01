package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import introsde.rest.ehealth.dao.LifeCoachDao;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "\"HealthMeasureHistory\"")
@NamedQuery(name = "HealthMeasureHistory.findAll", query = "SELECT h FROM HealthMeasureHistory h")
@XmlRootElement
public class HealthMeasureHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "sqlite_healthmeasurehistory")
	@TableGenerator(name = "sqlite_healthmeasurehistory", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "HealthMeasureHistory")
	@Column(name = "\"idMeasureHistory\"")
	private int idMeasureHistory;

	@Temporal(TemporalType.DATE)
	@Column(name = "\"timestamp\"")
	private Date timestamp;

	@Column(name = "\"value\"")
	private String value;

	// ---------------------------JOIN-----------------------------
	@ManyToOne
	@JoinColumn(name = "\"idMeasureDef\"", referencedColumnName = "\"idMeasureDef\"")
	private MeasureDefinition measureDefinition;

	@ManyToOne
	@JoinColumn(name = "\"idPerson\"", referencedColumnName = "\"idPerson\"")
	private Person person;
	// -------------------------------------------------------------

	public HealthMeasureHistory() {
	}

	@XmlElement(name = "mid")
	public int getIdMeasureHistory() {
		return this.idMeasureHistory;
	}

	public void setIdMeasureHistory(int idMeasureHistory) {
		this.idMeasureHistory = idMeasureHistory;
	}

	@XmlElement(name = "created")
	public String getTimestamp() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		if (timestamp == null) {
			return null;
		}
		return df.format(timestamp);
	}

	public void setTimestamp(String ts) throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
		Date date = format.parse(ts);
		this.timestamp = date;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MeasureDefinition getMeasureDefinition() {
		return this.measureDefinition;
	}

	public void setMeasureDefinition(MeasureDefinition measureDefinition) {
		this.measureDefinition = measureDefinition;
	}

	@XmlTransient
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public static HealthMeasureHistory updateHealthMeasureHistory(HealthMeasureHistory h) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		h = em.merge(h);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		return h;
	}

	public static List<HealthMeasureHistory> getPHUsingIdType(int personId, String measureType,
			String beforeDate, String afterDate) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		int idMeasureDef = MeasureDefinition.getIdUsingType(measureType);

		List<HealthMeasureHistory> list = null;
		if (beforeDate == null || afterDate == null) {
			list = em
					.createQuery(
							"SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson= :id and h.measureDefinition.idMeasureDef= :idMeasure",
							HealthMeasureHistory.class)
					.setParameter("id", personId).setParameter("idMeasure", idMeasureDef).getResultList();
		} else {
			try {
				SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
				Date bDate = sdformat.parse(beforeDate);
				Date aDate = sdformat.parse(afterDate);
				sdformat.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				bDate = sdformat.parse(sdformat.format(bDate));
				aDate = sdformat.parse(sdformat.format(aDate));

				list = em
						.createQuery(
								"SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson= :id and h.measureDefinition.idMeasureDef= :idMeasure "
										+ "and h.timestamp < :bDate and h.timestamp > :aDate",
								HealthMeasureHistory.class)
						.setParameter("id", personId).setParameter("idMeasure", idMeasureDef)
						.setParameter("bDate", bDate, TemporalType.DATE).setParameter("aDate", aDate, TemporalType.DATE)
						.getResultList();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	public static HealthMeasureHistory getPHUsingIdTypeMid(int personId, String measureType, int mid) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		int idMeasureDef = MeasureDefinition.getIdUsingType(measureType);
		HealthMeasureHistory h = em
				.createQuery(
						"SELECT h FROM HealthMeasureHistory h WHERE h.idMeasureHistory= :mid and h.person.idPerson= :id and h.measureDefinition.idMeasureDef= :idMeasure",
						HealthMeasureHistory.class)
				.setParameter("mid", mid).setParameter("id", personId).setParameter("idMeasure", idMeasureDef)
				.getSingleResult();
		LifeCoachDao.instance.closeConnections(em);
		return h;
	}

	public static void saveLifestatusIntoHistory(LifeStatus lifestatus, int personId) {
		HealthMeasureHistory h = new HealthMeasureHistory();
		h.setValue(lifestatus.getValue());
		try {
			h.setTimestamp(new Date().toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Person p = Person.getPersonById(personId);
		h.setPerson(p);
		h.setMeasureDefinition(MeasureDefinition.getMeasureDefinitionUsingType(lifestatus.getMeasure()));
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(h);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
	}
	
	public static List<HealthMeasureHistory> getAll(int personId, String type) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<HealthMeasureHistory> list = em
				.createNamedQuery("HealthMeasureHistory.findAll", HealthMeasureHistory.class).getResultList();

		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	public static HealthMeasureHistory updateHealthMeasureHistory1(HealthMeasureHistory h) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		h = em.merge(h);
		tx.commit();
		LifeCoachDao.instance.closeConnections(em);
		return h;
	}
}
