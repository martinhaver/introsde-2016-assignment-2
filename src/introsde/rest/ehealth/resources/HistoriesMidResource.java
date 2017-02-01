package introsde.rest.ehealth.resources;

import java.text.ParseException;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import introsde.rest.ehealth.model.HealthMeasureHistory;

public class HistoriesMidResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	int id;
	String measureType;
	int mid;
	String beforedate;
	String afterdate;
	EntityManager entityManager;

	// with "mid" ------------------------------------------------------
	public HistoriesMidResource(UriInfo uriInfo, Request request, int id, String measureType, int mid,
			EntityManager em) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.measureType = measureType;
		this.mid = mid;
		this.entityManager = em;
	}

	public HistoriesMidResource(UriInfo uriInfo, Request request, int id, String measureType, int mid) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.measureType = measureType;
		this.mid = mid;
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public HealthMeasureHistory getPHistoryMid() {
		HealthMeasureHistory history = this.getPHistoryIdTypeMid(id, measureType, mid);
		if (history == null) {
			throw new RuntimeException("Get: History with " + id + " not found");
		}
		return history;
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putPHistoryMid(HealthMeasureHistory history) {
		System.out.println("--> Updating History... " + this.id);
		Response res;
		HealthMeasureHistory existing = this.getPHistoryIdTypeMid(id, measureType, mid);

		if (existing == null) {
			res = Response.noContent().build();
		} else {
			history.setIdMeasureHistory(existing.getIdMeasureHistory());
			history.setMeasureDefinition(existing.getMeasureDefinition());
			history.setPerson(existing.getPerson());

			if (history.getTimestamp() == null) {
				try {
					history.setTimestamp(existing.getTimestamp());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (history.getValue() == null) {
				history.setValue(existing.getValue());
			}

			HealthMeasureHistory updatedHistory = HealthMeasureHistory.updateHealthMeasureHistory(history);
			res = Response.ok().entity(updatedHistory).build();
		}

		return res;
	}

	public HealthMeasureHistory getPHistoryIdTypeMid(int personId, String measureType, int mid) {
		System.out.println("Reading person history from DB with id: " + personId);
		HealthMeasureHistory history = HealthMeasureHistory.getPHUsingIdTypeMid(personId, measureType, mid);
		System.out.println("Person history: " + history.toString());
		return history;
	}
}
