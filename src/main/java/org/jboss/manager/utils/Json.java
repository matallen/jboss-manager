package org.jboss.manager.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jboss.manager.Server;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {

	public static String toJson(Object o) throws JsonGenerationException, JsonMappingException, IOException{
		if (o==null) return "<null>";
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, o);
		return new String(baos.toByteArray());
	}

	public static Server toObject(String payload) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(new ByteArrayInputStream(payload.getBytes()), Server.class);
	}
	
}
