package com.danalent.api.web;

import com.danalent.dashboard.dao.DashBoardDao;
import com.danalent.api.MultipartUtility;
import com.danalent.api.MultipartUtilityGD;
import com.danalent.api.MultipartUtility_SS;
import com.danalent.common.CommonUtil;
import com.danalent.common.NaverBlogUtil;

import sun.misc.BASE64Encoder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

//ALTER TABLE combine_product ADD COLUMN isGodo INT NULL DEFAULT '0' AFTER product_price3;

@Controller
public class ApiController {
 private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

 HashMap < String, String > dataMap = new HashMap < String, String > ();

 @Resource(name = "dashBoardDao")
 private DashBoardDao dashBoardDao;

 @Autowired
 MappingJacksonJsonView jsonView;

 Map < String, String > tempCookies;

 CommonUtil commonUtil = new CommonUtil();

 final String logPath = "d:\\log\\";
 final String exePath = logPath + "urban_style_log.exe";

 @RequestMapping(value = "/api/test.api", method = {
  RequestMethod.GET,
  RequestMethod.POST
 })
 public void test(HttpServletRequest request, HttpServletResponse response,
  @RequestParam HashMap < String, Object > paramMap, Model model, HttpSession session) throws Exception {
  try {
   HashMap < String, Object > insertMap = new HashMap < String, Object > ();

   List < HashMap > combineShopList = this.dashBoardDao.test(paramMap);

   for (int i = 0; i < combineShopList.size(); i++) {
    ArrayList < String > colors = new ArrayList < String > ();
    ArrayList < String > sizes = new ArrayList < String > ();

    insertMap.put("seq", combineShopList.get(i).get("seq").toString());

    String str = combineShopList.get(i).get("product_size").toString();
    String test2 = combineShopList.get(i).get("product_color").toString();
    String[] product_split = str.split(",");

    if (str.length() > 0 && test2.length() == 0 && !str.contains("(")) {
     for (int j = 0; j < product_split.length; j++) {
      String str2 = product_split[j];
      String[] final_product_info = str2.split("-");

      if (!colors.contains(final_product_info[0])) {
       colors.add(final_product_info[0]);
      }

      if (!sizes.contains(final_product_info[1])) {
       sizes.add(final_product_info[1]);
      }
     }

     String product_color = colors.toString();
     System.out.println(product_color.replace("[", "").replace("]", "") + ",");
     insertMap.put("product_color", product_color.replace("[", "").replace("]", "") + ",");

     String product_size = sizes.toString();
     System.out.println(product_size.replace("[", "").replace("]", "") + ",");
     insertMap.put("product_size", product_size.replace("[", "").replace("]", "") + ",");

     this.dashBoardDao.test2(insertMap);
    }

   }

  } catch (Exception e) {
   System.out.println(e.getMessage());
  }

  Map < String, Object > map = new HashMap < String, Object > ();

  jsonView.render(map, request, response);
 };

 /**
  * 상품등록
  * 
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping(value = "/api/combineProductRegist.api", method = {
  RequestMethod.GET,
  RequestMethod.POST
 })
 public void combineProductRegist(HttpServletRequest request, HttpServletResponse response, @RequestParam HashMap < String, Object > paramMap, Model model, HttpSession session) throws Exception {

  //System.setProperty("jsse.enableSNIExtension", "false");
  logger.info("combineProductRegist");
  Boolean retVal = false;
  String retMsg = null;
  Integer insertCnt = 0;

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  Calendar c1 = Calendar.getInstance();

  try {
   // 상품번호로 정보 조회
   List < HashMap > combineShopList = this.dashBoardDao.selectCombineShopList(paramMap);

   String strToday = sdf.format(c1.getTime()); // 오늘날짜
   String shopUrl = combineShopList.get(0).get("shop_url").toString(); // 사이트 주소
   String shopName = combineShopList.get(0).get("shop_name").toString(); // 상점코드
   String shopID = combineShopList.get(0).get("shop_id").toString(); // 사이트 아이디
   String shopPW = combineShopList.get(0).get("shop_pw").toString(); // 사이트 비밀번호
   String saveDir = commonUtil.getServerRootPath(request) + shopName; // 상점별  저장 경로

   // 로그인
   String authCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);

   // productUrlList
   Document shopMainDoc = shopMainDoc(shopUrl, shopName, authCookies);
   Elements shopMainProductList = shopMainProductList(shopMainDoc, shopUrl, shopName, authCookies);
   List < String > productLinkArry = productLinkAddArray(shopUrl, shopName, shopMainProductList);
   insertCnt = productDetailInfoInsert(shopUrl, shopName, authCookies, saveDir, strToday, productLinkArry, shopID, shopPW, session);

   retVal = true;
   retMsg = "success";
  } catch (Exception e) {
   retVal = false;
   retMsg = e.getMessage();
  }

  Map < String, Object > map = new HashMap < String, Object > ();
  map.put("status", retVal);
  map.put("msg", retMsg);
  map.put("insertCnt", insertCnt);

  jsonView.render(map, request, response);
 }

 /**
  * 캡쳐
  * 
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping(value = "/api/regist.api", method = {
  RequestMethod.GET,
  RequestMethod.POST
 })
 public void capture(HttpServletRequest request, HttpServletResponse response,
  @RequestParam HashMap < String, Object > paramMap) throws Exception {
  Boolean retVal = false;
  Socket socket = null;
  try {
   socket = new Socket("localhost", 13000);
   retVal = true;
  } catch (Exception e) {
   e.printStackTrace();
  } finally {
   try {
    socket.close();
   } catch (Exception e) {
    e.printStackTrace();
   }
  }

  Map < String, Object > map = new HashMap < String, Object > ();
  map.put("status", retVal);

  jsonView.render(map, request, response);
 }

 /**
  * 목록
  * 
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping("/api/list.api")
 @ResponseBody
 public String captureList(HttpServletRequest request, HttpServletResponse response, Model model,
  @RequestParam HashMap < String, Object > paramMap) throws Exception {
  File folder = new File(logPath);
  File[] listOfFiles = folder.listFiles();

  StringBuilder sb = new StringBuilder();

  for (File file: listOfFiles) {
   if (file.isFile()) {
    if (file.getName().contains(".txt")) {
     sb.append("<a href='/api/view.api?fileName=" + file.getName() + "'>" + file.getName() + "</a>");
    }
   }
  }

  model.addAttribute("page_name", "test");
  return sb.toString();
 }

 /**
  * 상세
  * 
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping("/api/view.api")
 @ResponseBody
 public String captureView(HttpServletRequest request, HttpServletResponse response, Model model,
  @RequestParam HashMap < String, Object > paramMap) throws Exception {
  String fileName = paramMap.get("fileName").toString();

  StringBuilder sb = new StringBuilder();
  List < String > list = Files.readAllLines(Paths.get(logPath + fileName), StandardCharsets.UTF_8);
  Collections.reverse(list);
  for (String line: list) {
   sb.append("<img src='data:image/jpeg;base64," + line + "' style='width:100%;' />");
  }

  model.addAttribute("page_name", "test");
  return sb.toString();
 }

 /**
  * shop -> loginCookie Get
  * 
  * @param shopPW
  * @param shopID
  * @param shopName
  * @param shopUrl
  * @param shopPW
  * @param shopID
  * @param shopName
  * @throws IOException
  * @throws NoSuchAlgorithmException
  * @throws KeyManagementException
  * @throws ParseException
  */
 
 public void HttpsURLConnectionPost(String strURL, String strParams) {
     //HttpsURLConnection은 HttpURLConnection을 상속 받는다.
     //사용 방식은 동일하나 http url과 https url이 다른 부분이다.
     
     try {
         URL url = new URL(strURL);
         HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
         
         conn.setRequestMethod("POST");
         //con.setRequestProperty("Authorization", alpha);
         conn.setRequestProperty("User-Agent", "Mozilla/5.0");
         conn.setRequestProperty("Accept-Language", "ko-kr");
         conn.setRequestProperty("Access-Control-Allow-Origin", "*");
         conn.setRequestProperty("Content-Type", "application/json");
         
         conn.setConnectTimeout(10000); // 커넥션 timeout 10초
         conn.setReadTimeout(5000); //컨텐츠 조회시 timeout 5초
         
         conn.setDoOutput(true); //항상 갱신된 내용 가져오도록 true로 설정
         DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
         wr.writeBytes(strParams); //파라미터 write
         wr.flush();
         wr.close();
         
         //int responseCode = conn.getResponseCode();
         
         Charset charset = Charset.forName("UTF-8");
         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
         
         String inputLine;
         StringBuffer response = new StringBuffer();
         
         while((inputLine = in.readLine()) != null) {
             response.append(inputLine);
         }
         
         in.close();
         
         System.out.println(response.toString());
         
         
     } catch (MalformedURLException e) {
         //URL
         e.printStackTrace();
     } catch (IOException e) {
         //HttpsURLConnection
         e.printStackTrace();
     }
     
 }
 
