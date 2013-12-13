package org.jboss.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.manager.utils.Json;
import org.jboss.resteasy.annotations.Body;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value = "/servers", description = "exposes an interface to configure servers, add, remove, get, list and find")
@Path("/servers")
public class ServersController {
	private Persistence p=new Persistence();
	
	@GET
	@Path("/find")
//	@ApiOperation(value = "lists system properties matching the search criteria", notes = "lists system properties that match the search criteria", response = String.class)
//	@ApiResponses(value = { @ApiResponse(code = 400, message = "Not sure"), @ApiResponse(code = 404, message = "bad") })
	public Response find() throws JsonGenerationException, JsonMappingException, IOException {
		return Response.status(200).entity("NOT IMPLEMENTED").build();
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "lists all the known servers", notes = "", response = List.class)
	public Response list() throws JsonGenerationException, JsonMappingException, IOException {
		// TODO: obfuscate the server passwords
		return Response.status(200).entity(Json.toJson(p.getServers().keySet())).build();
	}

	
	@GET
	@Path("/{id}")
	@ApiOperation(value = "returns the details for the given server {id}", notes = "", response = Server.class)
	public Response get(@PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
		return Response.status(200).entity(Json.toJson(p.getServers().get(id))).build();
	}
	
	@POST
	@Path("/add")
	@ApiOperation(value = "add new server details to be managed", notes = "", response = String.class)
	public Response add(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException {
		String payload=IOUtils.toString(request.getInputStream());
		Server server=(Server)Json.toObject(payload);
		System.out.println("server = "+server);
		Map<String, Server> list=p.getServers();
		boolean updated=list.containsKey(server.getId());
		p.add(server);
		return Response.status(200).entity("{\"response\":\""+(updated?"updated":"added")+"\"}").build();
	}
	
	@DELETE
	@Path("/delete/{id}")
	@ApiOperation(value = "delete a server from the managed list", notes = "", response = String.class)
	public Response delete(@Context HttpServletRequest request, @PathParam(value="id") String id) throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Server> list=p.getServers();
		Server s=list.get(id);
		if (s==null)
			return Response.status(404).entity("{\"response\":\"server not found\"}").build();
		p.remove(id);
		return Response.status(200).entity("{\"response\":\"removed\"}").build();
	}
	
	public static void main(String[] a) throws Exception{
		Server s=new Server();
		s.setId("server12345");
		s.setServer("server12345");
		s.setUsername("mat");
		s.setPassword("mat");
		s.setPort(9990);
		System.out.println(Json.toJson(s));
	}
	
}
