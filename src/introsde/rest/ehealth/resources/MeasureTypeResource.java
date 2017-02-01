package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.model.MeasureDefinition;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
@Path("/measureTypes")
public class MeasureTypeResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @PersistenceUnit(unitName = "introsde-jpa")
    EntityManager entityManager;

    @PersistenceContext(unitName = "introsde-jpa", type = PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;

	/*
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML })
	public String getPersonsXML() {
		System.out.println("Getting list of measure definition in XML...");
		List<MeasureDefinition> md = MeasureDefinition.getAll();
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><measureTypes>";
		for (int i = 0; i < md.size(); i++) {
			result += "<measureType>" + md.get(i).getMeasureName() + "</measureType>";
		}
		result += "</measureTypes>";
		return result;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getPersonsJSON() {
		System.out.println("Getting list of measure definition in JSON...");
		List<MeasureDefinition> md = MeasureDefinition.getAll();
		String result = "{\"measureType\": [";
		for (int i = 0; i < md.size() - 1; i++) {
			result += "\"" + md.get(i).getMeasureName() + "\",";
		}
		result += "\"" + md.get(md.size() - 1).getMeasureName() + "\"]}";
		return result;
	}*/
	
    // Browser --> List of measure
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<MeasureDefinition> getMeasureDefBrowser() {
        System.out.println("Getting list of measure definitions...");
        List<MeasureDefinition> list = MeasureDefinition.getAll();
        return list;
    }
}
