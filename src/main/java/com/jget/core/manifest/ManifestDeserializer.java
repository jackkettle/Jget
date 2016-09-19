package com.jget.core.manifest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ManifestDeserializer
		extends JsonDeserializer<Manifest> {

	@Override
	public Manifest deserialize (JsonParser jsonParser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.getCodec ().readTree (jsonParser);

		String name = node.get ("name").asText ();

		Iterator<JsonNode> iterator = node.get ("rootURLInputs").elements ();
		ArrayList<String> rootURLInputs = new ArrayList<String> ();
		while (iterator.hasNext ()) {
			JsonNode urlNode = iterator.next ();
			if (urlNode == null)
				continue;
			String rootInput = urlNode.asText ();
			if (StringUtils.isEmpty (urlNode.asText ()))
				continue;
			rootURLInputs.add (rootInput);
		}
		iterator = node.get ("seedURLInputs").elements ();
		ArrayList<String> seedURLInputs = new ArrayList<String> ();
		while (iterator.hasNext ()) {
			JsonNode urlNode = iterator.next ();
			if (urlNode == null)
				continue;
			String seedInput = urlNode.asText ();
			if (StringUtils.isEmpty (urlNode.asText ()))
				continue;
			seedURLInputs.add (seedInput);
		}
		Manifest manifest = new Manifest ();
		manifest.setName (name);
		manifest.setRootUrls (rootURLInputs);
		URI urlSeedUri = null;

		JsonNode idNode = node.get ("id");
		if (idNode != null) {
			UUID uid = UUID.fromString (node.get ("id").asText ());
			manifest.setId (uid);
		}

		for (String urlString : seedURLInputs) {

			try {
				urlSeedUri = new URI (urlString);
			}
			catch (URISyntaxException e) {
				continue;
			}
			manifest.getSeeds ().add (urlSeedUri);

		}
		return manifest;
	}

}
