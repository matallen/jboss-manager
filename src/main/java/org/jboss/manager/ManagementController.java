package org.jboss.manager;

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jayway.restassured.response.Header;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "/management", description = "exposes an interface to operate on servers")
@Path("/management")
public class ManagementController {
	private Persistence p=new Persistence();
	private ResourceBundle bundle=ResourceBundle.getBundle("resources");
	
	// stop
	// start
	// deploy
	// get info (jvm, memory, threads, cpu)
	// change logging level
	// status
	
	private String getJbossManagementUrl(Server server){
		return "http://"+server.getServer()+":"+server.getPort()+"/management";
	}
	
	private Server getServer(String id){
		Map<String, Server> servers=p.getServers();
		System.out.println("[status] servers.class.name="+servers.getClass().getName());
		Server server=servers.get(id);
		if (server==null) throw new RuntimeException("Unknown server id ["+id+"]");
		return server;
	}
	
	private com.jayway.restassured.response.Response post(Server server, String bodyContentKey) throws org.apache.http.conn.HttpHostConnectException{
		return 
				given().redirects().follow(true)
				.auth().basic(server.getUsername(), server.getPassword())
				.when()
				.header(new Header("Content-Type", "application/json"))
				.body(bundle.getString(bodyContentKey))
				.post(getJbossManagementUrl(server));
	}
	
	@GET
	@Path("/status/{id}")
	@ApiOperation(value = "returns the status of a server instance", notes = "", response = String.class)
	public Response status(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
//	curl --digest -L -D - http://admin:admin123@localhost:9990/management --header "Content-Type: application/json" -d '{"operation":"read-attribute","name":"server-state","json.pretty":1}'
		
		Server server=getServer(id);
		com.jayway.restassured.response.Response response;
		try{
			response = post(server, "status");
			String responseString = response.getBody().asString();
			
			System.out.println("[status] request.url = " + getJbossManagementUrl(server));
			System.out.println("[status] request.body = " + bundle.getString("status"));
			System.out.println("[status] response.statusline = " + response.statusLine());
			System.out.println("[status] response.content = "+responseString);
			
			if (responseString==null || responseString.length()<=0)
				responseString="{\"outcome\": \"success\", \"result\": \"starting\"}";
			return Response.status(200).entity(responseString).build();
		}catch(org.apache.http.conn.HttpHostConnectException e){
			return Response.status(500).entity("{\"outcome\": \"success\", \"result\": \"stopped\"}").build();
		}
	}
	
	@POST
	@Path("/stop/{id}")
	@ApiOperation(value = "stops (shuts down) a server instance", notes = "", response = String.class)
	public Response stop(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
//		curl --digest -D - http://admin:admin123@localhost:9990/management/ -d '{"operation":"shutdown" , "json.pretty":1}' -HContent-Type:application/json
		
		Server server=getServer(id);
		com.jayway.restassured.response.Response response = post(server, "stop");
		String responseString=response.getBody().asString();
		
		return Response.status(200).entity(responseString).build();
	}
	
	@POST
	@Path("/start/{id}")
	public Response start(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
		return Response.status(200).entity("NOT IMPLEMENTED").build();
	}
	
	@GET
	@Path("/deploy")
	public Response deploy() throws JsonGenerationException, JsonMappingException, IOException {
		return Response.status(200).entity("NOT IMPLEMENTED").build();
	}
	
	@GET
	@Path("/info/{id}")
	public Response info(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
		return Response.status(200).entity("NOT IMPLEMENTED").build();
	}
	@GET
	@Path("/info/env/{id}")
	@ApiOperation(value = "provides environment information about a server instance", notes = "", response = String.class)
	public Response environmentInfo(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
//		curl --digest -D - http://admin:admin123@localhost:9990/management/ -d '{"operation":"read-resource", "include-runtime":"true", "address":[{"core-service":"server-environment"}], "json.pretty":1}' -HContent-Type:application/json
		com.jayway.restassured.response.Response response = post(getServer(id), "info-env");
		String responseString=response.getBody().asString();
		return Response.status(200).entity(responseString).build();
	}
	@GET
	@Path("/info/runtime/{id}")
	@ApiOperation(value = "provides runtime information about a server instance", notes = "", response = String.class)
	public Response runtimeInfo(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
//		curl --digest -D - http://admin:admin123@localhost:9990/management/ -d '{"operation":"read-resource", "include-rutime":"true", "address":[{"core-service":"platform-mbean"},{"type":"runtime"}], "json.pretty":1}' -HContent-Type:application/json
		com.jayway.restassured.response.Response response = post(getServer(id), "info-runtime");
		String responseString=response.getBody().asString();
		return Response.status(200).entity(responseString).build();
	}
	@GET
	@Path("/info/memory/{id}")
	@ApiOperation(value = "provides jvm memory information about a server instance", notes = "", response = String.class)
	public Response memoryInfo(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
//		curl --digest -D - http://admin:admin123@localhost:9990/management/ -d '{"operation":"read-resource", "include-runtime":"true", "address":[{"core-service":"platform-mbean"},{"type":"memory"}], "json.pretty":1}' -HContent-Type:application/json
		com.jayway.restassured.response.Response response = post(getServer(id), "info-memory");
		String responseString=response.getBody().asString();
		return Response.status(200).entity(responseString).build();
	}
			
			
//	
//	@POST
//	@Path("/add")
//	public Response add(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException {
//		String payload=IOUtils.toString(request.getInputStream());
//		System.out.println("payload = "+payload);
//		return Response.status(200).entity("NOT IMPLEMENTED").build();
//	}
//	
//	@DELETE
//	@Path("/delete/{id}")
//	public Response add(@Context HttpServletRequest request, @PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
//		String payload=IOUtils.toString(request.getInputStream());
//		System.out.println("payload (id="+id+") = "+payload);
//		return Response.status(200).entity("NOT IMPLEMENTED").build();
//	}
	
}
