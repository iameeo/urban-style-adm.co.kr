package com.danalent.dashboard.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.danalent.api.MultipartUtilityGD;
import com.danalent.common.NaverBlogUtil;
import com.danalent.dashboard.dao.DashBoardDao;

@Controller
public class DashBoardController {
 private final String page_name = "DASHBOARD";
 private final String modual_prefix = page_name.toLowerCase() + "/";

 @Resource(name = "dashBoardDao")
 private DashBoardDao dashBoardDao;

 @Autowired
 MappingJacksonJsonView jsonView;

 /**
  * 대시보드 메인 페이지
  * @param request
  * @param paramMap
  * @param model
  * @return
  * @throws UnsupportedEncodingException 
  */
 @RequestMapping("/dashboard/index.do")
 public String main(HttpServletRequest request, @RequestParam HashMap < String, Object > paramMap, Model model, HttpSession session) throws UnsupportedEncodingException {
  List < HashMap > combineProductList = this.dashBoardDao.selectCombineProductList(paramMap);
  List < HashMap > combineShopList = this.dashBoardDao.selectCombineShopList(paramMap);

  String clientId = "yEVnLyurbSHQJT3nlMeB"; //애플리케이션 클라이언트 아이디값";
  String redirectURI = URLEncoder.encode("http://urban-style-adm.co.kr/dashboard/popup/naverCallback.do", "UTF-8");
  SecureRandom random = new SecureRandom();
  String state = new BigInteger(130, random).toString();
  String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
  apiURL += "&client_id=" + clientId;
  apiURL += "&redirect_uri=" + redirectURI;
  apiURL += "&state=" + state;

  session.setAttribute("state", state);

  model.addAttribute("combineShopList", combineShopList);
  model.addAttribute("combineProductList", combineProductList);
  model.addAttribute("apiURL", apiURL);
  model.addAttribute("page_name", page_name);

  return modual_prefix + "index";
 }

 /**
  * 상점목록
  * @param request
  * @param paramMap
  * @param model
  * @return
  */
 @RequestMapping("/dashboard/shopList.do")
 public String shopList(HttpServletRequest request, @RequestParam HashMap < String, Object > paramMap, Model model) {
  List < HashMap > combineShopList = this.dashBoardDao.selectCombineShopList(paramMap);
  model.addAttribute("combineShopList", combineShopList);
  model.addAttribute("page_name", page_name);

  return modual_prefix + "shopList";
 }
 
 @RequestMapping("/dashboard/retitle.do")
 public String reTitle(HttpServletRequest request, @RequestParam HashMap < String, Object > paramMap, Model model) {
  List < HashMap > reTitleList = this.dashBoardDao.selectReTitleList(paramMap);
  model.addAttribute("reTitleList", reTitleList);
  model.addAttribute("page_name", page_name);
  model.addAttribute("scrollValue", paramMap.get("scrollValue"));

  return modual_prefix + "reTitle";
 }

 /**
  * 네이비 이미지 파일명 등록
  * @param request
  * @param paramMap
  * @param model
  * @return
  * @throws IOException
  */
 @RequestMapping("/dashboard/rename.do")
 public String todoTest(HttpServletRequest request, @RequestParam HashMap < String, Object > paramMap, Model model) throws IOException {
  List < HashMap > combineShopList = this.dashBoardDao.selectCombineShopList(paramMap);
  model.addAttribute("combineShopList", combineShopList);
  model.addAttribute("page_name", page_name);
  return modual_prefix + "rename";
 }
 
 /**
  * 자동 품절처리
  * @param request
  * @param paramMap
  * @param model
  * @return
  * @throws IOException
  */
 @RequestMapping("/dashboard/soldout.do")
 public String soldOut(HttpServletRequest request, @RequestParam HashMap < String, Object > paramMap, Model model) throws IOException {

  model.addAttribute("page_name", page_name);
  return modual_prefix + "soldout";
 }
 
