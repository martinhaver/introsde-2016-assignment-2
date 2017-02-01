package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import introsde.rest.ehealth.dao.LifeCoachDao;

@Entity
@Table(name = "\"MeasureDefinition\"")
@NamedQuery(name = "MeasureDefinition.findAll", query = "SELECT m FROM MeasureDefinition m")
@XmlRootElement(name = "measureType")
public class MeasureDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "sqlite_measuredefinition")
    @TableGenerator(name = "sqlite_measuredefinition", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "MeasureDefinition")
    @Column(name = "\"idMeasureDef\"")
    private int idMeasureDef;

    @Column(name = "\"measureName\"")
    private String measureName;

    @Column(name = "\"measureType\"")
    private String measureType;

    public MeasureDefinition() {
    }

    @XmlTransient
    public int getIdMeasureDef() {
        return this.idMeasureDef;
    }

    public void setIdMeasureDef(int idMeasureDef) {
        this.idMeasureDef = idMeasureDef;
    }

    @XmlValue
    public String getMeasureName() {
        return this.measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    @XmlTransient
    public String getMeasureType() {
        return this.measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    
    public static int getIdUsingType(String measureName) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<MeasureDefinition> list = em.createQuery(
                "SELECT m FROM MeasureDefinition m WHERE m.measureName = :measureName",
                MeasureDefinition.class)
                .setParameter("measureName", measureName)
                .getResultList();
        MeasureDefinition m = list.get(0);
        int id = m.getIdMeasureDef();

        LifeCoachDao.instance.closeConnections(em);
        return id;
    }

    public static MeasureDefinition getMeasureDefinitionUsingType(String measureName) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<MeasureDefinition> list = em.createQuery(
                "SELECT m FROM MeasureDefinition m WHERE m.measureName = :measureName",
                MeasureDefinition.class)
                .setParameter("measureName", measureName)
                .getResultList();
        MeasureDefinition m = list.get(0);

        LifeCoachDao.instance.closeConnections(em);
        return m;
    }

    public static List<MeasureDefinition> getAll() {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<MeasureDefinition> measureTypes = em.createNamedQuery("MeasureDefinition.findAll", MeasureDefinition.class)
                .getResultList();
        LifeCoachDao.instance.closeConnections(em);
        return measureTypes;
    }

}
