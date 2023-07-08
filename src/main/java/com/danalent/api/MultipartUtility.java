package com.danalent.api;

import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.text.MessageFormat.format;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MultipartUtility {
	private static final Logger log = getLogger(MultipartUtility.class.getName());

	private static final String CRLF = "\r\n";
	private static final String CHARSET = "UTF-8";

	private static final int CONNECT_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 10000;

	private final HttpURLConnection connection;
	private final OutputStream outputStream;
	private final PrintWriter writer;
	private final String boundary;

	// for log formatting only
	private final URL url;
	private final long start;
	
	public String cookies = "";
	
	public MultipartUtility(final URL url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		start = currentTimeMillis();
		this.url = url;

		boundary = "---------------------------" + currentTimeMillis();
		
		if(this.url.toString().contains("https://")){
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };
			
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String paramString, SSLSession paramSSLSession) {
					return true;
				}
			});

			connection = (HttpsURLConnection) url.openConnection();
			
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", CHARSET);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			outputStream = connection.getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);			
		}else{
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", CHARSET);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			outputStream = connection.getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);
		}
	}

	public void addFormField(final String name, final String value) {
		writer.append("--").append(boundary).append(CRLF).append("Content-Disposition: form-data; name=\"").append(name).append("\"")
				.append(CRLF)
				.append(CRLF).append(value).append(CRLF);
	}

	public void addFilePart(final String fieldName, final File uploadFile) throws IOException {
		final String fileName = uploadFile.getName();
		writer.append("--").append(boundary).append(CRLF).append("Content-Disposition: form-data; name=\"")
				.append(fieldName).append("\"; filename=\"").append(fileName).append("\"").append(CRLF)
				.append(CRLF)
				.append("Content-Transfer-Encoding: binary").append(CRLF).append(CRLF);

		writer.flush();
		outputStream.flush();
		try (final FileInputStream inputStream = new FileInputStream(uploadFile);) {
			final byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		}

		writer.append(CRLF);
	}

	public void addHeaderField(String name, String value) {
		writer.append(name).append(": ").append(value).append(CRLF);
	}

	public byte[] finish() throws IOException {
		writer.append("--").append(boundary).append("--").append(CRLF);
		writer.close();
		
		final int status = connection.getResponseCode();
		if (status != HTTP_OK) {
			throw new IOException(format("{0} failed with HTTP status: {1}", url, status));
		}

		//cookie DB등록
		saveCookie();
		
		try (final InputStream is = connection.getInputStream()) {
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			final byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				bytes.write(buffer, 0, bytesRead);
			}

			log.log(INFO, format("{0} took {4} ms", url, (currentTimeMillis() - start)));
			return bytes.toByteArray();
		} finally {
			connection.disconnect();
		}
	}
	
	public void saveCookie() throws IOException{
		Map<String, List<String>> imap = connection.getHeaderFields();

		if (imap.containsKey("Set-Cookie")) {
			List<String> lString = imap.get("Set-Cookie");
			for (int i = 0; i < lString.size(); i++) {
				//m_cookies += lString.get(i);
				String[] domain = url.toString().split("/");
				String cookieDomain = domain[2];
				if((lString.get(i).indexOf("PHP") > -1 && lString.get(i).indexOf("delete") < 0) || lString.get(i).indexOf("ECSE") > -1 ){
					cookies += lString.get(i).replace("path=/; domain=."+ cookieDomain +"; HttpOnly", "");
				}
			}
		} else {
			cookies= "";
		}
	}
	
}