 @RequestMapping("/dashboard/godoSoldout.do")
 public void godoSoldout(HttpServletRequest request, HttpServletResponse response, @RequestParam HashMap < String, Object > paramMap, Model model, HttpSession session) throws Exception {
  Boolean result = false;

  String prd = paramMap.get("prd").toString();
  
  String[] str_array = prd.split("\n");

  for(int i = 0; i < str_array.length; i++) {
	  String product_name = str_array[i].trim();
	  //System.out.println(product_name.length());
      if(product_name.length() > 0){
    	  //System.out.println(product_name);
    	  godoSoldoutHttp(session, product_name);
      }
      result = true;
  }
  
  HashMap < String, Boolean > resultMap = new HashMap < String, Boolean > ();
  resultMap.put("result", result);

  this.jsonView.render(resultMap, request, response);
 }
 
 public void godoSoldoutHttp(HttpSession session, String keyword) throws KeyManagementException, NoSuchAlgorithmException, IOException{
	 String url = "http://gdadmin.urban-style.co.kr/goods/goods_list.php?detailSearch=&delFl=n&key=goodsNm&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&searchDateFl=regDt&searchDate%5B%5D=&searchDate%5B%5D=&searchPeriod=-1&cateGoods%5B%5D=&cateGoods%5B%5D=&cateGoods%5B%5D=&cateGoods%5B%5D=&displayTheme%5B%5D=&displayTheme%5B%5D=&goodsPrice%5B%5D=&goodsPrice%5B%5D=&mileage%5B%5D=&mileage%5B%5D=&mileageFl=&stock%5B%5D=&stock%5B%5D=&optionFl=&optionTextFl=&addGoodsFl=&goodsDisplayFl=&goodsSellFl=&goodsDisplayMobileFl=&goodsSellMobileFl=&stockFl=&soldOut=&goodsIconCdPeriod=&goodsIconCd=&goodsDeliveryFl=&goodsDeliveryFixFl%5B%5D=all&event_text=&eventThemeSno=&sort=g.goodsNo+desc&pageNum=10&searchFl=y&applyPath=%2Fgoods%2Fgoods_list.php";
	 String auth = session.getAttribute("godoLogin").toString().replace("{", "").replace("}", "").replace("GD5SESSID", "").trim().replace("=", "");
	 
	 Document doc = Jsoup.connect(url).cookie("GD5SESSID", auth).timeout(100000).get();
	 //System.out.println(doc);
	 Elements ele = doc.select("#frmList").select(".center").select("input");
	 for(int i =0;i<ele.size();i++){
		 String goodsNo = ele.eq(i).attr("name").replace("goodsNo[", "").replace("]", "");
		 System.out.println(goodsNo);
		 
		 if(goodsNo != ""){
			 url = "http://gdadmin.urban-style.co.kr/goods/goods_ps.php";
			 Jsoup.connect(url).data("mode", "soldout", "modDtUse", "y","goodsNo["+goodsNo+"]",goodsNo).cookie("GD5SESSID", auth).timeout(100000).post();	 
		 }		 
	 }
 }

 /**
  * 네이버 이미지 파일명 DB UPDATE
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @throws Exception
  */
 @RequestMapping("/dashboard/imgReName.do")
 public void imgReName(HttpServletRequest request, HttpServletResponse response, @RequestParam HashMap < String, Object > paramMap, Model model) throws Exception {
  Boolean result = false;

  Document doc = Jsoup.parse(request.getParameter("naverText"));
  Elements imgConvert = doc.select("img");

  try {
   for (int i = 0; i < imgConvert.size(); i++) {

    String product_info = imgConvert.get(i).attr("title");
    String[] infos = product_info.split("_");

    paramMap.put("product_seq", infos[0]);
    paramMap.put("product_img_sort", infos[1].replace(".jpg", "").replace(".gif", "").replace(".png", "").replace(".JPG", ""));
    paramMap.put("product_new_img_url", imgConvert.get(i).attr("src"));

    this.dashBoardDao.updateCombineNaverImg(paramMap);
   }
   result = true;
  } catch (Exception e) {
   result = false;
  }

  HashMap < String, Boolean > resultMap = new HashMap < String, Boolean > ();
  resultMap.put("result", result);

  this.jsonView.render(resultMap, request, response);
 }

