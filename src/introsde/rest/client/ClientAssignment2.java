package introsde.rest.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.glassfish.jersey.client.ClientConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientAssignment2 {

	private static String first_id;
	private static String last_id;
	private static DocumentBuilderFactory domFactory;
	private static DocumentBuilder builder;
	private static XPath xpath;
	private static WebTarget service;
	private static String start;
	private static String request;
	private static String type;
	private static String content;
	private static boolean result;
	private static String xml;
	private static String json;
	private static Document doc;
	private static Response resp;
	private static String measure_id;
	private static String measure_type;
	private static ObjectMapper mapper;
	private static PrintStream printxml;
	private static PrintStream printjson;

	private static void initialize() throws ParserConfigurationException, FileNotFoundException {
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		service = client.target(getBaseURI());
		System.out.println("Test started at url:" + getBaseURI());
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		builder = domFactory.newDocumentBuilder();
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		FileOutputStream filexml = new FileOutputStream("client-server-xml-heroku.log");
		FileOutputStream filejson = new FileOutputStream("client-server-json-heroku.log");
		printxml = new PrintStream(filexml);
		printjson = new PrintStream(filejson);

		mapper = new ObjectMapper();
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException,
			XPathExpressionException, TransformerException {
		initialize();

		request1XML();
		printResult();
		request2XML(first_id);
		printResult();
		request3XML();
		printResult();
		String newperson_id = request4XML();
		printResult();
		request5XML(newperson_id);
		printResult();
		String[] s = request6XML();
		printResult();
		request7XML(s, first_id);
		String id = first_id;
		if (result == false) {
			request7XML(s, last_id);
			id = last_id;
		}
		printResult();
		request8XML(id);
		printResult();
		request9XML(s);
		printResult();
		request10XML(id);
		printResult();
		request11XML(id);
		printResult();
		request12XML();
		printResult();

		request1JSON();
		printResult();
		request2JSON(first_id);
		printResult();
		request3JSON();
		printResult();
		newperson_id = request4JSON();
		printResult();
		request5JSON(newperson_id);
		printResult();
		s = request6JSON();
		printResult();
		request7JSON(s, first_id);
		id = first_id;
		if (result == false) {
			request7JSON(s, last_id);
			id = last_id;
		}
		printResult();
		request8JSON(id);
		printResult();
		request9JSON(s);
		printResult();
		request10JSON(id);
		printResult();
		request11JSON(id);
		printResult();
		request12JSON();
		printResult();

		System.out.println("Output written in log files");

	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("https://fathomless-lowlands-81350.herokuapp.com/sdelab").build();
	}

	private static void printResult() throws TransformerException {
		PrintStream stream = null;
		if (type == MediaType.APPLICATION_XML) {
			stream = printxml;
		} else if (type == MediaType.APPLICATION_JSON) {
			stream = printjson;
		}
		stream.print(start + request + " Accept: " + type);
		if (content != null)
			stream.println(" Content-Type:  " + content);
		else
			stream.println();
		if (result)
			stream.println("=> Result: OK");
		else
			stream.println("=> Result: ERROR");
		if (resp != null) {
			stream.println("=> HTTP Status: " + resp.getStatus());
			if (type == MediaType.APPLICATION_XML) {
				stream.println(printXML(doc));
			} else if (type == MediaType.APPLICATION_JSON) {
				stream.println(printJSON());
			}

		} else {
			System.out.println("=> HTTP Status: NO RESPONSE");
		}

	}

	private static String printXML(Document doc) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		// initialize StreamResult with File object to save to file
		StreamResult result1 = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result1);
		String xmlString = result1.getWriter().toString();
		return xmlString;
	}

	private static String printJSON() {
		Object obj;
		String jsonString = null;
		try {
			if (!json.isEmpty()) {
				obj = mapper.readValue(json, Object.class);
				jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	private static void request1XML() throws SAXException, IOException, XPathExpressionException, TransformerException {
		// GET Request #1 - GET /person

		start = "Request #1: GET /";
		request = "person";
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		xml = resp.readEntity(String.class);
		doc = builder.parse(new InputSource(new StringReader(xml)));

		XPathExpression expr = xpath.compile("//*");
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		if (nodes.getLength() > 2)
			result = true;

		expr = xpath.compile("//person[1]/idPerson");
		Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
		first_id = node.getTextContent();

		expr = xpath.compile("//person[last()]/idPerson");
		node = (Node) expr.evaluate(doc, XPathConstants.NODE);
		last_id = node.getTextContent();

	}

	private static void request2XML(String id)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// GET Request #2 - GET /person/first_id

		start = "Request #2: GET /";
		request = "person/" + id;
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		xml = resp.readEntity(String.class);
		if (!xml.isEmpty())
			doc = builder.parse(new InputSource(new StringReader(xml)));

		if (resp.getStatus() == 200 || resp.getStatus() == 202)
			result = true;

	}

	private static void request3XML() throws SAXException, IOException, XPathExpressionException, TransformerException {
		// PUT Request #3 - PUT /person/first_id

		start = "Request #3: PUT /";
		request = "person/" + first_id;
		type = MediaType.APPLICATION_XML;
		content = MediaType.APPLICATION_XML;
		result = false;

		String newName = "Dexter";
		String requestBody = "<person><firstname>" + newName + "</firstname></person>";
		resp = service.path(request).request().accept(type).put(Entity.entity(requestBody, content));
		xml = resp.readEntity(String.class);
		doc = builder.parse(new InputSource(new StringReader(xml)));

		XPathExpression expr = xpath.compile("//firstname");
		String firstname = (String) expr.evaluate(doc, XPathConstants.STRING);
		if (newName.equals(firstname))
			result = true;

	}

	private static String request4XML()
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// POST Request #4 - POST /person

		start = "Request #4: POST /";
		request = "person";
		type = MediaType.APPLICATION_XML;
		content = MediaType.APPLICATION_XML;
		result = false;
		String newperson_id = "";

		String requestBody = "<person>" + "<firstname>Chuck</firstname>" + "<lastname>Norris</lastname>"
				+ "<birthdate>01/01/1945</birthdate>" + "<healthprofile>"
				+ "<measureType><measure>weight</measure><value>78.9</value></measureType>"
				+ "<measureType><measure>height</measure><value>172</value></measureType>"
				+ "</healthprofile></person>";

		resp = service.path(request).request().accept(type).post(Entity.entity(requestBody, content));
		xml = resp.readEntity(String.class);
		doc = builder.parse(new InputSource(new StringReader(xml)));

		XPathExpression expr = xpath.compile("//idPerson");
		newperson_id = (String) expr.evaluate(doc, XPathConstants.STRING);
		if ((resp.getStatus() == 200 || resp.getStatus() == 201 || resp.getStatus() == 202) && !newperson_id.isEmpty())
			result = true;
		return newperson_id;

	}

	private static void request5XML(String id)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// DELETE Request #5 - DELETE /person/id

		start = "Request #5: DELETE /";
		request = "person/" + id;
		type = MediaType.APPLICATION_XML;
		content = null;
		doc = null;

		Response this_resp = service.path(request).request().accept(type).delete();
		request2XML(id);

		start = "Request #5: DELETE /";
		request = "person/" + id;
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		if (resp.getStatus() == 404)
			result = true;
		resp = this_resp;
	}

	private static String[] request6XML()
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// GET Request #6 - GET /measureTypes

		start = "Request #6: GET /";
		request = "measureTypes";
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		xml = resp.readEntity(String.class);
		doc = builder.parse(new InputSource(new StringReader(xml)));

		XPathExpression expr = xpath.compile("//*");
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		if (nodes.getLength() > 2)
			result = true;
		String[] measureTypes = new String[nodes.getLength() - 1];
		for (int i = 1; i < nodes.getLength(); i++) {
			measureTypes[i - 1] = nodes.item(i).getTextContent();
		}
		return measureTypes;

	}

	private static void request7XML(String[] vector, String id)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// GET Request #7 - GET /person/{id}/{measureType}

		start = "Request #7: GET /";
		request = "person/" + id + "/";
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;
		Response this_res = null;
		String this_req = null;
		String this_xml = null;
		Document this_doc = null;

		for (int i = 0; i < vector.length; i++) {
			String request1 = request + vector[i];

			resp = service.path(request1).request().accept(type).get();
			if (resp.getStatus() == 200) {
				result = true;
				xml = resp.readEntity(String.class);
				doc = builder.parse(new InputSource(new StringReader(xml)));
				XPathExpression test = xpath.compile("//healthMeasureHistories");
				Node nodetest = (Node) test.evaluate(doc, XPathConstants.NODE);
				String resultstring = nodetest.getTextContent();
				if (!resultstring.isEmpty()) {
					XPathExpression expr = xpath.compile("//measureDefinition[1]");
					Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
					measure_type = node.getTextContent();
					expr = xpath.compile("//mid[1]");
					node = (Node) expr.evaluate(doc, XPathConstants.NODE);
					measure_id = node.getTextContent();
					id = first_id;
					this_res = resp;
					this_req = request1;
					this_xml = xml;
					this_doc = doc;
				}

			}

		}
		xml = this_xml;
		resp = this_res;
		request = this_req;
		doc = this_doc;

	}

	private static void request8XML(String id)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// GET Request #8 - GET /person/{id}/{measureType}/{mid}

		start = "Request #8: GET /";
		request = "person/" + id + "/" + measure_type + "/" + measure_id;
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		xml = resp.readEntity(String.class);
		if (!xml.isEmpty())
			doc = builder.parse(new InputSource(new StringReader(xml)));

		if (resp.getStatus() == 200 || resp.getStatus() == 202)
			result = true;

	}

	private static void request9XML(String[] vector)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// POST Request #9 - POST /person/{first_person_id}/{measureType}

		request7XML(vector, first_id);
		XPathExpression expr = xpath.compile("count(//healthMeasureHistory)");
		int count = Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING));

		start = "Request #9: POST /";
		type = MediaType.APPLICATION_XML;
		content = MediaType.APPLICATION_XML;
		result = false;
		request = "person/" + first_id + "/" + measure_type;
		String requestBody = "<measureType><value>72</value></measureType>";

		Response this_resp = service.path(request).request().accept(type).post(Entity.entity(requestBody, content));
		String this_xml = this_resp.readEntity(String.class);
		Document this_doc = builder.parse(new InputSource(new StringReader(this_xml)));

		request7XML(vector, first_id);
		expr = xpath.compile("count(//healthMeasureHistory)");
		int second_count = Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING));

		start = "Request #9: POST /";
		type = MediaType.APPLICATION_XML;
		content = MediaType.APPLICATION_XML;
		result = false;
		request = "person/" + first_id + "/" + measure_type;
		resp = this_resp;
		xml = this_xml;
		doc = this_doc;

		if (count + 1 == second_count)
			result = true;

	}

	private static void request10XML(String id)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// PUT Request #10 - PUT /person/{id}/{measureType}/{mid}

		start = "Request #10: PUT /";
		request = "person/" + id + "/" + measure_type + "/" + measure_id;
		type = MediaType.APPLICATION_XML;
		content = MediaType.APPLICATION_XML;
		result = false;
		int value = 82;

		String requestBody = "<healthMeasureHistory><value>" + value + "</value></healthMeasureHistory>";
		Response this_resp = service.path(request).request().accept(type).put(Entity.entity(requestBody, content));
		String this_xml = this_resp.readEntity(String.class);
		Document this_doc = builder.parse(new InputSource(new StringReader(this_xml)));

		request8XML(id);
		XPathExpression expr = xpath.compile("//value");
		int newvalue = Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING));

		// reset variable
		start = "Request #10: PUT /";
		request = "person/" + id + "/" + measure_type + "/" + measure_id;
		type = MediaType.APPLICATION_XML;
		content = MediaType.APPLICATION_XML;
		result = false;
		resp = this_resp;
		xml = this_xml;
		doc = this_doc;

		if (value == newvalue)
			result = true;

	}

	private static void request11XML(String id)
			throws SAXException, IOException, XPathExpressionException, TransformerException {
		// GET Request #11 - GET
		// /person/{id}/{measureType}?before={beforeDate}&after={afterDate}

		String before = "20-11-2016";
		String after = "10-11-1990";
		measure_type = "weight";
		start = "Request #11: GET /";
		request = "person/" + id + "/" + measure_type;
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		resp = service.path(request).queryParam("before", before).queryParam("after", after).request().accept(type)
				.get();
		xml = resp.readEntity(String.class);

		if (!xml.isEmpty()) {
			doc = builder.parse(new InputSource(new StringReader(xml)));
		}

		XPathExpression expr = xpath.compile("count(//healthMeasureHistory)");
		int size = Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING));

		if (resp.getStatus() == 200 && size > 0)
			result = true;
		request = "person/" + id + "/" + measure_type + "?before=" + before + "&after=" + after;

	}

	private static void request12XML() throws XPathExpressionException, SAXException, IOException {
		// GET Request #12 - GET
		// /person/{id}?measureType={measureType}&max={max}&min={min}

		String max = "95";
		String min = "85";
		measure_type = "weight";
		start = "Request #12: GET /";
		request = "person";
		type = MediaType.APPLICATION_XML;
		content = null;
		result = false;

		resp = service.path(request).queryParam("measureType", measure_type).queryParam("max", max)
				.queryParam("min", min).request().accept(type).get();
		xml = resp.readEntity(String.class);
		if (!xml.isEmpty())
			doc = builder.parse(new InputSource(new StringReader(xml)));

		XPathExpression expr = xpath.compile("count(//person)");
		int size = Integer.parseInt((String) expr.evaluate(doc, XPathConstants.STRING));

		if (resp.getStatus() == 200 && size > 0)
			result = true;
		request = "person?measureType=" + measure_type + "&max=" + max + "&min=" + min;

	}

	private static void request1JSON() throws JsonProcessingException, IOException {

		start = "Request #1: GET /";
		request = "person";
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		json = resp.readEntity(String.class);
		JsonNode nodes = mapper.readTree(json);

		if (nodes.size() > 2)
			result = true;

		first_id = nodes.get(0).path("idPerson").asText();

		last_id = nodes.get(nodes.size() - 1).path("idPerson").asText();

	}

	private static void request2JSON(String id) {

		start = "Request #2: GET /";
		request = "person/" + id;
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		json = resp.readEntity(String.class);

		if (resp.getStatus() == 200 || resp.getStatus() == 202)
			result = true;

	}

	private static void request3JSON() throws JsonProcessingException, IOException {

		start = "Request #3: PUT /";
		request = "person/" + first_id;
		type = MediaType.APPLICATION_JSON;
		content = MediaType.APPLICATION_JSON;
		result = false;

		String newName = "Jon";
		String requestBody = "{\"firstname\": \"" + newName + "\"}";

		resp = service.path(request).request().accept(type).put(Entity.entity(requestBody, content));
		json = resp.readEntity(String.class);
		JsonNode node = mapper.readTree(json);

		String firstname = node.path("firstname").asText();
		if (newName.equals(firstname))
			result = true;

	}

	private static String request4JSON() throws JsonProcessingException, IOException {

		start = "Request #4: POST /";
		request = "person";
		type = MediaType.APPLICATION_JSON;
		content = MediaType.APPLICATION_JSON;
		result = false;
		String newperson_id = "";

		String requestBody = "{" + "\"firstname\": \"Chuck\"," + "\"lastname\":\"Norris\","
				+ "\"birthdate\":\"01/01/1945\"," + "\"measureType\":[" + "{\"measure\":\"weight\",\"value\":\"78.9\"},"
				+ "{\"measure\":\"height\",\"value\":\"172\"}]" + "}";

		resp = service.path(request).request().accept(type).post(Entity.entity(requestBody, content));
		json = resp.readEntity(String.class);
		JsonNode node = mapper.readTree(json);

		newperson_id = node.path("idPerson").asText();
		if ((resp.getStatus() == 200 || resp.getStatus() == 201 || resp.getStatus() == 202) && !newperson_id.isEmpty())
			result = true;
		return newperson_id;

	}

	private static void request5JSON(String id) {

		start = "Request #5: DELETE /";
		request = "person/" + id;
		type = MediaType.APPLICATION_JSON;
		content = null;
		doc = null;

		Response this_resp = service.path(request).request().accept(type).delete();
		request2JSON(id);

		// reset variable
		start = "Request #5: DELETE /";
		request = "person/" + id;
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		if (resp.getStatus() == 404)
			result = true;
		resp = this_resp;
	}

	private static String[] request6JSON() throws JsonProcessingException, IOException {

		start = "Request #6: GET /";
		request = "measureTypes";
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		json = resp.readEntity(String.class);
		JsonNode node = mapper.readTree(json);

		int size = node.size();
		if (size > 2)
			result = true;

		String[] measureTypes = new String[size];
		for (int i = 0; i < size; i++) {
			measureTypes[i] = node.get(i).path("value").asText();
		}
		return measureTypes;

	}

	private static void request7JSON(String[] vector, String id) throws JsonProcessingException, IOException {

		start = "Request #7: GET /";
		request = "person/" + id + "/";
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;
		Response this_res = null;
		String this_req = null;
		String this_json = null;

		for (int i = 0; i < vector.length; i++) {
			String request1 = request + vector[i];

			resp = service.path(request1).request().accept(type).get();
			if (resp.getStatus() == 200) {
				result = true;
				json = resp.readEntity(String.class);
				JsonNode node = mapper.readTree(json);
				if (!"[]".equals(json)) {
					measure_type = node.get(0).path("measureDefinition").path("value").asText();
					measure_id = node.get(0).path("mid").asText();
					id = first_id;
					this_res = resp;
					this_req = request1;
					this_json = json;
				}

			}

		}

		resp = this_res;
		request = this_req;
		json = this_json;

	}

	private static void request8JSON(String id) {

		start = "Request #8: GET /";
		request = "person/" + id + "/" + measure_type + "/" + measure_id;
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		resp = service.path(request).request().accept(type).get();

		json = resp.readEntity(String.class);

		if (resp.getStatus() == 200 || resp.getStatus() == 202)
			result = true;

	}

	private static void request9JSON(String[] vector) throws JsonProcessingException, IOException {

		request7JSON(vector, first_id);
		JsonNode node = mapper.readTree(json);
		int count = node.size();

		start = "Request #9: POST /";
		type = MediaType.APPLICATION_JSON;
		content = MediaType.APPLICATION_JSON;
		result = false;
		request = "person/" + first_id + "/" + measure_type;
		String requestBody = "{\"value\":\"72\"}";

		Response this_resp = service.path(request).request().accept(type).post(Entity.entity(requestBody, content));
		String this_json = this_resp.readEntity(String.class);

		request7JSON(vector, first_id);
		node = mapper.readTree(json);
		int second_count = node.size();

		// reset variable
		start = "Request #9: POST /";
		type = MediaType.APPLICATION_JSON;
		content = MediaType.APPLICATION_JSON;
		result = false;
		request = "person/" + first_id + "/" + measure_type;
		resp = this_resp;
		json = this_json;

		if (count + 1 == second_count)
			result = true;

	}

	private static void request10JSON(String id) throws JsonProcessingException, IOException {

		start = "Request #10: PUT /";
		request = "person/" + id + "/" + measure_type + "/" + measure_id;
		type = MediaType.APPLICATION_JSON;
		content = MediaType.APPLICATION_JSON;
		result = false;
		int value = 82;

		String requestBody = "{\"value\":" + value + "}";
		Response this_resp = service.path(request).request().accept(type).put(Entity.entity(requestBody, content));
		String this_json = this_resp.readEntity(String.class);

		request8JSON(id);
		JsonNode node = mapper.readTree(json);
		int newvalue = 0;
		if (node != null)
			newvalue = node.path("value").asInt();

		start = "Request #10: PUT /";
		request = "person/" + id + "/" + measure_type + "/" + measure_id;
		type = MediaType.APPLICATION_JSON;
		content = MediaType.APPLICATION_JSON;
		result = false;
		resp = this_resp;
		json = this_json;

		if (value == newvalue)
			result = true;

	}

	private static void request11JSON(String id) throws JsonProcessingException, IOException {

		String before = "20-11-2016";
		String after = "10-11-1990";
		measure_type = "weight";
		start = "Request #11: GET /";
		request = "person/" + id + "/" + measure_type;
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		resp = service.path(request).queryParam("before", before).queryParam("after", after).request().accept(type)
				.get();
		json = resp.readEntity(String.class);
		JsonNode node = null;
		int size = 0;
		if (!json.isEmpty()) {
			node = mapper.readTree(json);
			size = node.size();
		}

		if (resp.getStatus() == 200 && size > 0)
			result = true;
		request = "person/" + id + "/" + measure_type + "?before=" + before + "&after=" + after;

	}

	private static void request12JSON() throws JsonProcessingException, IOException {

		String max = "95";
		String min = "85";
		measure_type = "weight";
		start = "Request #12: GET /";
		request = "person";
		type = MediaType.APPLICATION_JSON;
		content = null;
		result = false;

		resp = service.path(request).queryParam("measureType", measure_type).queryParam("max", max)
				.queryParam("min", min).request().accept(type).get();
		json = resp.readEntity(String.class);
		int size = 0;
		if (!json.isEmpty()) {
			JsonNode node = mapper.readTree(json);
			size = node.size();
		}

		if (resp.getStatus() == 200 && size > 0)
			result = true;
		request = "person?measureType=" + measure_type + "&max=" + max + "&min=" + min;

	}

}