 private String shopGetCookieLogin(String shopUrl, String shopName, String shopID, String shopPW) throws KeyManagementException, NoSuchAlgorithmException, IOException, ParseException {
  String retVal = "";

  // ddmshu 예외
  if (shopName.equals("ddmshu") || shopName.equals("girlsgoob") || shopName.equals("formyshoe") || shopName.equals("shubasic") || shopName.equals("mojasareo") || shopName.equals("shoesdabang") || shopName.equals("shuline") || shopName.equals("minshu") || shopName.equals("ju-ry") || shopName.equals("modernbox") || shopName.equals("bedding") || shopName.equals("ingdome") || shopName.equals("shoenine") || shopName.equals("ddogakshu") ) {
   URL url = new URL(shopUrl + "/exec/front/Member/login/");

   if(shopName.equals("ddmshu")){
	   final MultipartUtility http = new MultipartUtility(url);
	   http.addFormField("member_id", shopID);
	   http.addFormField("member_passwd", shopPW);
	   http.finish();

	   retVal = http.cookies;
	   retVal = retVal.replace("ECSESSID=; ; SameSite=None; SecureECSESSID=", "ECSESSID=");
	   retVal = retVal.replace(" Secure", " Secure;");	   
   }else{
	   final MultipartUtility http = new MultipartUtility(url);
	   http.addFormField("member_id", shopID);
	   http.addFormField("member_passwd", shopPW);
	   http.finish();

	   retVal = http.cookies;
	   retVal = retVal.replace("ECSESSID=; ; SameSite=None; SecureECSESSID=", "ECSESSID=");
	   retVal = retVal.replace(" Secure", " Secure;");	   
   }

  } else if (shopName.equals("sinsang")) {
   URL url = new URL(shopUrl + "/api/requestLogin.php");

   final MultipartUtility http = new MultipartUtility(url);
   http.addFormField("userid", shopID);
   http.addFormField("passwd", shopPW);
   http.addFormField("autologin", "Y");
   http.addFormField("is_ajax", "1");

   Connection.Response res = Jsoup.connect(shopUrl + "/api/requestLogin.php").data("userid", shopID, "passwd", "cc27364dd663fbce9e875a94927ecca41bf68d54d345db2a4b77d6fcc481090a", "is_ajax", "1", "autologin", "Y").method(Method.POST).execute();

   tempCookies = res.cookies();
   Document doc = res.parse();
   JSONParser jsonParser = new JSONParser();
   JSONObject jsonObj = (JSONObject) jsonParser.parse(doc.select("body").html());
   String oauth_token = jsonObj.get("oauth_token").toString();

   retVal = oauth_token;
  } else if (shopName.equals("themojo")) {
   String url = shopUrl + "/exec/front/Member/login/";

   Response LoginCookies = Jsoup.connect(url).data("member_id", shopID, "member_passwd", shopPW).method(Method.POST).timeout(100000).execute();
   tempCookies = LoginCookies.cookies();
   retVal = tempCookies.toString();
  } else if (shopName.equals("ponpon")) {

   Connection.Response res = Jsoup.connect(shopUrl + "/exec/front/Member/loginKey").method(Method.POST).execute();
   Document doc = res.parse();

   String s_key = doc.select("body").html().substring(doc.select("body").html().indexOf("sKey\":\""), doc.select("body").html().lastIndexOf("\"")).replace("sKey\":\"", "");
   String member_form = "member_form_";
   Random generator = new Random();
   int num1 = generator.nextInt(1000000000) + 719956556;
   member_form = member_form + num1;

   long unixTime = System.currentTimeMillis() / 1000 ;

   Connection.Response res_captcha = Jsoup.connect(shopUrl + "/exec/front/Member/CheckCaptcha").method(Method.POST).execute();
   Document doc_captcha = res_captcha.parse();

   String temp = "https://login2.cafe24ssl.com/crypt/AuthSSLManagerV2.php?auth_mode=encrypt&auth_callbackName=AuthSSL.encryptSubmit_Complete&auth_string=%7B%22" + member_form + "%3A%3Amember_id%22%3A%22" + shopID + "%22%2C%22" + member_form + "%3A%3Amember_passwd%22%3A%22" + shopPW + "%22%2C%22" + member_form + "%3A%3AreturnUrl%22%3A%22http%253A%252F%252Fwww.ponpon.or.kr%252F%22%2C%22" + member_form + "%3A%3AforbidIpUrl%22%3A%22%252Findex.html%22%2C%22" + member_form + "%3A%3Ause_login_keeping%5B%5D%22%3A%22%22%2C%22" + member_form + "%3A%3AcertificationUrl%22%3A%22%252Fintro%252Fadult_certification.html%253FreturnUrl%253D%22%2C%22" + member_form + "%3A%3AsIsSnsCheckid%22%3A%22%22%2C%22" + member_form + "%3A%3AsProvider%22%3A%22%22%2C%22" + member_form + "%3A%3Acheck_save_id%22%3A%22%22%2C%22" + member_form + "%3A%3Ause_login_keeping%22%3A%22%22%2C%22" + member_form + "%3A%3AsLoginKey%22%3A%22" + s_key + "%22%7D&dummy=" + unixTime;
   Connection.Response res2 = Jsoup.connect(temp).method(Method.GET).execute();
   Document doc2 = res2.parse();
   String encrypt = doc2.select("body").html().substring(doc2.select("body").html().indexOf("AuthSSL.encryptSubmit_Complete('"), doc2.select("body").html().lastIndexOf("'")).replace("AuthSSL.encryptSubmit_Complete('", "");

   URL url = new URL(shopUrl.replace("http", "https") + "/exec/front/Member/login/");

   final MultipartUtility http = new MultipartUtility(url);
   http.addFormField("returnUrl", "");
   http.addFormField("forbidIpUrl", "");
   http.addFormField("certificationUrl", "");
   http.addFormField("sIsSnsCheckid", "");
   http.addFormField("sProvider", "");
   http.addFormField("member_id", "");
   http.addFormField("member_passwd", "");
   http.addFormField("sLoginKey", "");
   http.addFormField("encrypted_str", encrypt);
   http.finish();
   retVal = http.cookies;
  } else if (shopName.equals("viviopera")) {

   Connection.Response res = Jsoup.connect(shopUrl + "/exec/front/Member/loginKey").method(Method.POST).execute();
   Document doc = res.parse();

   String s_key = doc.select("body").html().substring(doc.select("body").html().indexOf("sKey\":\""), doc.select("body").html().lastIndexOf("\"")).replace("sKey\":\"", "");
   String member_form = "member_form_";
   Random generator = new Random();
   int num1 = generator.nextInt(1000000000) + 719956556;
   member_form = member_form + num1;

   long unixTime = System.currentTimeMillis() / 1000 ;

   Connection.Response res_captcha = Jsoup.connect(shopUrl + "/exec/front/Member/CheckCaptcha").method(Method.POST).execute();
   Document doc_captcha = res_captcha.parse();

   // member_form_5250504551%3A%3AcertificationUrl%22%3A%22%252Fintro%252Fadult_certification.html%253FreturnUrl%253D%22%2C%22member_form_5250504551%3A%3AsIsSnsCheckid%22%3A%22%22%2C%22member_form_5250504551%3A%3AsProvider%22%3A%22%22%2C%22member_form_5250504551%3A%3Acheck_save_id%22%3A%22T%22%2C%22member_form_5250504551%3A%3Ause_login_keeping%22%3A%22%22%2C%22member_form_5250504551%3A%3AsLoginKey%22%3A%2271e7dfdf1d409d2a94dfbd56e1f4806b%22%7D&dummy=1555550212438

   String temp = "https://login2.cafe24ssl.com/crypt/AuthSSLManagerV2.php?auth_mode=encrypt&auth_callbackName=AuthSSL.encryptSubmit_Complete&auth_string=%7B%22" + member_form + "%3A%3Amember_id%22%3A%22" + shopID + "%22%2C%22" + member_form + "%3A%3Amember_passwd%22%3A%22" + shopPW + "%22%2C%22" + member_form + "%3A%3AreturnUrl%22%3A%22http%253A%252F%252Fwww.viviopera.co.kr%252F%22%2C%22" + member_form + "%3A%3AforbidIpUrl%22%3A%22%252Findex.html%22%2C%22" + member_form + "%3A%3Ause_login_keeping%5B%5D%22%3A%22%22%2C%22" + member_form + "%3A%3AcertificationUrl%22%3A%22%252Fintro%252Fadult_certification.html%253FreturnUrl%253D%22%2C%22" + member_form + "%3A%3AsIsSnsCheckid%22%3A%22%22%2C%22" + member_form + "%3A%3AsProvider%22%3A%22%22%2C%22" + member_form + "%3A%3Acheck_save_id%22%3A%22%22%2C%22" + member_form + "%3A%3Ause_login_keeping%22%3A%22%22%2C%22" + member_form + "%3A%3AsLoginKey%22%3A%22" + s_key + "%22%7D&dummy=" + unixTime;
   Connection.Response res2 = Jsoup.connect(temp).method(Method.GET).execute();
   Document doc2 = res2.parse();
   String encrypt = doc2.select("body").html().substring(doc2.select("body").html().indexOf("AuthSSL.encryptSubmit_Complete('"), doc2.select("body").html().lastIndexOf("'")).replace("AuthSSL.encryptSubmit_Complete('", "");

   URL url = new URL(shopUrl.replace("http", "https") + "/exec/front/Member/login/");

   final MultipartUtility http = new MultipartUtility(url);
   http.addFormField("returnUrl", "");
   http.addFormField("forbidIpUrl", "");
   http.addFormField("certificationUrl", "");
   http.addFormField("sIsSnsCheckid", "");
   http.addFormField("sProvider", "");
   http.addFormField("member_id", "");
   http.addFormField("member_passwd", "");
   http.addFormField("sLoginKey", "");
   http.addFormField("encrypted_str", encrypt);
   http.finish();
   retVal = http.cookies;
  } else if (shopName.equals("mignonchic")) {
   String url = shopUrl + "/member/login_ps.php";

   Response LoginCookies = Jsoup.connect(url).data("loginId", shopID, "loginPwd", shopPW, "mode", "login").method(Method.POST).timeout(100000).execute();
   tempCookies = LoginCookies.cookies();
   retVal = tempCookies.toString();
  } else if (shopName.equals("sunnylike")) {
   String url = shopUrl + "/";

   Connection.Response res = Jsoup.connect(url).method(Method.GET).execute();

   tempCookies = res.cookies();
   retVal = tempCookies.toString();

   url = shopUrl + "/LoginOk";

   Connection.Response res2 = Jsoup.connect(url).cookies(tempCookies).data("mem_type", "A", "mem_userid", shopID, "mem_userpwd", shopPW, "mem_savechk", "true", "user_id", shopID, "user_pwd", shopPW, "save_id1", "", "user_id", "", "user_pwd", "").method(Method.POST).execute();

   /*
    * tempCookies = res2.cookies(); retVal = tempCookies.toString();
    */
  }

  return retVal;
 }

