package introsde.rest.ehealth.resources;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import introsde.rest.ehealth.model.HealthMeasureHistory;
import introsde.rest.ehealth.model.LifeStatus;

public class HistoriesResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	int id;
	String measureType;
	String befored;
	String afterd;

	EntityManager entityManager;

	public HistoriesResource(UriInfo uriInfo, Request request, int id, String measureType, EntityManager em) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.measureType = measureType;
		this.entityManager = em;
	}

	public HistoriesResource(UriInfo uriInfo, Request request, int id, String measureType) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.measureType = measureType;
	}

	public HistoriesResource(UriInfo uriInfo, Request request, int id, String measureType, String beforeDate,
			String afterDate) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.measureType = measureType;
		this.befored = beforeDate;
		this.afterd = afterDate;
	}

	// NOT GOING TO USE MID
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<HealthMeasureHistory> getPersonHistory() {
		List<HealthMeasureHistory> history = this.getPHistoryIdType(id, measureType, befored, afterd);
		if (history == null) {
			throw new RuntimeException("Get: History with " + id + " not found");
		}
		return history;
	}

	// Here we add a new LifeStatus inside DB!
	// NOT GOING TO USE MID
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public LifeStatus newLS(LifeStatus lifestatus) throws IOException {
		System.out.println("Creating in history...");
		return LifeStatus.saveLifeStatusAndStore(lifestatus, id, measureType);
	}

	public List<HealthMeasureHistory> getPHistoryIdType(int personId, String measureType, String beforeDate,
			String afterDate) {
		System.out.println("Reading history from DB with id: " + personId);
		List<HealthMeasureHistory> history = HealthMeasureHistory.getPHUsingIdType(personId, measureType,
				beforeDate, afterDate);
		System.out.println("history: " + history.toString());
		return history;
	}

}
