package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.model.Person;

import java.text.ParseException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
public class PersonResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	int id;

	EntityManager entityManager;

	public PersonResource(UriInfo uriInfo, Request request, int id, EntityManager em) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.entityManager = em;
	}

	public PersonResource(UriInfo uriInfo, Request request, int id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// -------Assignment 2 SDE LAB-------PART 2 REQUEST 1----------------
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getPerson() {
		Person person = this.getPersonById(id);
		if (person == null) {
			return Response.status(404).build();
		}
		return Response.ok().entity(person).build();
	}

	// -------Assignment 2 SDE LAB-------PART 2 REQUEST 3----------------
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putPerson(Person person) {
		System.out.println("--> Updating Person... " + this.id);
		System.out.println("--> " + person.toString());

		Response res;
		Person existing = getPersonById(this.id);

		if (existing == null) {
			res = Response.noContent().build();
		} else {
			person.setIdPerson(this.id);

			if (person.getFirstname() == null) {
				person.setFirstname(existing.getFirstname());
			}
			if (person.getLastname() == null) {
				person.setLastname(existing.getLastname());
			}
			if (person.getBirthdate() == null) {
				try {
					person.setBirthdate(existing.getBirthdate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (person.getUsername() == null) {
				person.setUsername(existing.getUsername());
			}
			if (person.getEmail() == null) {
				person.setEmail(existing.getEmail());
			}
			Person updatedPerson = Person.updatePerson(person);
			res = Response.ok().entity(updatedPerson).build();
		}
		return res;
	}

	// -------Assignment 2 SDE LAB-------PART 2 REQUEST 5----------------
	@DELETE
	public void deletePerson() {
		Person c = getPersonById(id);
		if (c == null) {
			throw new RuntimeException("Delete: Person with " + id + " not found");
		}
		Person.removePerson(c);
	}

	public Person getPersonById(int personId) {
		System.out.println("Reading person from DB with id: " + personId);
		Person person = Person.getPersonById(personId);
		if (person != null) {
			System.out.println("Person: " + person.toString());
		}
		return person;
	}
}