 /**
  * 상점 메인 페이지 로그인 후 document
  * 
  * @param shopUrl
  * @param shopName
  * @param authCookies
  * @return
  * @throws IOException
  */
 private Document shopMainDoc(String shopUrl, String shopName, String authCookies) throws IOException {
  Document retVal = null;

  // ddmshu 예외
  if (shopName.equals("ddmshu") || shopName.equals("girlsgoob")) {
   retVal = null;  
  } else if (shopName.equals("formyshoe") || shopName.equals("shubasic") || shopName.equals("mojasareo") || shopName.equals("shoesdabang") || shopName.equals("shuline") || shopName.equals("minshu") || shopName.equals("ju-ry") || shopName.equals("ponpon") || shopName.equals("modernbox") || shopName.equals("mignonchic") || shopName.equals("bedding") || shopName.equals("viviopera") || shopName.equals("shoenine") || shopName.equals("ddogakshu")) {
   retVal = Jsoup.connect(shopUrl).cookie("cookie", authCookies).timeout(100000).get();
  } else if (shopName.equals("sinsang")) {
   retVal = Jsoup.connect(shopUrl + "/api/getSChoiceGoodsList?userid=lmychoicel&display_count=0&query_count=100&oauth_token=" + authCookies).cookies(tempCookies).timeout(100000).get();
  } else if (shopName.equals("themojo")) {
   retVal = Jsoup.connect(shopUrl + "/product/list.html?cate_no=39").cookies(tempCookies).timeout(100000).get();
  } else if (shopName.equals("ingdome")) {
   retVal = Jsoup.connect(shopUrl + "/product/list.html?cate_no=86").cookie("cookie", authCookies).timeout(100000).get();
  } else if (shopName.equals("sunnylike")) {
   retVal = Jsoup.connect(shopUrl + "/jGlist").userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.sunnylike.co.kr").ignoreContentType(true).data("page", "1", "ipp", "32", "hp_memid", "DB1A101200", "hp_domain", "http://www.sunnylike.co.kr&").cookies(tempCookies).timeout(100000).get();
  }

  return retVal;
 }

 /**
  * 상품 html parser
  * 
  * @param shopMainDoc
  * @param shopUrl
  * @param shopName
  * @param authCookies
  * @return
  * @throws ParseException
  */
 private Elements shopMainProductList(Document shopMainDoc, String shopUrl, String shopName, String authCookies) throws ParseException {
  Elements retVal = null;

  // ddmshu 예외
  if (shopName.equals("ddmshu") || shopName.equals("girlsgoob")) {
   retVal = null;
  } else if (shopName.equals("sinsang")) {
   retVal = shopMainDoc.select("body");
  } else if (shopName.equals("girlsgoob")) {
   //retVal = shopMainDoc.select("ul.df-prl-items_grid_4 a");
  } else if (shopName.equals("formyshoe")) {
   retVal = shopMainDoc.select("div[class=thumbnail] a");
  } else if (shopName.equals("shubasic")) {
   retVal = shopMainDoc.select("div[class=thumbnail] a");
  } else if (shopName.equals("themojo")) {
   retVal = shopMainDoc.select("ul.prdList .box p a");
  } else if (shopName.equals("mojasareo")) {
   retVal = shopMainDoc.select(".box p a");
  } else if (shopName.equals("shoesdabang")) {
   retVal = shopMainDoc.select(".main_thumbnail a");
  } else if (shopName.equals("shuline")) {
   retVal = shopMainDoc.select("div[class=df-prl-fadearea] a");
  } else if (shopName.equals("minshu")) {
   retVal = shopMainDoc.select("ul.prdList a");
  } else if (shopName.equals("ju-ry")) {
   retVal = shopMainDoc.select(".description a");
  } else if (shopName.equals("ponpon")) {
   retVal = shopMainDoc.select("div[class=thumbnail] a");
  } else if (shopName.equals("modernbox")) {
   retVal = shopMainDoc.select("div[class=thumbnail] a");
  } else if (shopName.equals("mignonchic")) {
   retVal = shopMainDoc.select("div[class=item_photo_box] a");
  } else if (shopName.equals("sunnylike")) {
   retVal = shopMainDoc.select("body");
  } else if (shopName.equals("bedding")) {
   retVal = shopMainDoc.select(".box p a");
  } else if (shopName.equals("ingdome")) {
   retVal = shopMainDoc.select("a[class=prdImg]");
  } else if (shopName.equals("viviopera")) {
   retVal = shopMainDoc.select("ul.prdList a");
  } else if (shopName.equals("shoenine")) {
   retVal = shopMainDoc.select("ul.prdList a");
  } else if (shopName.equals("ddogakshu")){
   retVal = shopMainDoc.select("div.contenthover a");  
  }

  return retVal;
 }

 /**
  * 상품 정보 페이지 배열 추가
  * 
  * @param shopUrl
  * @param shopName
  * @param shopMainProductList
  * @return
  * @throws ParseException
 * @throws IOException 
  */
 private List < String > productLinkAddArray(String shopUrl, String shopName, Elements shopMainProductList) throws ParseException, IOException {
  List < String > retArray = new ArrayList < > ();

  // ddmshu 예외
  if (shopName.equals("formyshoe") || shopName.equals("shubasic") || shopName.equals("themojo") || shopName.equals("mojasareo") || shopName.equals("shoesdabang") || shopName.equals("shuline") || shopName.equals("minshu") || shopName.equals("ju-ry") || shopName.equals("ponpon") || shopName.equals("modernbox") || shopName.equals("mignonchic") || shopName.equals("bedding") || shopName.equals("ingdome") || shopName.equals("viviopera") || shopName.equals("shoenine") || shopName.equals("ddogakshu") ) {
   for (Element shopMainProductLink: shopMainProductList) {
    String productLink = shopMainProductLink.attr("href");

    if (shopName.equals("girlsgoob") || shopName.equals("ddmshu") || shopName.equals("formyshoe") || shopName.equals("shoesdabang") || shopName.equals("shuline") || shopName.equals("minshu") || shopName.equals("ju-ry") || shopName.equals("ponpon")) {
     if (productLink.indexOf("product") > -1) {
      retArray.add(productLink);
     }
    } else if (shopName.equals("mignonchic")) {
     if (productLink.indexOf("goods_view.php") > -1) {
      retArray.add(productLink.replace("..", ""));
     }
    } else {
     if (productLink.indexOf("detail.html") > -1) {
      retArray.add(productLink);
     }
    }
   }
  } else if (shopName.equals("sinsang")) {
   JSONParser jsonParser = new JSONParser();
   JSONObject jsonObject = (JSONObject) jsonParser.parse(shopMainProductList.select("body").html());
   JSONArray productInfoArray = (JSONArray) jsonObject.get("list");

   for (int i = 0; i < productInfoArray.size(); i++) {
    JSONObject bookObject = (JSONObject) productInfoArray.get(i);
    String gid = bookObject.get("gid").toString();
    retArray.add(gid);
   }
  } else if (shopName.equals("sunnylike")) {
   JSONParser jsonParser = new JSONParser();

   JSONArray ja = (JSONArray) jsonParser.parse(shopMainProductList.toString().substring(shopMainProductList.toString().indexOf("["), shopMainProductList.toString().length() - shopMainProductList.toString().indexOf("[")));
   for (int i = 0; i < ja.size(); i++) {
    JSONObject bookObject = (JSONObject) ja.get(i);
    String gid = bookObject.get("gd_no").toString();
    retArray.add(gid);
   }
  } else if (shopName.equals("ddmshu")) {
	  
	  for(int z=1; z <= 5; z++){
		   Response res = Jsoup.connect("https://ddmshu.com/exec/front/Product/ApiProductMain?display_group=2&supplier_code=S0000000&page="+ z +"&bInitMore=F&count=12").method(Method.GET).ignoreContentType(true).timeout(100000).execute();
		   JSONParser jsonParser = new JSONParser();
		   JSONObject jsonObject = (JSONObject) jsonParser.parse(res.body());
		   JSONObject jsonObject2 = (JSONObject) jsonObject.get("rtn_data");
		   JSONArray productInfoArray = (JSONArray) jsonObject2.get("data");

		   for (int i = 0; i < productInfoArray.size(); i++) {
		    JSONObject bookObject = (JSONObject) productInfoArray.get(i);
		    String gid = bookObject.get("link_product_detail").toString();
		    retArray.add(gid);
		   }
	  }
  } else if (shopName.equals("girlsgoob")) {
	  
	  for(int z=1; z <= 5; z++){
		   Response res = Jsoup.connect("http://girlsgoob.com/exec/front/Product/ApiProductMain?display_group=3&supplier_code=S0000000&page="+ z +"&bInitMore=F&count=12").method(Method.GET).ignoreContentType(true).timeout(100000).execute();
		   JSONParser jsonParser = new JSONParser();
		   JSONObject jsonObject = (JSONObject) jsonParser.parse(res.body());
		   JSONObject jsonObject2 = (JSONObject) jsonObject.get("rtn_data");
		   JSONArray productInfoArray = (JSONArray) jsonObject2.get("data");

		   for (int i = 0; i < productInfoArray.size(); i++) {
		    JSONObject bookObject = (JSONObject) productInfoArray.get(i);
		    String gid = bookObject.get("link_product_detail").toString();
		    retArray.add(gid);
		   }
	  }
  }

  return retArray;
 }

