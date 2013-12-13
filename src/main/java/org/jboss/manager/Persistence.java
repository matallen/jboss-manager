package org.jboss.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.manager.utils.Json;

import com.google.common.collect.Maps;

public class Persistence {
//	private static Map<String, Server> servers;
	
//	public Persistence(){
//		try {
//			String serversJson = IOUtils.toString(new FileInputStream(new File("target/persistence.json")));
//			servers=new ObjectMapper().readValue(new ByteArrayInputStream(serversJson.getBytes()), Map.class);
//		} catch (Exception e) {
//			servers=Maps.newHashMap();
//		}
//	}
	
	public void add(Server s){
		Map<String, Server> servers=getServers();
		System.out.println("size of getServers() = "+servers.size());
		servers.put(s.getId(), s);
		System.out.println("added 1 more so its now = "+servers.size());
		save(servers);
	}
	
	public void remove(String id) {
		Map<String, Server> servers=getServers();
		servers.remove(id);
		save(servers);
	}
	
	public Map<String, Server> getServers(){
		Map<String, Server> servers;
		try{
			servers=new ObjectMapper().readValue(new ByteArrayInputStream(load().getBytes()), new TypeReference<HashMap<String,Server>>(){});
			return servers;
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Unable to load servers, assuming no persistence file");
			servers=Maps.newHashMap();
		}
		return servers;
	}
	
	private String load() throws FileNotFoundException, IOException{
		return IOUtils.toString(new FileInputStream(new File("target/persistence.json")));
	}
	
	private void save(Map<String, Server> servers){
		try {
			IOUtils.write(Json.toJson(servers), new FileOutputStream(new File("target/persistence.json")));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to save servers config to persistence file");
		}
	}
	
}
