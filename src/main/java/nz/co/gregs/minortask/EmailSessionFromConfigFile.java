/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 *
 * @author gregorygraham
 */
public class EmailSessionFromConfigFile {

	private final File configFile;
	private Properties prop;
	private EmailConnectionSettings config = null;

	public EmailSessionFromConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public Session getSession() throws NoEmailConfigurationFound, IOException {
		if (config == null) {
			this.config = parseYAML(configFile, configFile.getName());
			prop = new Properties();
			prop.put("mail.smtp.auth", config.auth);
			prop.put("mail.smtp.starttls.enable", config.starttls);
			prop.put("mail.smtp.host", config.getHost());
			prop.put("mail.smtp.port", config.getPort());
//			prop.put("mail.smtp.ssl.trust", config.getSsltrust());
		}
		Session sess = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.getUsername(), config.getPassword());
			}
		});
		return sess;
	}

	private EmailConnectionSettings parseYAML(File file, String yamlConfigFilename) throws NoEmailConfigurationFound, IOException {
		final YAMLFactory yamlFactory = new YAMLFactory();
		YAMLParser parser = yamlFactory.createParser(file);
		ObjectMapper mapper = new ObjectMapper(yamlFactory);
		EmailConnectionSettings settings = mapper.readValue(parser, EmailConnectionSettings.class);
		return settings;
	}

	public static class EmailConnectionSettings {

		private boolean auth = true;
		private String host = "";
		private String port = "";
		private boolean starttls = true;
		private String ssltrust = "";
		private String username = "";
		private String password = "";

		public EmailConnectionSettings() {
		}

		/**
		 * @return the auth
		 */
		public boolean isAuth() {
			return auth;
		}

		/**
		 * @return the host
		 */
		public String getHost() {
			return host;
		}

		/**
		 * @return the port
		 */
		public String getPort() {
			return port;
		}

		/**
		 * @return the starttls
		 */
		public boolean isStarttls() {
			return starttls;
		}

		/**
		 * @return the ssltrust
		 */
		public String getSsltrust() {
			return ssltrust;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param auth the auth to set
		 */
		public void setAuth(boolean auth) {
			System.out.println("AUTH set to "+auth);
			this.auth = auth;
		}

		/**
		 * @param host the host to set
		 */
		public void setHost(String host) {
			System.out.println("HOST set to "+host);
			this.host = host;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(String port) {
			System.out.println("PORT set to "+port);
			this.port = port;
		}

		/**
		 * @param starttls the starttls to set
		 */
		public void setStarttls(boolean starttls) {
			System.out.println("STARTTLS set to "+starttls);
			this.starttls = starttls;
		}

		/**
		 * @param ssltrust the ssltrust to set
		 */
		public void setSsltrust(String ssltrust) {
			System.out.println("SSLTRUST set to "+ssltrust);
			this.ssltrust = ssltrust;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			System.out.println("USERNAME set to "+username);
			this.username = username;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			System.out.println("PASSWORD set to "+password);
			this.password = password;
		}
	}

	public static class NoEmailConfigurationFound extends Exception {

		public NoEmailConfigurationFound(String yamlConfigFilename) {
		}
	}
}