 private Integer productDetailInfoInsert(String shopUrl, String shopName, String authCookies, String saveDir, String strToday, List < String > productLinkArry, String shopID, String shopPW, HttpSession session) throws Exception {
  Integer retVal = 0;
  Integer isProduct = 0;

  HashMap < String, Object > insertMap = new HashMap < String, Object > ();
  insertMap.put("product_shop", shopName);

  for (int i = 0; i < productLinkArry.size(); i++) {
   String detailFullUrl = null;
   // ddmshu 예외
   if (shopName.equals("ddmshu") || shopName.equals("girlsgoob") || shopName.equals("formyshoe") || shopName.equals("shubasic") || shopName.equals("themojo") || shopName.equals("mojasareo") || shopName.equals("shoesdabang") || shopName.equals("shuline") || shopName.equals("minshu") || shopName.equals("ju-ry") || shopName.equals("ponpon") || shopName.equals("modernbox") || shopName.equals("mignonchic") || shopName.equals("bedding") || shopName.equals("ingdome") || shopName.equals("viviopera") || shopName.equals("shoenine") || shopName.equals("ddogakshu") ) {
    detailFullUrl = shopUrl + productLinkArry.get(i).toString();
   } else if (shopName.equals("sinsang")) {
    detailFullUrl = shopUrl + "/api/getSChoiceGoodsData?userid=lmychoicel&gid=" + productLinkArry.get(i).toString() + "&oauth_token=" + authCookies;
   } else if (shopName.equals("sunnylike")) {
    detailFullUrl = shopUrl + "/jGinfo?gd_no=" + productLinkArry.get(i).toString();
   }

   Document productDetailDoc = null;

   // ddmshu 예외
   if (shopName.equals("ddmshu")) {
	   
    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();
    insertMap.put("product_code", productDetailDoc.select("meta[property=og:title]").eq(1).attr("content"));
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(1).attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(1).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("ul[ec-dev-id=product_option_id1] > li")) {
       product_size = product_size + options.text() + ",";
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("ul[ec-dev-id=product_option_id2] > li")) {
       product_color = product_color + options.text() + ",";
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);
     
     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("ec-data-src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("girlsgoob")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();
    insertMap.put("product_code", productDetailDoc.select("meta[property=og:title]").attr("content").replace(" - 걸스굽", ""));
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content").replace(" - 걸스굽", ""));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.attr("value") + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.attr("value") + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("formyshoe")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();
    insertMap.put("product_code", productDetailDoc.select("meta[property=og:title]").attr("content"));
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.attr("value") + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.attr("value") + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("shubasic")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();
    insertMap.put("product_code", productDetailDoc.select("meta[property=og:title]").attr("content").replace(" - 슈베이직[SHOE BASIC]", ""));
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content").replace(" - 슈베이직[SHOE BASIC]", ""));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.attr("value") + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.attr("value") + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("ec-data-src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("themojo")) {

    productDetailDoc = Jsoup.connect(detailFullUrl).cookies(tempCookies).timeout(100000).get();
    insertMap.put("product_code", productDetailDoc.select("div.infoArea h3").text().replace(" (해외배송 가능상품)", "").replace(" ", ""));
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", insertMap.get("product_code"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("strong[id=span_product_price_text]").text().replace(",", "").replace("원", ""));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("mojasareo")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");
    insertMap.put("product_code", "mj" + product_code_arr[product_code_arr.length - 1]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", commonUtil.StringReplace(productDetailDoc.select("div[class=infoArea]").select("h3").text()).replace("  해외배송 가능상품 ", "").replace("   ", ""));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(1).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("shoesdabang")) {

    detailFullUrl = java.net.URLDecoder.decode(detailFullUrl, "UTF-8");
    detailFullUrl = detailFullUrl.contains("http") ? detailFullUrl : detailFullUrl + detailFullUrl.substring(1);

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    insertMap.put("product_code", productDetailDoc.select("th[scope=row]").eq(3).parents().select("td").eq(0).text());
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[class=cont]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("shuline")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");

    //insertMap.put("product_code", "sl" + product_code_arr[product_code_arr.length - 1]);
    insertMap.put("product_code", productDetailDoc.select("meta[property=og:title]").eq(1).attr("content"));
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(1).attr("content"));
     
     if (productDetailDoc.select("table").eq(1).select("tr").eq(4).select("span").text().indexOf(",") > -1 && productDetailDoc.select("table").eq(1).select("tr").eq(4).select("span").text().indexOf("원") > -1){
    	 insertMap.put("product_new_title", productDetailDoc.select("table").eq(1).select("tr").eq(3).select("span").text());	 
     }else{
    	 insertMap.put("product_new_title", productDetailDoc.select("table").eq(1).select("tr").eq(4).select("span").text());
     }
     
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(1).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select").eq(0).select("option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     ArrayList < String > colors = new ArrayList < String > ();
     ArrayList < String > sizes = new ArrayList < String > ();

     // 색상
     String product_color = "";

     String str = product_size;
     String[] product_split = str.split(",");

     if (str.length() > 0 && !str.contains("(")) {
      for (int j = 0; j < product_split.length; j++) {
       String str2 = product_split[j];
       String[] final_product_info = str2.split("-");

       if (!colors.contains(final_product_info[0])) {
        colors.add(final_product_info[0]);
       }

       if (!sizes.contains(final_product_info[1])) {
        sizes.add(final_product_info[1]);
       }
      }
     }

     product_color = colors.toString();
     // System.out.println(product_color.replace("[",
     // "").replace("]", "") + ",");
     insertMap.put("product_color", product_color.replace("[", "").replace("]", "") + ",");

     product_size = sizes.toString();
     // System.out.println(product_size.replace("[",
     // "").replace("]", "") + ",");
     insertMap.put("product_size", product_size.replace("[", "").replace("]", "") + ",");

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[class=cont]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      //String imgs_url = imgs.attr("ec-data-src");
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }
     
     //imgCombine(saveDir, insertMap, z);

     retVal++;
    }
   } else if (shopName.equals("minshu")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    insertMap.put("product_code", productDetailDoc.select("th[scope=row]").eq(3).parents().select("td").eq(0).text());
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[class=cont]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("ju-ry")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    System.out.println(productDetailDoc.select("th[scope=row]"));

    insertMap.put("product_code", productDetailDoc.select("th[scope=row]").eq(1).parents().select("td").eq(0).text());
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(1).attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_price2", productDetailDoc.select("meta[property=product:sale_price:amount]").attr("content"));
     insertMap.put("product_price3", productDetailDoc.select("span[id=span_product_price_custom]").text().replace("원", "").replace(",", ""));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");

      // 예외파일
      if (imgs_url.contains("delivery.jpg") || imgs_url.contains("don.jpg") || imgs_url.contains("guide.jpg") || imgs_url.contains("delivery_page.jpg") || imgs_url.contains("detail_end.jpg")) {

      } else {
       insertMap.put("product_img_url", imgs_url);
       insertMap.put("product_img_sort", z);
       this.dashBoardDao.insertCombineDetailImg(insertMap);
       String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

       // update
       if (naverNewImg != "") {
        insertMap.put("product_new_img_url", naverNewImg);
        this.dashBoardDao.updateCombineNaverImg(insertMap);
       }

       z++;
      }
     }

     retVal++;
    }
   } else if (shopName.equals("sinsang")) {
    insertMap.put("product_code", productLinkArry.get(i).toString());
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     productDetailDoc = Jsoup.connect(detailFullUrl).cookies(tempCookies).timeout(100000).ignoreContentType(true).get();

     JSONParser jsonParser = new JSONParser();
     String sb = productDetailDoc.select("body").html().replace("<br \\>", " ").replaceAll(System.getProperty("line.separator"), " ").replaceAll("(\r\n|\r|\n|\n\r)", " ");
     JSONObject jsonDetailObject = (JSONObject) jsonParser.parse(commonUtil.removeTag(sb));

     // books의 배열을 추출
     JSONObject jsonObj2 = (JSONObject) jsonDetailObject.get("gdata");

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_text", "< div style=text-align:center; >" + jsonObj2.get("intro").toString().replace("\n", "< br/ >").substring(0, jsonObj2.get("intro").toString().replace("\n", "< br/ >").indexOf("*")) + "< /div >");
     insertMap.put("product_title", jsonObj2.get("goodsName").toString());
     insertMap.put("product_price", jsonObj2.get("price").toString().replace("원", "").replace(",", ""));
     insertMap.put("product_color", jsonObj2.get("color").toString().replace(" ", ","));
     insertMap.put("product_size", jsonObj2.get("size").toString().replace(" ", ","));
     insertMap.put("product_price2", jsonObj2.get("categoryName").toString());
     insertMap.put("product_price3", "");

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     JSONArray detailImgs = (JSONArray) jsonObj2.get("goodsImages");
     JSONObject detailimg = (JSONObject) detailImgs.get(2);
     String img_url_title = detailimg.get("imageUrl").toString() + detailimg.get("filename").toString();

     insertMap.put("product_thumbImg", img_url_title);

     int z = 0;
     for (int j = 0; j < detailImgs.size(); j++) {
      JSONObject detailimgDesc = (JSONObject) detailImgs.get(j);
      String img_url_desc = detailimgDesc.get("imageUrl").toString() + detailimgDesc.get("filename").toString();

      if (detailImgs.size() <= 2) {
       if (j == 0) {
        commonUtil.download(img_url_desc, insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
       }
      } else {
       if (j == 2) {
        commonUtil.download(img_url_desc, insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
       }
      }

      String imgs_url = img_url_desc;
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("ponpon")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    authAgainCookies = authAgainCookies.replace("path=/; domain=.ponpon.or.kr; HttpOnly", "");
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");

    insertMap.put("product_code", "pp" + product_code_arr[product_code_arr.length - 1]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.attr("value") + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > optgroup")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("ec-data-src");

      // 예외파일
      if (imgs_url.indexOf("ED8FB0ED8FB020770") > -1 || imgs_url.indexOf("배송안내") > -1 || imgs_url.indexOf("KakaoTalk") > -1 || imgs_url.indexOf("ED8FB0ED8FB0EBB0B0EC86A1EC9588EB82B4") > -1) {

      } else {
       insertMap.put("product_img_url", imgs_url);
       insertMap.put("product_img_sort", z);
       this.dashBoardDao.insertCombineDetailImg(insertMap);
       String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

       // update
       if (naverNewImg != "") {
        insertMap.put("product_new_img_url", naverNewImg);
        this.dashBoardDao.updateCombineNaverImg(insertMap);
       }

       z++;
      }
     }

     retVal++;
    }
   } else if (shopName.equals("modernbox")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");

    insertMap.put("product_code", product_code_arr[product_code_arr.length - 1]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(1).attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(1).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > optgroup")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      insertMap.put("product_img_url", imgs_url);
      insertMap.put("product_img_sort", z);
      this.dashBoardDao.insertCombineDetailImg(insertMap);
      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

      // update
      if (naverNewImg != "") {
       insertMap.put("product_new_img_url", naverNewImg);
       this.dashBoardDao.updateCombineNaverImg(insertMap);
      }

      z++;
     }

     retVal++;
    }
   } else if (shopName.equals("mignonchic")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("meta[property=og:image]").eq(0).attr("content");
    String[] product_code_arr = product_code.split("/");

    insertMap.put("product_code", product_code_arr[product_code_arr.length - 2]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(0).attr("content"));
     insertMap.put("product_text", "");
     insertMap.put("product_price", productDetailDoc.select("input[name=set_goods_price]").val());
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(0).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈 & 색상
     List < String > product_option1 = new ArrayList < String > ();
     List < String > product_option2 = new ArrayList < String > ();
     List < String > product_option3 = new ArrayList < String > ();
     for (Element options: productDetailDoc.select("select[name=optionNo_0] > option")) {
      if (options.attr("value") != "") {
       String size = options.text();

       if (!product_option1.contains(size)) {
        product_option1.add(size);
       }

       // 비동기 제품별 기종
       Connection.Response res = Jsoup.connect("http://mignonchic.com/goods/goods_ps.php").data("mode", "option_select", "optionVal[]", size, "optionKey", "0", "goodsNo", insertMap.get("product_code").toString(), "mileageFl", "c").cookie("cookie", authAgainCookies).timeout(100000).method(Method.POST).execute();

       Document doc = res.parse();
       JSONParser jsonParser = new JSONParser();
       JSONObject jsonObj = (JSONObject) jsonParser.parse(doc.select("body").html());

       JSONArray devices = (JSONArray) jsonObj.get("nextOption");
       for (int j = 0; j < devices.size(); j++) {
        // 비동기 기종별 색상
        Connection.Response res2 = Jsoup.connect("http://mignonchic.com/goods/goods_ps.php").data("mode", "option_select", "optionVal[]", size, "optionVal[]", devices.get(j).toString(), "optionKey", "1", "goodsNo", insertMap.get("product_code").toString(), "mileageFl", "c").cookie("cookie", authAgainCookies).timeout(100000).method(Method.POST).execute();

        Document doc2 = res2.parse();
        JSONParser jsonParser2 = new JSONParser();
        JSONObject jsonObj2 = (JSONObject) jsonParser2.parse(doc2.select("body").html());

        JSONArray colors = (JSONArray) jsonObj2.get("nextOption");
        product_option2.addAll(colors);
        JSONArray addPrices = (JSONArray) jsonObj2.get("optionPrice");
        product_option3.add(devices.get(j).toString());

        if (colors.size() == 0) {
         // product_size = product_size + "" + size +
         // " - " + devices.get(j).toString() +
         // ((devices.size()-1) != j ? "," : "");
        } else {
         for (int z = 0; z < colors.size(); z++) {
          // product_size = product_size + "" +
          // size + " - " +
          // devices.get(j).toString() +
          // ((devices.size()-1) != j ? "," :
          // "")+" - "+ colors.get(z).toString()
          // +"("+
          // addPrices.get(z).toString().replace(".00",
          // "") +")" + ((colors.size()-1) != z ?
          // "," : "");
          // product_size = product_size + " - "+
          // colors.get(z).toString() +"("+
          // addPrices.get(z).toString().replace(".00",
          // "") +")" + ((colors.size()-1) != z ?
          // "," : "");
         }
        }
       }

       // product_size = product_size + ",";
      }
     }

     List < String > product_option1_uniq = new ArrayList < String > (new HashSet < String > (product_option1));
     List < String > product_option2_uniq = new ArrayList < String > (new HashSet < String > (product_option2));
     List < String > product_option3_uniq = new ArrayList < String > (new HashSet < String > (product_option3));
     String product_option_1_txt = "";
     String product_option_2_txt = "";
     String product_option_3_txt = "";

     for (String word: product_option1_uniq) {
      product_option_1_txt += word + ",";
     }

     for (String word: product_option2_uniq) {
      product_option_2_txt += word + ",";
     }

     for (String word: product_option3_uniq) {
      product_option_3_txt += word + ",";
     }

     insertMap.put("product_size", product_option_1_txt);
     insertMap.put("product_color", product_option_2_txt);
     insertMap.put("product_text", product_option_3_txt);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[class=detail_explain_box]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");

      // 예외파일
      if (imgs_url.indexOf("mignonchic_notice") > -1) {

      } else {
       insertMap.put("product_img_url", imgs_url);
       insertMap.put("product_img_sort", z);
       this.dashBoardDao.insertCombineDetailImg(insertMap);
       String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

       // update
       if (naverNewImg != "") {
        insertMap.put("product_new_img_url", naverNewImg);
        this.dashBoardDao.updateCombineNaverImg(insertMap);
       }

       z++;
      }
     }

     retVal++;
    }
   } else if (shopName.equals("sunnylike")) {
    insertMap.put("product_code", productLinkArry.get(i).toString());
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     productDetailDoc = Jsoup.connect(detailFullUrl).cookies(tempCookies).timeout(100000).ignoreContentType(true).get();

     JSONParser jsonParser = new JSONParser();
     String sb = productDetailDoc.select("body").html().replaceAll(System.getProperty("line.separator"), "").replaceAll("(\r\n|\r|\n|\n\r)", "");

     sb = sb.substring(0, sb.lastIndexOf("]") + 1);
     JSONArray ja = (JSONArray) jsonParser.parse(sb);
     JSONObject bookObject = (JSONObject) ja.get(0);

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_text", "< div style=text-align:center; >" + bookObject.get("store_ment").toString() + bookObject.get("gd_sizestr").toString() + bookObject.get("gd_myinfostr").toString() + "< /div >");
     insertMap.put("product_title", bookObject.get("gd_name1").toString() + "[" + bookObject.get("gd_no").toString() + "]");
     insertMap.put("product_price", bookObject.get("gd_vprice").toString());
     insertMap.put("product_color", bookObject.get("gd_optcolornm").toString());
     insertMap.put("product_size", bookObject.get("gd_optsize").toString());
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // title img
     insertMap.put("product_thumbImg", bookObject.get("img_2").toString());

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // title img save
     commonUtil.download(bookObject.get("img_2").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);

     Set key = bookObject.keySet();
     Iterator < String > iter = key.iterator();

     int z = 0;
     while (iter.hasNext()) {
      String keyname = iter.next();
      if (keyname.indexOf("img_") > -1) {
       if (bookObject.get(keyname) != null) {
        String imgs_url = bookObject.get(keyname).toString();

        insertMap.put("product_img_url", imgs_url);
        insertMap.put("product_img_sort", z);
        this.dashBoardDao.insertCombineDetailImg(insertMap);
        String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

        // update
        if (naverNewImg != "") {
         insertMap.put("product_new_img_url", naverNewImg);
         this.dashBoardDao.updateCombineNaverImg(insertMap);
        }

        z++;
       }
      }
     }
     retVal++;
    }
   } else if (shopName.equals("bedding")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");

    insertMap.put("product_code", product_code_arr[product_code_arr.length - 1]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(0).attr("content"));
     insertMap.put("product_text", productDetailDoc.select("meta[property=og:description]").eq(0).attr("content"));
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(0).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      if (imgs_url.indexOf("bar_itemview_detail.jpg") < 0) {
       insertMap.put("product_img_url", imgs_url);
       insertMap.put("product_img_sort", z);
       this.dashBoardDao.insertCombineDetailImg(insertMap);
       String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

       // update
       if (naverNewImg != "") {
        insertMap.put("product_new_img_url", naverNewImg);
        this.dashBoardDao.updateCombineNaverImg(insertMap);
       }

       z++;
      }
     }

     retVal++;
    }
   } else if (shopName.equals("ingdome")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");

    insertMap.put("product_code", product_code_arr[product_code_arr.length - 1]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(0).attr("content"));
     insertMap.put("product_text", productDetailDoc.select("meta[property=og:description]").eq(0).attr("content"));
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(0).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > optgroup > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_size = product_size + options.text() + ",";
      }
     }

     // 색상
     String product_color = "";
     for (Element options: productDetailDoc.select("select[id=multi_product_option_id1] > optgroup > option")) {
      if (options.attr("value").indexOf("*") < 0) {
       product_color = product_color + options.text() + ",";
      }
     }

     insertMap.put("product_size", product_size);
     insertMap.put("product_color", product_color);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("src");
      if (imgs_url.indexOf("bar_itemview_detail.jpg") < 0) {
       insertMap.put("product_img_url", imgs_url);
       insertMap.put("product_img_sort", z);
       this.dashBoardDao.insertCombineDetailImg(insertMap);
       String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

       // update
       if (naverNewImg != "") {
        insertMap.put("product_new_img_url", naverNewImg);
        this.dashBoardDao.updateCombineNaverImg(insertMap);
       }

       z++;
      }
     }

     retVal++;
    }
   } else if (shopName.equals("viviopera")) {

    // 재로그인
    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
    authAgainCookies = authAgainCookies.replace("path=/; domain=.viviopera.co.kr; HttpOnly", "");
    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();

    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
    String[] product_code_arr = product_code.split("/");

    insertMap.put("product_code", product_code_arr[product_code_arr.length - 1]);
    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

    // 등록되지 않은 상품
    if (isProduct == 0) {

     insertMap.put("product_shop", shopName);
     insertMap.put("product_url", detailFullUrl);
     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").eq(0).attr("content"));
     insertMap.put("product_text", productDetailDoc.select("meta[property=og:description]").eq(0).attr("content"));
     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").eq(0).attr("content"));
     insertMap.put("product_price2", "");
     insertMap.put("product_price3", "");

     // 사이즈
     String tmp_product_size = "";
     for (Element options: productDetailDoc.select("select[id=product_option_id1] > optgroup > option")) {
    	 tmp_product_size = tmp_product_size + options.text() +",";
     }

     String product_size = "";
     
     // 색상
     String product_color = "";
     
     if(tmp_product_size == ""){
         // 사이즈
         for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
          if (options.attr("value").indexOf("*") < 0) {
           product_size = product_size + options.text() + ",";
          }
         }

         for (Element options: productDetailDoc.select("select[id=product_option_id3] > option")) {
          if (options.attr("value").indexOf("*") < 0) {
           product_color = product_color + options.text() + ",";
          }
         }    	 
     }else{
         ArrayList < String > colors = new ArrayList < String > ();
         ArrayList < String > sizes = new ArrayList < String > ();
         
    	 String[] tmp_color_size = tmp_product_size.replaceAll("대리배송-", "").replaceAll("선택안함-", "").split(",");
    	 for (int z = 0; z < tmp_color_size.length; z++) {
    		 String[] tmp_color_sizes = tmp_color_size[z].split("-");
    		 System.out.println(tmp_color_sizes[0]);
    		 System.out.println(tmp_color_sizes[1]);
    		 
			   if (!colors.contains(tmp_color_sizes[0])) {
			    colors.add(tmp_color_sizes[0]);
			   }
			
			   if (!sizes.contains(tmp_color_sizes[1])) {
			    sizes.add(tmp_color_sizes[1]);
			   }    		 
         }

         product_color = colors.toString();
         // System.out.println(product_color.replace("[",
         // "").replace("]", "") + ",");
         product_color = product_color.replace("[", "").replace("]", "") + ",";
         //insertMap.put("product_color", product_color.replace("[", "").replace("]", "") + ",");

         product_size = sizes.toString();
         // System.out.println(product_size.replace("[",
         // "").replace("]", "") + ",");
         product_size = product_size.replace("[", "").replace("]", "") + ",";
         //insertMap.put("product_size", product_size.replace("[", "").replace("]", "") + ",");
    	 
     }

     insertMap.put("product_size", product_color);
     insertMap.put("product_color", product_size);

     // 공통 등록
     this.dashBoardDao.insertCombineProduct(insertMap);

     // 타이틀 이미지 다운로드
     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
     int z = 0;

     // 상세 이미지 다운로드
     for (Element imgs: productDetailImgs) {
      String imgs_url = imgs.attr("ec-data-src");
      if ((imgs_url.indexOf("bar_itemview_detail.jpg") < 0 || imgs_url.indexOf("detail_tit_1.jpg") < 0 ) && imgs_url != "") {
       insertMap.put("product_img_url", imgs_url);
       insertMap.put("product_img_sort", z);
       this.dashBoardDao.insertCombineDetailImg(insertMap);
       String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

       // update
       if (naverNewImg != "") {
        insertMap.put("product_new_img_url", naverNewImg);
        this.dashBoardDao.updateCombineNaverImg(insertMap);
       }

       z++;
      }
     }

     retVal++;
    }
   }else if (shopName.equals("shoenine")) {

	    // 재로그인
	    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
	    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();
	    
	    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
	    String[] product_code_arr = product_code.split("/");
	    insertMap.put("product_code", "sn" + product_code_arr[product_code_arr.length - 1]);
	    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

	    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

	    // 등록되지 않은 상품
	    if (isProduct == 0 && !insertMap.get("product_code").equals("sn")) {

	     insertMap.put("product_shop", shopName);
	     insertMap.put("product_url", detailFullUrl);
	     insertMap.put("product_title", productDetailDoc.select("meta[property=og:title]").attr("content"));
	     insertMap.put("product_text", "");
	     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
	     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
	     insertMap.put("product_price2", "");
	     insertMap.put("product_price3", "");

	     // 사이즈
	     String product_size = "";
	     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
	      if (options.attr("value").indexOf("*") < 0) {
	       product_size = product_size + options.text() + ",";
	      }
	     }

	     // 색상
	     String product_color = "";
	     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
	      if (options.attr("value").indexOf("*") < 0) {
	       product_color = product_color + options.text() + ",";
	      }
	     }

	     insertMap.put("product_size", product_size);
	     insertMap.put("product_color", product_color);

	     // 공통 등록
	     this.dashBoardDao.insertCombineProduct(insertMap);

	     // 타이틀 이미지 다운로드
	     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
	     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
	     int z = 0;

	     // 상세 이미지 다운로드
	     for (Element imgs: productDetailImgs) {
	      String imgs_url = imgs.attr("src");
	      if ((imgs_url.indexOf("20190906_101122612.jpg") < 0 && imgs_url.indexOf("4bf7c4af6e6fe133353c4bb65b0ec56d.jpg") < 0 ) && imgs_url != "") {
		      insertMap.put("product_img_url", imgs_url);
		      insertMap.put("product_img_sort", z);
		      this.dashBoardDao.insertCombineDetailImg(insertMap);
		      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

		      // update
		      if (naverNewImg != "") {
		       insertMap.put("product_new_img_url", naverNewImg);
		       this.dashBoardDao.updateCombineNaverImg(insertMap);
		      }

		      z++;  
	      }
	     }

	     retVal++;
	    }
   }else if (shopName.equals("ddogakshu")) {

	    // 재로그인
	    String authAgainCookies = shopGetCookieLogin(shopUrl, shopName, shopID, shopPW);
	    productDetailDoc = Jsoup.connect(detailFullUrl).cookie("cookie", authAgainCookies).timeout(100000).get();
	    
	    String product_code = productDetailDoc.select("link[rel=canonical]").attr("href");
	    String[] product_code_arr = product_code.split("/");
	    insertMap.put("product_code", "dg" + product_code_arr[product_code_arr.length - 1]);
	    isProduct = this.dashBoardDao.selectCombineItemCheck(insertMap);

	    System.out.println(shopName + " : " + insertMap.get("product_code") + " - " + isProduct);

	    // 등록되지 않은 상품
	    if (isProduct == 0 && !insertMap.get("product_code").equals("dg")) {

	     insertMap.put("product_shop", shopName);
	     insertMap.put("product_url", detailFullUrl);
	     insertMap.put("product_title", productDetailDoc.select("title").text());
	     insertMap.put("product_text", "");
	     insertMap.put("product_price", productDetailDoc.select("meta[property=product:price:amount]").attr("content"));
	     insertMap.put("product_thumbImg", productDetailDoc.select("meta[property=og:image]").attr("content"));
	     insertMap.put("product_price2", "");
	     insertMap.put("product_price3", "");

	     // 사이즈
	     String product_size = "";
	     for (Element options: productDetailDoc.select("select[id=product_option_id2] > option")) {
	      if (options.attr("value").indexOf("*") < 0) {
	       product_size = product_size + options.text() + ",";
	      }
	     }

	     // 색상
	     String product_color = "";
	     for (Element options: productDetailDoc.select("select[id=product_option_id1] > option")) {
	      if (options.attr("value").indexOf("*") < 0) {
	       product_color = product_color + options.text() + ",";
	      }
	     }

	     insertMap.put("product_size", product_size);
	     insertMap.put("product_color", product_color);

	     // 공통 등록
	     this.dashBoardDao.insertCombineProduct(insertMap);

	     // 타이틀 이미지 다운로드
	     commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);
	     Elements productDetailImgs = productDetailDoc.select("div[id=prdDetail]").select("img"); // 상세이미지
	     int z = 0;

	     // 상세 이미지 다운로드
	     for (Element imgs: productDetailImgs) {
	      String imgs_url = imgs.attr("src");
	      if ((imgs_url.indexOf("shop information") < 0 ) && imgs_url != "") {
		      insertMap.put("product_img_url", imgs_url);
		      insertMap.put("product_img_sort", z);
		      this.dashBoardDao.insertCombineDetailImg(insertMap);
		      String naverNewImg = commonUtil.download(imgs_url, insertMap.get("product_seq").toString(), "desc", z, saveDir, shopUrl, session);

		      // update
		      if (naverNewImg != "") {
		       insertMap.put("product_new_img_url", naverNewImg);
		       this.dashBoardDao.updateCombineNaverImg(insertMap);
		      }

		      z++;  
	      }
	     }

	     retVal++;
	    }
	   }
  }
  return retVal;
 }

 /**
  * 상세
  * 
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping("/api/naverBlogImgUpdate.api")
 @ResponseBody
 public void naverBlogImgUpdate(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam HashMap < String, Object > paramMap, HttpSession session) throws Exception {
  String retVal = "";
  int retCnt = 0;
  try {
   HashMap < String, Object > insertMap = new HashMap < String, Object > ();

   List < HashMap > noneNaverList = this.dashBoardDao.noneNaverApi(paramMap);
   for (int i = 0; i < noneNaverList.size(); i++) {

    insertMap.put("product_seq", noneNaverList.get(i).get("product_seq").toString());
    insertMap.put("product_img_sort", noneNaverList.get(i).get("product_img_sort").toString());

    String saveDir = commonUtil.getServerRootPath(request) +
 noneNaverList.get(i).get("product_shop").toString() + "/desc/"; // 상점별
    String fileName = noneNaverList.get(i).get("product_seq").toString() + "_" + noneNaverList.get(i).get("product_img_sort").toString() + noneNaverList.get(i).get("ext").toString();

    if (session.getAttribute("access_token") != null) {
     NaverBlogUtil nbu = new NaverBlogUtil();
     try {
      String uploadUrl = nbu.naverBlogWrite(session.getAttribute("access_token").toString(), saveDir, fileName);
      String[] product_code_arr = uploadUrl.split("/");
      Document naverBlogView = Jsoup.connect("https://blog.naver.com/PostView.nhn?blogId=urbanstyle_blog&logNo=" + product_code_arr[product_code_arr.length - 1] + "&redirect=Dlog&widgetTypeCall=true&directAccess=false").timeout(100000).get();
      retVal = naverBlogView.select("meta[property=og:image]").attr("content");

      if (retVal.indexOf("og_default_image") > 0) {
       retVal = "";
      } else {
       retCnt++;
      }
     } catch (Exception e) {
      retVal = "";
      System.out.println(e);
     }
    }

    // update
    if (retVal != "") {
     insertMap.put("product_new_img_url", retVal);
     this.dashBoardDao.updateCombineNaverImg(insertMap);
    } else {
     break;
    }

   }

  } catch (Exception e) {
   System.out.println(e.getMessage());
  }

  Map < String, Object > map = new HashMap < String, Object > ();
  map.put("retCnt", retCnt);

  jsonView.render(map, request, response);
 }
 
 /**
  * 상세
  * 
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping("/api/titleImgDownload.api")
 @ResponseBody
 public void titleImgDownload(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam HashMap < String, Object > paramMap, HttpSession session) throws Exception {
  String retVal = "";
  int retCnt = 0;
  try {
   HashMap < String, Object > insertMap = new HashMap < String, Object > ();

   List < HashMap > titleImgList = this.dashBoardDao.titleImgList(paramMap);
   for (int i = 0; i < titleImgList.size(); i++) {

	   String shopUrl = titleImgList.get(i).get("shop_url").toString(); // 사이트 주소
	   String shopName = titleImgList.get(i).get("shop_name").toString(); // 상점코드
	   String saveDir = commonUtil.getServerRootPath(request) + shopName; // 상점별  저장 경로
	   
	    insertMap.put("product_seq", titleImgList.get(i).get("seq").toString());
	    insertMap.put("product_thumbImg", titleImgList.get(i).get("product_thumbImg").toString());

	    // 타이틀 이미지 다운로드
	    commonUtil.download(insertMap.get("product_thumbImg").toString(), insertMap.get("product_seq").toString(), "title", 0, saveDir, shopUrl, session);

   }

  } catch (Exception e) {
   System.out.println(e.getMessage());
  }

  Map < String, Object > map = new HashMap < String, Object > ();
  map.put("retCnt", retCnt);

  jsonView.render(map, request, response);
 }
 
 @RequestMapping("/api/gdadminInsert.api")
 @ResponseBody
 public void gdadminInsert(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam HashMap < String, Object > paramMap, HttpSession session) throws Exception {
  Map < String, Object > map = new HashMap < String, Object > ();

  String shopName = paramMap.get("shop_name").toString();
  String saveDir = commonUtil.getServerRootPath(request) + shopName; // 상점별  저장 경로
  
  HashMap < String, Object > insertMap = new HashMap < String, Object > ();

  List < HashMap > godoadminList = this.dashBoardDao.selectGodoAdminList(paramMap);
  for (int i = 0; i < godoadminList.size(); i++) {
	   String product_seq = godoadminList.get(i).get("seq").toString();
	   String product_shop = godoadminList.get(i).get("product_shop").toString();
	   String product_thumbImg = godoadminList.get(i).get("product_thumbImg").toString();
	   String product_code = godoadminList.get(i).get("product_code").toString();
	   
	   String product_title;
	   
	   if(product_shop.equals("shuline")){
		   product_title = "L - " + godoadminList.get(i).get("product_new_title").toString() + " - " + godoadminList.get(i).get("product_title").toString();
	   }else{
		   product_title = godoadminList.get(i).get("product_shop").toString().toUpperCase().substring(0,1) + " - " + godoadminList.get(i).get("product_new_title").toString() + " - " + godoadminList.get(i).get("product_title").toString();
	   }
	   
	   String product_price = godoadminList.get(i).get("product_price").toString();
	   String product_color = godoadminList.get(i).get("product_color").toString();
	   String product_size = godoadminList.get(i).get("product_size").toString();
	   String product_img_url = godoadminList.get(i).get("product_img_url").toString();
	   String product_category = godoadminList.get(i).get("product_category").toString();
	   
	   insertMap.put("product_code", product_code);
	   insertMap.put("product_seq", product_seq);
	   insertMap.put("product_shop", product_shop);
	   insertMap.put("product_thumbImg", product_thumbImg);
	   insertMap.put("product_title", product_title);
	   insertMap.put("product_price", product_price);
	   insertMap.put("product_color", product_color);
	   insertMap.put("product_size", product_size);
	   insertMap.put("product_img_url", product_img_url);
	   insertMap.put("product_category", product_category);
	   
	   //등록
	   godoInsert(session, insertMap, saveDir);
  }
  
  jsonView.render(map, request, response);
 }
 
 public void godoInsert(HttpSession session, HashMap<String, Object> insertMap, String saveDir) throws KeyManagementException, NoSuchAlgorithmException, IOException{

		 int pos = insertMap.get("product_thumbImg").toString().lastIndexOf(".");
		 String ext = insertMap.get("product_thumbImg").toString().substring( pos + 1 );
		 String thumb_file_str = saveDir+"\\title\\"+insertMap.get("product_seq").toString()+"."+ext;
	 
		 File thumb_file = new File(thumb_file_str);
		 
		 String destText = "";

		 //encodeToString();
		 
		 if(insertMap.get("product_img_url").toString().indexOf(",") == -1){
			 destText = "<img src='" + insertMap.get("product_img_url").toString() + "' />" ;
		 }else{
			 String[] img_arr = insertMap.get("product_img_url").toString().split(",");
			 
			 for(int i=0; i<img_arr.length;i++){
				 destText += "<img src='" + img_arr[i] + "' /><br/>";
			 }
		 }
		 
		 Double goodsPrice = 0.0;
		 Integer price = Integer.parseInt(insertMap.get("product_price").toString());
		 if(price > 10000){
			 goodsPrice = Double.parseDouble(insertMap.get("product_price").toString()) * 1.4;
			 goodsPrice = (double) Integer.parseInt(goodsPrice.toString().substring(0, goodsPrice.toString().indexOf(".") - 2)+"00");
		 }else{
			 if(price.equals(5000)){
				 goodsPrice = 7200.0;
			 }else if(price.equals(6000)){
				 goodsPrice = 8700.0;
			 }else if(price.equals(7000)){
				 goodsPrice = 10100.0;
			 }else if(price.equals(8000)){
				 goodsPrice = 11600.0;
			 }else if(price.equals(9000)){
				 goodsPrice = 13000.0;
			 }else if(price.equals(10000)){
				 goodsPrice = 14500.0;
			 }else{
				 goodsPrice = Double.parseDouble(price.toString()) * 1.4;
				 goodsPrice = (double) Integer.parseInt(goodsPrice.toString().substring(0, goodsPrice.toString().indexOf(".") - 2)+"00");
			 }
		 }
		 
		 String[] product_size = insertMap.get("product_size").toString().split(",");
		 String[] product_color = insertMap.get("product_color").toString().split(",");
		 
		 String url = "http://gdadmin.urban-style.co.kr/goods/goods_ps.php";
		 String randomStr = RandomStringUtils.random(32,33,125,true,false);
		 
		 URL gdurl = new URL(url);
		 final MultipartUtilityGD http = new MultipartUtilityGD(gdurl, session.getAttribute("godoLogin").toString());
		 http.addFormField("mode","register");
		 http.addFormField("goodsNo","");
		 //http.addFilePart("image[imageOriginal][]", thumb_file);
		 http.addFormField("applyGoodsCopy","");
		 http.addFormField("cateGoods[]", insertMap.get("product_category").toString());//category
		 http.addFormField("link[cateCd][]", insertMap.get("product_category").toString());//category
		 http.addFormField("link[cateLinkFl][]", "y");//category
		 http.addFormField("cateCd", insertMap.get("product_category").toString());//category
		 http.addFormField("goodsDisplayFl","y");
		 http.addFormField("goodsSellFl","y");
		 http.addFormField("goodsDisplayMobileFl","y");
		 http.addFormField("goodsSellMobileFl","y");
		 http.addFormField("scmNo","1");
		 http.addFormField("scmFl","n");
		 http.addFormField("commission","0");
		 http.addFormField("goodsCd",insertMap.get("product_code").toString());
		 http.addFormField("goodsNmFl","d");
		 http.addFormField("goodsNm",insertMap.get("product_title").toString());
		 http.addFormField("goodsNmMain","");
		 http.addFormField("goodsNmList","");
		 http.addFormField("goodsNmDetail","");
		 http.addFormField("goodsNmPartner","");
		 http.addFormField("goodsSearchWord","");
		 http.addFormField("goodsOpenDt","");
		 http.addFormField("goodsState","n");
		 http.addFormField("imageStorage","local");
		 http.addFormField("imagePath","");
		 http.addFormField("payLimitFl","n");
		 http.addFormField("purchaseGoodsNm","");
		 http.addFormField("brandCdNm","");
		 http.addFormField("makerNm","");
		 http.addFormField("originNm","");
		 http.addFormField("goodsModelNo","");
		 http.addFormField("hscodeNation[]","kr");
		 http.addFormField("hscode[]","");
		 http.addFormField("makeYmd","");
		 http.addFormField("launchYmd","");
		 http.addFormField("effectiveStartYmd","");
		 http.addFormField("effectiveEndYmd","");
		 http.addFormField("goodsPermissionPriceString","");
		 http.addFormField("goodsPermission","all");
		 http.addFormField("onlyAdultFl","n");
		 http.addFormField("onlyAdultDisplayFl","y");
		 http.addFormField("goodsAccessDisplayFl","y");
		 http.addFormField("goodsAccess","all");
		 http.addFormField("kcmarkInfo[kcmarkFl]","n");
		 http.addFormField("kcmarkInfo[kcmarkDivFl]","");
		 http.addFormField("kcmarkInfo[kcmarkNo]","");
		 http.addFormField("taxFreeFl","t");
		 http.addFormField("taxPercent","10");
		 http.addFormField("goodsWeight","0");
		 http.addFormField("goodsVolume","0");
		 http.addFormField("cultureBenefitFl","n");
		 http.addFormField("stockFl","n");
		 http.addFormField("stockCnt","0");
		 http.addFormField("fixedSales","option");
		 http.addFormField("salesUnit","1");
		 http.addFormField("soldOutFl","n");
		 http.addFormField("maxOrderChk","n");
		 http.addFormField("salesDateFl","n");
		 http.addFormField("mileageFl","c");
		 http.addFormField("mileageGroup","all");
		 http.addFormField("mileageGoods","0");
		 http.addFormField("mileageGoodsUnit","percent");
		 http.addFormField("mileageGroupMemberInfo['groupSno'][]","");
		 http.addFormField("mileageGroupMemberInfo['mileageGoods'][]","");
		 http.addFormField("mileageGroupMemberInfo['mileageGoodsUnit'][]","percent");
		 http.addFormField("goodsBenefitSetFl","n");
		 http.addFormField("goodsDiscountFl","n");
		 http.addFormField("benefitUseType","nonLimit");
		 http.addFormField("newGoodsRegFl","regDt");
		 http.addFormField("newGoodsDate","0");
		 http.addFormField("newGoodsDateFl","day");
		 http.addFormField("periodDiscountStart","");
		 http.addFormField("periodDiscountEnd","");
		 http.addFormField("goodsDiscountGroup","all");
		 http.addFormField("goodsDiscount","0");
		 http.addFormField("goodsDiscountUnit","percent");
		 http.addFormField("goodsDiscountGroupMemberInfo['groupSno'][]","");
		 http.addFormField("goodsDiscountGroupMemberInfo['goodsDiscount'][]","");
		 http.addFormField("goodsDiscountGroupMemberInfo['goodsDiscountUnit'][]","percent");
		 http.addFormField("optionN[sno][0]","");
		 http.addFormField("optionN[optionNo][0]","");
		 http.addFormField("fixedPrice","0");
		 http.addFormField("costPrice","0");
		 http.addFormField("goodsPriceString","");
		 http.addFormField("goodsPrice", goodsPrice.toString());
		 http.addFormField("optionFl","y");	//y 옵션 추가
		 http.addFormField("optionReged","y");
		 http.addFormField("optionTempSession",randomStr);
		 http.addFormField("optionTempStocked","");
		 http.addFormField("optionTextFl","n");
		 http.addFormField("addGoodsFl","n");
		 http.addFormField("addGoodsGroupTitle","");
		 http.addFormField("add_goods_info_sel","testesss");

		 http.addFormField("imageAddUrl","y");
		 
		 //http.addFilePart("image[imageOriginal][]", thumb_file);
		 
		 http.addFormField("imageSize[magnify]","500");
		 http.addFilePart("image[imageMagnify][]", thumb_file);
		 http.addFormField("image[imageMagnify][]",insertMap.get("product_thumbImg").toString());
		 
		 http.addFormField("imageSize[detail]","500");
		 http.addFilePart("image[imageDetail][]", thumb_file);
		 http.addFormField("image[imageDetail][]",insertMap.get("product_thumbImg").toString());
		 
		 http.addFormField("imageSize[list]","150");
		 http.addFilePart("image[imageList][]", thumb_file);
		 http.addFormField("image[imageList][]",insertMap.get("product_thumbImg").toString());
		 
		 http.addFormField("imageSize[main]","268");
		 http.addFilePart("image[imageMain][]", thumb_file);
		 http.addFormField("image[imageMain][]",insertMap.get("product_thumbImg").toString());
		 
		 http.addFormField("imageSize[add1]","268");
		 http.addFilePart("image[imageAdd1][]", thumb_file);
		 http.addFormField("image[imageAdd1][]",insertMap.get("product_thumbImg").toString());
		 
		 http.addFormField("imageSize[add2]","268");
		 http.addFilePart("image[imageAdd2][]", thumb_file);
		 http.addFormField("image[imageAdd2][]",insertMap.get("product_thumbImg").toString());
		 
		 http.addFormField("shortDescription","");
		 http.addFormField("eventDescription","");
		 http.addFormField("goodsDescriptionSameFl","y");
		 http.addFormField("goodsDescription",destText);
		 http.addFormField("goodsDescriptionMobile",destText);
		 http.addFormField("deliverySno","4");
		 http.addFormField("relationFl","n");
		 http.addFormField("relationSameFl","n");
		 http.addFormField("relationDataFl","y");
		 http.addFormField("relationGoodsDisplayDate[]","2020-05-18 0:00");
		 http.addFormField("relationGoodsDisplayDate[]","2020-05-24 23:59");
		 http.addFormField("goodsIconStartYmd","");
		 http.addFormField("goodsIconEndYmd","");
		 http.addFormField("imgDetailViewFl","n");
		 http.addFormField("externalVideoFl","n");
		 http.addFormField("externalVideoUrl","");
		 http.addFormField("externalVideoSizeFl","y");
		 http.addFormField("externalVideoWidth","");
		 http.addFormField("externalVideoHeight","");
		 http.addFormField("detailInfoDeliveryFl","selection");
		 http.addFormField("detailInfoDelivery","2001");
		 http.addFormField("detailInfoDeliveryDirectInput","");
		 http.addFormField("detailInfoDeliverySelectionInput","hello");
		 http.addFormField("detailInfoASFl","selection");
		 http.addFormField("detailInfoAS","3001");
		 http.addFormField("detailInfoASDirectInput","");
		 http.addFormField("detailInfoASSelectionInput","hello2");
		 http.addFormField("detailInfoRefundFl","selection");
		 http.addFormField("detailInfoRefund","4001");
		 http.addFormField("detailInfoRefundDirectInput","");
		 http.addFormField("detailInfoRefundSelectionInput","hello3");
		 http.addFormField("detailInfoExchangeFl","selection");
		 http.addFormField("detailInfoExchange","5001");
		 http.addFormField("detailInfoExchangeDirectInput","");
		 http.addFormField("detailInfoExchangeSelectionInput","hello4");
		 http.addFormField("seoTagFl","n");
		 http.addFormField("seoTag[title]","");
		 http.addFormField("seoTag[author]","");
		 http.addFormField("seoTag[description]","");
		 http.addFormField("seoTag[keyword]","");
		 http.addFormField("daumFl","y");
		 http.addFormField("naverFl","y");
		 http.addFormField("naverImportFlag","");
		 http.addFormField("naverProductFlag","");
		 http.addFormField("naverAgeGroup","a");
		 http.addFormField("naverGender","");
		 http.addFormField("naverAttribute","");
		 http.addFormField("naverTag","");
		 http.addFormField("naverCategory","");
		 http.addFormField("naverProductId","");
		 http.addFormField("fbUseFl","n");
		 http.addFormField("memo","");

		 http.addFormField("optionY[optionDisplayFl]","s");
		 http.addFormField("optionY[optionCnt]","1");
		 http.addFormField("optionY[optionName][]","사이즈");
		 http.addFormField("optionY[optionCnt][]", ""+ product_size.length +"");

		 for(int i=0; i<product_size.length;i++){
			 http.addFormField("optionY[optionValue][0][]", product_size[i]);
		 }

		 http.addFormField("optionYIcon[goodsImageTextChanged][0][]","n");
		 http.addFormField("optionY[optionName][]","색상");
		 http.addFormField("optionY[optionCnt][]", ""+ product_color.length +"");

		 for(int i=0; i<product_color.length;i++){
			 http.addFormField("optionY[optionValue][0][]", product_color[i]);
		 }
		 
		 http.addFormField("optionYIcon[goodsImageTextChanged][0][]","n");
			
		 for(int i=0; i< product_size.length;i++){
			 for(int j=0; j<product_color.length;j++){
				 http.addFormField("optionY[optionViewFl][]","y");
				 http.addFormField("optionY[optionSellFl][]","y");
				 http.addFormField("optionY[optionDeliveryFl][]","normal");
				 http.addFormField("optionY[optionValueText][]",product_size[i]+"^|^"+product_color[j]);
			 }
		 }

		 http.finish();
		 
		 this.dashBoardDao.updateGodoAdminChange(insertMap);

 }
 
 public String encodeToString(BufferedImage image, String type) {
     String imageString = null;
     ByteArrayOutputStream bos = new ByteArrayOutputStream();

     try {
         ImageIO.write(image, type, bos);
         byte[] imageBytes = bos.toByteArray();

         BASE64Encoder encoder = new BASE64Encoder();
         imageString = encoder.encode(imageBytes);

         bos.close();
     } catch (IOException e) {
         e.printStackTrace();
     }
     return imageString;
 }
 
 @RequestMapping("/api/reTitleUpdate.api")
 public String reTitleUpdate(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam HashMap < String, Object > paramMap, HttpSession session) throws Exception {
  try {
	  this.dashBoardDao.updateReTitleUpdate(paramMap);
  } catch (Exception e) {
   System.out.println(e.getMessage());
  }

  return "redirect:/dashboard/retitle.do?scrollValue="+paramMap.get("scrollValue");
 }
 
 public boolean imgCombine(String imgPath, HashMap<String, Object> insertMap, Integer img_size){
	 String product_seq = insertMap.get("product_seq").toString();
	 try {
		 	for(int i=0; i<img_size-1;i++){
				   BufferedImage image1 = ImageIO.read(new File(imgPath+"/desc/"+product_seq + "_" + 0 +".jpg"));
				   BufferedImage image2 = ImageIO.read(new File(imgPath+"/desc/"+product_seq + "_" + (i+1) +".jpg"));

				   int width = Math.max(image1.getWidth(), image2.getWidth());
				   int height = image1.getHeight() + image2.getHeight();

				   BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				   Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();

				   graphics.setBackground(Color.WHITE);
				   graphics.drawImage(image1, 0, 0, null);
				   graphics.drawImage(image2, 0, image1.getHeight(), null);
				   
				   ImageIO.write(mergedImage, "jpg", new File(imgPath+"/desc/"+product_seq + "_" + 0 +".jpg"));
		 	}
		 	
			File dir = new File(imgPath+"/desc/");
			File[] files = dir.listFiles();
			for (File f : files) {
				if (f.isFile() && f.getName().toUpperCase().startsWith(product_seq)) {
					if(!f.getName().contains("_0")){
						f.delete();
					}
				}
			}
		 	
			//db delete
			this.dashBoardDao.deleteImgCombine(insertMap);
		 	
		  } catch (IOException ioe) {
		   ioe.printStackTrace();
		  }
	 return true;
 }
 
}