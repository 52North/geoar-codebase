/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.geoar.codebase.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FilenameUtils;
import org.n52.geoar.codebase.CodebaseApplication;
import org.n52.geoar.codebase.CodebaseDatabase;
import org.n52.geoar.codebase.ds.Datasource;
import org.n52.geoar.codebase.util.CodebaseProperties;
import org.n52.geoar.codebase.util.HtmlHelper;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class InfoResource extends ServerResource {

	private static final String GEOAR_PLUGIN_XML_NAME = "geoar-plugin.xml";

	private static final String GEOAR_IMAGE_NAME = "icon.png";

	public static final String FORM_INPUT_DESCRIPTION = "descriptionOfResource";

	public static final String FORM_INPUT_FILE = "fileOfResource";

	public static final String FORM_INPUT_IMAGE_LINK = "imageOfResource";

	public static final String FORM_INPUT_NAME = "nameOfResource";

	public static final String FORM_INPUT_PLATFORM = "targetPlatformOfResource";

	private static Logger log = LoggerFactory.getLogger(InfoResource.class);

	private static final String MSG_NO_RESULT = "No results matching your input!";

	private static final String SERVICE_INFO_CONTEXT_PARAM = "serviceInfo";

	public static final Object FORM_INPUT_IDENTIFIER = "identifierOfResource";

	private String id = null;

	private Collection<Datasource> resources;

	private String serviceInfo;

	private boolean uploadAuthorized = false;

	private boolean deleteAuthorized;

	public InfoResource() {
		log.info("NEW {}", this);

		Series<Parameter> parameters = getApplication().getContext()
				.getParameters();
		this.serviceInfo = parameters.getFirstValue(SERVICE_INFO_CONTEXT_PARAM,
				true);

		CodebaseProperties.getInstance(getApplication());
	}

	/**
	 * Accepts and processes a representation posted to the resource. As
	 * response, the content of the uploaded file is sent back the client.
	 * 
	 * See
	 * http://wiki.restlet.org/docs_2.2/13-restlet/28-restlet/64-restlet.html
	 */
	@Post
	public Representation acceptPost(Representation entity) throws Exception {
		Representation rep = null;

		if (entity == null) {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);

			log.error("POST without multipart content.");
		} else {

			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				rep = handleUpload();
			} else if (MediaType.APPLICATION_WWW_FORM.equals(
					entity.getMediaType(), true)) {
				Form f = new Form(entity);
				Parameter action = f.getFirst(CodebaseApplication.FORM_ACTION);
				if (action != null) {
					String name = action.getName();
					String value = action.getValue();
					log.debug("formAction: {} : {}", name, value);

					if (value.equals(CodebaseApplication.FORM_ACTION_DELETE)) {
						if (!this.deleteAuthorized)
							setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						else
							rep = handleDelete(this.id);
					} else {
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
								"formAction not supported.");
					}
				} else
					log.warn("Unhandled form action: {}", action);
			} else {
				log.warn("Unhandled media type: {}", entity.getMediaType());
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}

		return rep;
	}

	private Representation handleDelete(String identifier) {
		boolean delete = CodebaseDatabase.getInstance().deleteResource(
				identifier);
		Representation rep = null;

		if (delete) {
			setStatus(Status.SUCCESS_OK);
			rep = new StringRepresentation("Resource deleted.",
					MediaType.TEXT_PLAIN);
		} else {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			rep = new StringRepresentation("Resource could not be deleted.",
					MediaType.TEXT_PLAIN);
		}

		return rep;
	}

	private Representation handleUpload() throws FileUploadException, Exception {
		// 1/ Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1000240);

		// 2/ Create a new file upload handler based on the Restlet FileUpload
		// extension that will
		// parse Restlet requests and generates FileItems.
		RestletFileUpload upload = new RestletFileUpload(factory);

		// 3/ Request is parsed by the handler which generates a list of
		// FileItems
		List<FileItem> items = upload.parseRequest(getRequest());

		// Process the uploaded item and the associated fields, then save file
		// on disk

		// get the form inputs
		FileItem dsFileItem = null;

		for (FileItem fi : items) {
			if (fi.getFieldName().equals(FORM_INPUT_FILE)) {
				dsFileItem = fi;
				break;
			}
		}

		if (dsFileItem.getName().isEmpty()) {
			return new StringRepresentation("Missing file!",
					MediaType.TEXT_PLAIN);
		}
		if (!FilenameUtils.getExtension(dsFileItem.getName()).equals(
				CodebaseProperties.APK_FILE_EXTENSION)) {
			return new StringRepresentation("Wrong filename extension",
					MediaType.TEXT_PLAIN);
		}

		PluginDescriptor pluginDescriptor = readPluginDescriptorFromPluginInputStream(dsFileItem
				.getInputStream());
		if (pluginDescriptor == null) {
			return new StringRepresentation(
					"The uploaded plugin has no descriptor ("
							+ GEOAR_PLUGIN_XML_NAME + ")!",
					MediaType.TEXT_PLAIN);
		}
		if (pluginDescriptor.identifier == null) {
			return new StringRepresentation(
					"The uploaded plugin has no identifier!",
					MediaType.TEXT_PLAIN);
		}

		log.debug("Plugin: {}, {}, {}, {}, {}", new Object[] {
				pluginDescriptor.identifier, pluginDescriptor.name,
				pluginDescriptor.description, pluginDescriptor.publisher,
				pluginDescriptor.version });

		// see if it already exists
		CodebaseDatabase db = CodebaseDatabase.getInstance();
		boolean databaseEntryExists = db
				.containsResource(pluginDescriptor.identifier);

		File pluginFile = new File(CodebaseProperties.getInstance().getApkPath(
				pluginDescriptor.identifier));
		File imageFile = new File(CodebaseProperties.getInstance()
				.getImagePath(pluginDescriptor.identifier));
		if (pluginFile.exists()) {
			log.warn("Deleting apk file " + pluginFile.getName());
			if (!pluginFile.delete()) {
				log.error("Could not delete file " + pluginFile.getPath());
			}
		}
		if (imageFile.exists()) {
			log.warn("Deleting image file " + imageFile.getName());
			if (!imageFile.delete()) {
				log.error("Could not delete file " + imageFile.getPath());
			}
		}
		// write the file!
		dsFileItem.write(pluginFile);
		extractPluginImage(pluginFile, imageFile);

		if (databaseEntryExists) {
			db.updateResource(pluginDescriptor.identifier,
					pluginDescriptor.name, pluginDescriptor.description,
					pluginDescriptor.publisher, pluginDescriptor.version);
			return new StringRepresentation("Updated datasource ("
					+ pluginDescriptor.identifier + ").", MediaType.TEXT_PLAIN);
		} else {
			db.addResource(pluginDescriptor.identifier, pluginDescriptor.name,
					pluginDescriptor.description, pluginDescriptor.publisher,
					pluginDescriptor.version);
			return new StringRepresentation("Uploaded datasource ("
					+ pluginDescriptor.identifier + ").", MediaType.TEXT_PLAIN);
		}
	}

	private void extractPluginImage(File pluginFile, File pluginImageFile)
			throws IOException {
		ZipFile zipFile = new ZipFile(pluginFile);
		ZipEntry pluginIconEntry = zipFile.getEntry(GEOAR_IMAGE_NAME);
		if (pluginIconEntry == null) {
			return;
		}

		InputStream inputStream = zipFile.getInputStream(pluginIconEntry);
		OutputStream outputStream = new FileOutputStream(pluginImageFile);

		int read = 0;
		byte[] bytes = new byte[4096];
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}

		zipFile.close();
		outputStream.flush();
		outputStream.close();
	}

	static class PluginDescriptor {
		public PluginDescriptor(String name, String description, Long version,
				String identifier, String publisher) {
			this.name = name;
			this.description = description;
			this.version = version;
			this.identifier = identifier;
			this.publisher = publisher;
		}

		String name;
		String description;
		Long version;
		String identifier;
		String publisher;

		static final Pattern pluginVersionPattern = Pattern
				.compile("-(\\d+(?:\\.\\d+)*)[.-]");

		/**
		 * Parses a version string to long. Assumes that each part of a version
		 * string is < 100.
		 * 
		 * @param version
		 *            Version string, e.g. "1.2.3"
		 * @return long built by multiplying each version component by 100 to
		 *         the power of its position from the back, i.e. "0.0.1" -> 1,
		 *         "0.1.0" -> 100
		 */
		static long parseVersionNumber(String version) {
			String[] split = version.split("\\.");
			long versionNumber = 0;
			for (int i = 0; i < split.length; i++) {
				int num = Integer.parseInt(split[i]);
				if (num < 0 || num >= 100) {
					throw new NumberFormatException(
							"Unable to parse version number, each part may not exceed 100");
				}
				versionNumber += Math.pow(100, (split.length - 1) - i) * num;
			}
			return versionNumber;
		}
	}

	/**
	 * Reads {@link PluginDescriptor} from {@link File} representing a GeoAR
	 * plugin APK. Uses a {@link ZipInputStream} to find and read plugin
	 * descriptor file.
	 * 
	 * 
	 * @param pluginFile
	 * @return
	 * @throws IOException
	 */
	private static PluginDescriptor readPluginDescriptorFromPluginFile(
			File pluginFile) throws IOException {
		ZipFile zipFile = new ZipFile(pluginFile);
		ZipEntry pluginDescriptorEntry = zipFile
				.getEntry(GEOAR_PLUGIN_XML_NAME);
		if (pluginDescriptorEntry == null) {
			return null;
		}
		return readPluginInfoFromDescriptorInputStream(zipFile
				.getInputStream(pluginDescriptorEntry));
	}

	/**
	 * Reads {@link PluginDescriptor} from {@link InputStream} representing a
	 * GeoAR plugin APK. Uses a {@link ZipInputStream} to find and read plugin
	 * descriptor file.
	 * 
	 * @param pluginFileStream
	 * @return
	 * @throws IOException
	 */
	private static PluginDescriptor readPluginDescriptorFromPluginInputStream(
			InputStream pluginFileStream) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(pluginFileStream);

		ZipEntry zipEntry = null;
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			if (zipEntry.getName().equals(GEOAR_PLUGIN_XML_NAME)) {
				return readPluginInfoFromDescriptorInputStream(zipInputStream);
			}
		}
		zipInputStream.close();

		return null;
	}

	/**
	 * Parses a single plugin descriptor file
	 * 
	 * @param inputStream
	 * @return
	 */
	private static PluginDescriptor readPluginInfoFromDescriptorInputStream(
			InputStream inputStream) {
		try {

			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(inputStream);
			// Find name
			String name = null;
			NodeList nodeList = document.getElementsByTagName("name");
			if (nodeList != null && nodeList.getLength() >= 1) {
				name = nodeList.item(0).getTextContent();
			} else {
				log.warn("Plugin Descriptor for does not specify a name");
			}

			// Find publisher
			String publisher = null;
			nodeList = document.getElementsByTagName("publisher");
			if (nodeList != null && nodeList.getLength() >= 1) {
				publisher = nodeList.item(0).getTextContent();
			} else {
				log.warn("Plugin Descriptor for does not specify a publisher");
			}

			// Find description
			String description = null;
			nodeList = document.getElementsByTagName("description");
			if (nodeList != null && nodeList.getLength() >= 1) {
				description = nodeList.item(0).getTextContent();
			} else {
				log.warn("Plugin Descriptor for does not specify a description");
			}

			// Find identifier
			String identifier = null;
			nodeList = document.getElementsByTagName("identifier");
			if (nodeList != null && nodeList.getLength() >= 1) {
				identifier = nodeList.item(0).getTextContent();
			} else {
				log.warn("Plugin Descriptor for does not specify an identifier");
			}

			// Find version
			Long version = null;
			nodeList = document.getElementsByTagName("version");
			if (nodeList != null && nodeList.getLength() >= 1) {
				String versionString = "-" + nodeList.item(0).getTextContent();

				Matcher matcher = PluginDescriptor.pluginVersionPattern
						.matcher(versionString);
				if (matcher.find() && matcher.group(1) != null) {
					try {
						version = PluginDescriptor.parseVersionNumber(matcher
								.group(1));
					} catch (NumberFormatException e) {
						log.error("Plugin filename version invalid: "
								+ matcher.group(1));
					}
				}
			} else {
				log.warn("Plugin Descriptor for does not specify a version");
			}

			if (identifier == null) {
				identifier = name;
			}

			return new PluginDescriptor(name, description, version, identifier,
					publisher);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void doInit() throws ResourceException {
		Reference reference = getReference();
		log.debug("Processing {}.", reference.toString());

		Map<String, Object> requestAttributes = getRequestAttributes();

		this.id = (String) requestAttributes.get(CodebaseApplication.PARAM_ID);
		log.debug("Id: {} {}", this.id);

		String token = getQueryValue(CodebaseApplication.PARAM_TOKEN);
		log.debug("Token: {}", token);

		if (token != null
				&& token.equals(CodebaseProperties.getInstance()
						.getUploadToken()))
			this.uploadAuthorized = true;
		log.debug("Upload authorized: {}",
				Boolean.valueOf(this.uploadAuthorized));

		if (token != null
				&& token.equals(CodebaseProperties.getInstance()
						.getDeleteToken()))
			this.deleteAuthorized = true;
		log.debug("Delete authorized: {}",
				Boolean.valueOf(this.deleteAuthorized));

		// if there is no id given, then return all! > simply return
		// codebase/index.json file!
		if (this.id == null)
			this.resources = CodebaseDatabase.getInstance().getResources();
		else
			this.resources = CodebaseDatabase.getInstance()
					.getResource(this.id);
	}

	@Get("html")
	public Representation toHtml() {
		Reference ref = getReference();
		StringBuilder sb = new StringBuilder();

		HtmlHelper.beforeResult(sb, this.serviceInfo);

		if (this.resources.isEmpty()) {
			sb.append(MSG_NO_RESULT);
		} else {
			for (Datasource ds : this.resources) {
				StringBuilder link = new StringBuilder();
				link.append(ref.getHierarchicalPart());
				if (!ref.getHierarchicalPart().endsWith(ds.getId())) {
					if (!ref.getHierarchicalPart().endsWith("/"))
						link.append("/");
					link.append(ds.getId());
				}
				if (ref.hasQuery()) {
					link.append("?");
					link.append(ref.getQuery());
				}
				HtmlHelper.appendDatasource(sb, ds, link.toString());
			}
		}

		if (this.uploadAuthorized) {
			StringBuilder action = new StringBuilder();
			action.append(ref.getHierarchicalPart());
			// if ( !ref.getHierarchicalPart().endsWith("/"))
			// action.append("/");
			// action.append(CodebaseApplication.ROUTE_UPLOAD);
			if (ref.hasQuery()) {
				action.append("?");
				action.append(ref.getQuery());
			}

			// Am I on a single view page?
			if (this.id != null && this.resources.size() == 1) {
				Datasource r = this.resources.iterator().next();
				log.debug(
						"Only one datasource, filling upload form >>> id (from URL): {}, datasource: {}",
						this.id, r);
				HtmlHelper.appendUploadForm(sb, action.toString());
			} else {
				HtmlHelper.appendUploadForm(sb, action.toString());
			}
		}

		// if is single datasource, offer delete option
		// FIXME this should not be done with POST, but using HTTP DELETE.
		// However, I have no idea how that
		// can be done with a web form, so this would require jQuery - do later!
		if (this.deleteAuthorized && this.id != null
				&& this.resources.size() == 1) {
			StringBuilder action = new StringBuilder();
			action.append(ref.getHierarchicalPart());
			// action.append("/");
			// action.append(this.resources.iterator().next().getId());
			// action.append("/");
			// action.append(CodebaseApplication.ROUTE_DELETE);
			if (ref.hasQuery()) {
				action.append("?");
				action.append(ref.getQuery());
			}

			HtmlHelper.appendDeleteForm(sb, action.toString());
		}

		HtmlHelper.afterResult(sb, this.serviceInfo);

		StringRepresentation representation = new StringRepresentation(
				sb.toString(), MediaType.TEXT_HTML);
		return representation;
	}

	@Get("json")
	public Representation toJson() {
		Map<String, Object> response = new HashMap<String, Object>();

		// http://wiki.fasterxml.com/JacksonInFiveMinutes
		if (this.resources.isEmpty())
			response.put("datasources", MSG_NO_RESULT);
		else
			response.put("datasources", this.resources);
		response.put("service", this.serviceInfo);

		JacksonRepresentation<Map<String, Object>> representation = new JacksonRepresentation<Map<String, Object>>(
				response);
		return representation;
	}

	@Get("txt")
	public Representation toPlain() {
		StringBuilder sb = new StringBuilder();

		sb.append(this.serviceInfo);
		sb.append("\n");
		sb.append("\n");
		if (this.resources.isEmpty()) {
			sb.append(MSG_NO_RESULT);
		} else {
			sb.append("\n");

			for (Datasource ds : this.resources) {
				sb.append(ds.getName());
				sb.append("\n");
				for (int i = 0; i < ds.getName().length(); i++) {
					sb.append("=");
				}
				sb.append("\nId: ");
				sb.append(ds.getId());
				sb.append("\nDescription: ");
				sb.append(ds.getDescription());
				sb.append("\nDownload: ");
				sb.append(CodebaseProperties.getInstance().getApkPath(
						ds.getId()));

				sb.append("\n");
				sb.append("\n");
			}
		}

		StringRepresentation representation = new StringRepresentation(
				sb.toString(), MediaType.TEXT_PLAIN);
		return representation;
	}

}
