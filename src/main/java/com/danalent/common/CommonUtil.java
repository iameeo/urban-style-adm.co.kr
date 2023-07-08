package com.danalent.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @Author : jaeho
 * @Date : 2017. 3. 6. 오후 1:15:26
 */
public class CommonUtil {

	/**
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {

		if (str != null) {
			str = str.trim();
		}

		return (str == null || "".equals(str));
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static String null2string(String str, String defaultValue) {

		if (isNull(str)) {
			return defaultValue;
		}

		return str;
	}

	/**
	 * randomString
	 * 
	 * @return
	 */
	public static String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * randomString
	 * 
	 * @return
	 */
	public static String getDateString() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(date);
	}
	
	// 실제 서버 물리 경로
	public String getServerRootPath(HttpServletRequest request) {
		String retVal = null;
		retVal = request.getSession().getServletContext().getRealPath("/");
		return retVal;
	}
	
	//string 제외 제거
	public String StringReplace(String str) {
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
		str = str.replaceAll(match, " ");
		return str;
	}

	//htmlTag 제거
	public String removeTag(String html) throws Exception {
		return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	//filedownload
	public String download(String sourceUrl, String targetFilename, String type, int size, String saveDir, String shop, HttpSession session) throws NoSuchAlgorithmException, KeyManagementException {
		String retVal = "";
		if (type.equals("desc")) {
			if(sourceUrl.substring(0,1).equals("/")){
				if(sourceUrl.substring(0,2).equals("//")){
					sourceUrl = "http:" + sourceUrl;
				}else{
					sourceUrl = shop + sourceUrl;	
				}
			}
		}
		
		if(sourceUrl.length() > 0){
			String file_ext = sourceUrl.substring(sourceUrl.lastIndexOf("."), sourceUrl.length());
			FileOutputStream fos = null;
			InputStream is = null;
			String filePathName = saveDir + "/";
			String fileName = targetFilename + "_" + size + file_ext.replace("jpeg","jpg");

			File destdir = new File(filePathName); // 디렉토리 가져오기

			if (!destdir.exists()) {
				destdir.mkdirs(); // 디렉토리가 존재하지 않는다면 생성
			}

			filePathName = filePathName + type + "/";

			File destdir2 = new File(filePathName); // 디렉토리 가져오기

			if (!destdir2.exists()) {
				destdir2.mkdirs(); // 디렉토리가 존재하지 않는다면 생성
			}

			if (type.equals("title")) {
				if (file_ext.indexOf("?") > 0){
					String[] fileName_tmp = file_ext.split("\\?");
					fileName = targetFilename + "_" + size + fileName_tmp[0];
				}else{
					fileName = targetFilename + file_ext;
				}
			}

			try {
				File f = new File(filePathName + fileName);

				if (!f.isFile()) {
					fos = new FileOutputStream(filePathName + fileName);

					if (!sourceUrl.contains("https")) {
						sourceUrl = sourceUrl.replace("http", "https");
					}
					
					//mignonchic 예외 https 사용 안됨.
					if (sourceUrl.contains("mignonchic") || sourceUrl.contains("llsugill.godohosting") || sourceUrl.contains("openhost.shuzz.cafe24.com") || sourceUrl.contains("gimg.ddmadmin.co.kr") || sourceUrl.contains("girlsgoob") ) {
						sourceUrl = sourceUrl.replace("https", "http");
					}

					if(sourceUrl.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
						char[] txtChar = sourceUrl.toCharArray();
					    for (int j = 0; j < txtChar.length; j++) {
					        if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
					            String targetText = String.valueOf(txtChar[j]);
					            try {
					            	sourceUrl = sourceUrl.replace(targetText, URLEncoder.encode(targetText, "UTF-8")).replace(" ", "%20");
					            } catch (UnsupportedEncodingException e) {
					                e.printStackTrace();
					            }
					        } 
					    }
					}	

					URL url = new URL(sourceUrl);

					if(sourceUrl.contains("https://")){
						/* ssl */
						TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return null;
							}

							public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
							}

							public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
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
						HttpsURLConnection connection;
						connection = (HttpsURLConnection) url.openConnection();
						connection.setConnectTimeout(5000);
						is = connection.getInputStream();
						/* ssl */
					}else{
						HttpURLConnection connection;
						connection = (HttpURLConnection) url.openConnection();
						connection.setConnectTimeout(5000);
						is = connection.getInputStream();
					}
					
					byte[] buffer = new byte[1024];
					int readBytes;
					while ((readBytes = is.read(buffer)) != -1) {
						fos.write(buffer, 0, readBytes);
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
					if (is != null) {
						is.close();
					}
					
					//naver
					if (type.equals("desc")) {
						if(session.getAttribute("access_token") != null){
							NaverBlogUtil nbu = new NaverBlogUtil();
							try{
								String uploadUrl = nbu.naverBlogWrite(session.getAttribute("access_token").toString(), filePathName, fileName);
								String[] product_code_arr = uploadUrl.split("/");							
								Document naverBlogView = Jsoup.connect("https://blog.naver.com/PostView.nhn?blogId=urbanstyle_blog&logNo="+product_code_arr[product_code_arr.length - 1]+"&redirect=Dlog&widgetTypeCall=true&directAccess=false").timeout(100000).get();
								retVal = naverBlogView.select("meta[property=og:image]").attr("content");
								
								if (retVal.indexOf("og_default_image") > 0){
									retVal = "";
								}
							}catch(Exception e){
								retVal = "";
								System.out.println(e);
							}
						}
					}
				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		
		return retVal;
	}
}