 /**
  * 엑셀 다운로드
  * @param request
  * @param response
  * @param paramMap
  * @param model
  * @return
  * @throws IOException
  */
 @RequestMapping("/dashboard/popup/excel.do")
 public String excel(HttpServletRequest request, HttpServletResponse response, @RequestParam HashMap < String, Object > paramMap, Model model) throws IOException {

  String shopType = paramMap.get("shopType").toString();
  String regdate = paramMap.get("regdate").toString();

  paramMap.put("shopType", shopType);
  paramMap.put("regdate", regdate);

  List < HashMap > excelList = this.dashBoardDao.selectCombineProductExcelList(paramMap);
  response.setContentType("application/msexcel");
  response.setHeader("Content-Disposition", "attachment; filename=\"daily_" + shopType + "[" + regdate + "].xls\"");

  model.addAttribute("excelList", excelList);
  model.addAttribute("page_name", page_name);

  return "/dashboard/popup/excel";
 }

 /**
  * 네이버 로그인 메인 페이지
  * @param request
  * @param paramMap
  * @param model
  * @return
  * @throws UnsupportedEncodingException
  */
 @RequestMapping("/dashboard/popup/naverCallback.do")
 public String naverCallback(HttpServletRequest request, @RequestParam HashMap < String, Object > paramMap, Model model, HttpSession session) throws UnsupportedEncodingException {

  String clientId = "yEVnLyurbSHQJT3nlMeB"; //애플리케이션 클라이언트 아이디값";
  String clientSecret = "dstC7VSwFz"; //애플리케이션 클라이언트 시크릿값";
  String code = request.getParameter("code");
  String state = request.getParameter("state");
  String redirectURI = URLEncoder.encode("http://urban-style-adm.co.kr/dashboard/popup/naverCallback.do", "UTF-8");
  String apiURL;
  apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
  apiURL += "client_id=" + clientId;
  apiURL += "&client_secret=" + clientSecret;
  apiURL += "&redirect_uri=" + redirectURI;
  apiURL += "&code=" + code;
  apiURL += "&state=" + state;
  String access_token = "";
  String refresh_token = "";

  try {
   URL url = new URL(apiURL);
   HttpURLConnection con = (HttpURLConnection) url.openConnection();
   con.setRequestMethod("GET");
   int responseCode = con.getResponseCode();
   BufferedReader br;

   if (responseCode == 200) { // 정상 호출
    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
   } else { // 에러 발생
    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
   }
   String inputLine;
   StringBuffer res = new StringBuffer();
   while ((inputLine = br.readLine()) != null) {
    res.append(inputLine);
   }
   br.close();
   if (responseCode == 200) {
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObj = (JSONObject) jsonParser.parse(res.toString());

    access_token = jsonObj.get("access_token").toString();
    refresh_token = jsonObj.get("refresh_token").toString();

    session.setAttribute("access_token", access_token);
    session.setAttribute("refresh_token", refresh_token);
   }
  } catch (Exception e) {
   System.out.println(e);
  }

  return "redirect:/dashboard/index.do";
 }

 @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
 public void fileUpload(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
  // 추가데이터 테스트
  System.err.println(multipartRequest.getParameter("temp"));
  String filePath = "c:/tmp";
  String serverPort = multipartRequest.getParameter("port");

  HandlerFile handlerFile = new HandlerFile(multipartRequest, filePath);

  Map < String, List < String >> fileNames = handlerFile.getUploadFileName();
  // 실제저장파일명과 원본파일명 DB저장처리

  // 클라이언트 객체
  System.err.println(fileNames.toString());
  String fileName = handlerFile.getFileFullPath();
  Client client = new Client(fileName, Integer.parseInt(serverPort));
  String result = client.getResult();
  String js;
  ServletOutputStream out;

  try {
   response.setContentType("text/html; charset=UTF-8");
   out = response.getOutputStream();

   if (result.equals("null") || result.equals("fail")) {
    js = "<script>history.back(); alert('Result : Error! Page Reload!');</script>";
   } else {
    js = "<script>alert('Result : " + result + "'); //location.href='https://www.google.co.kr/search?q=" +
     result + "'</script>";
    js = result;
   }

   out.println(js);
   out.flush();

  } catch (Exception e) {
   e.printStackTrace();
  } // catch

 } // fileUpload

}