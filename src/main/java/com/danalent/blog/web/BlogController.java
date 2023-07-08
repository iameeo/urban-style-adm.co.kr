package com.danalent.blog.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.danalent.common.NaverMultipartUtil;

@Controller
public class BlogController {
	private final String page_name = "blog";
	private final String modual_prefix = page_name.toLowerCase() + "/";

	public final String cliendId_t = "f05efcec447702929b16d203717efd08";
	public final String redirectURI_t = "http://urban-style-adm.co.kr/blog/popup/tistoryCallback.do";
	public final String clientSecret_t = "f05efcec447702929b16d203717efd08519611d15372b9e9da0758d0fdb1936c127a035c";
	
	public final String cliendId_n = "yEVnLyurbSHQJT3nlMeB";
	public final String redirectURI_n = "http://urban-style-adm.co.kr/blog/popup/naverCallback.do";
	public final String clientSecret_n = "dstC7VSwFz";

	@Autowired
	MappingJacksonJsonView jsonView;

	/**
	 * 블로그 메인 페이지
	 * 
	 * @param request
	 * @param paramMap
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/blog/index.do")
	public String main(HttpServletRequest request, @RequestParam HashMap<String, Object> paramMap, Model model, HttpSession session) throws UnsupportedEncodingException {
		SecureRandom random = new SecureRandom();
		String state = new BigInteger(130, random).toString();
		String apiURL_t = "https://www.tistory.com/oauth/authorize?response_type=code";
		apiURL_t += "&client_id=" + cliendId_t;
		apiURL_t += "&redirect_uri=" + URLEncoder.encode(redirectURI_t, "UTF-8");
		apiURL_t += "&state=" + state;
		
		String apiURL_n = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		apiURL_n += "&client_id=" + cliendId_n;
		apiURL_n += "&redirect_uri=" + URLEncoder.encode(redirectURI_n, "UTF-8");
		apiURL_n += "&state=" + state;

		session.setAttribute("state", state);

		model.addAttribute("apiURL_t", apiURL_t);
		model.addAttribute("apiURL_n", apiURL_n);
		model.addAttribute("page_name", page_name);

		return modual_prefix + "index";
	}

	/**
	 * 네이비 이미지 파일명 등록
	 * 
	 * @param request
	 * @param paramMap
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/blog/write.do")
	public String write(HttpServletRequest request, @RequestParam HashMap<String, Object> paramMap, Model model) throws IOException {

		String token_t = request.getParameter("token_t");
		String token_n = request.getParameter("token_n");
		String token_g = request.getParameter("token_g");

		// tistory api create
		if (!token_t.isEmpty()) {
			String apiURL_t;
			apiURL_t = "https://www.tistory.com/apis/post/write?access_token=" + token_t + "&";
			apiURL_t += "output=json";
			apiURL_t += "&blogName=ho-mi";
			apiURL_t += "&tag="+ URLEncoder.encode(paramMap.get("tag").toString(), "UTF-8");
			apiURL_t += "&title=" + URLEncoder.encode(paramMap.get("title").toString(), "UTF-8");
			apiURL_t += "&content=" + URLEncoder.encode(paramMap.get("content").toString(), "UTF-8");
			/*
			 * blogName: Blog Name (필수) title: 글 제목 (필수) content: 글 내용
			 * visibility: 발행상태 (0: 비공개 - 기본값, 1: 보호, 3: 발행) category: 카테고리 아이디
			 * (기본값: 0) published: 발행시간 (TIMESTAMP 이며 미래의 시간을 넣을 경우 예약. 기본값:
			 * 현재시간) slogan: 문자 주소 tag: 태그 (',' 로 구분) acceptComment: 댓글 허용 (0, 1
			 * - 기본값) password: 보호글 비밀번호
			 */

			try {
				URL url = new URL(apiURL_t);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
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
					//System.out.println(res);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		// naver api create
		if (!token_n.isEmpty()) {
			String header = "Bearer " + token_n;
			String apiURL_n = "https://openapi.naver.com/blog/writePost.json";
            NaverMultipartUtil mu = new NaverMultipartUtil(apiURL_n);

            mu.addHeaderField("Authorization", header);
            mu.readyToConnect();

            mu.addFormField("title", paramMap.get("title").toString());
            mu.addFormField("contents", paramMap.get("content").toString());

            // HTTP 호출 결과 수신
            List response = mu.finish();
            System.out.println(response);
		}

		// google api create
		if (!token_g.isEmpty()) {
			String apiURL_g;
			apiURL_g = "https://www.googleapis.com/blogger/v3/blogs/4661306117078067652/posts/";
			/*
			 * blogName: Blog Name (필수) title: 글 제목 (필수) content: 글 내용
			 * visibility: 발행상태 (0: 비공개 - 기본값, 1: 보호, 3: 발행) category: 카테고리 아이디
			 * (기본값: 0) published: 발행시간 (TIMESTAMP 이며 미래의 시간을 넣을 경우 예약. 기본값:
			 * 현재시간) slogan: 문자 주소 tag: 태그 (',' 로 구분) acceptComment: 댓글 허용 (0, 1
			 * - 기본값) password: 보호글 비밀번호
			 */

			try {
				JSONObject json = new JSONObject();
				JSONArray tagsArray = new JSONArray();

				for (String wo : paramMap.get("tag").toString().split(",")){
					tagsArray.add(wo);
		        }
				
				json.put("title", paramMap.get("title").toString());
				json.put("content", paramMap.get("content").toString());
				json.put("labels", tagsArray);
				String body = json.toString();
				//System.out.println(body);
				URL postUrl = new URL(apiURL_g);
				HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
				connection.setDoOutput(true); // xml내용을 전달하기 위해서 출력 스트림을 사용
				connection.setInstanceFollowRedirects(false); // Redirect처리 하지
																// 않음
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Authorization", "Bearer "+ token_g);
				OutputStream os = connection.getOutputStream();
				os.write(body.getBytes());
				os.flush();
				//System.out.println("Location: " + connection.getHeaderField("Location"));

				BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

				String output;
				//System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					//System.out.println(output);
				}
				connection.disconnect();
				// 81page~
			} catch (

			Exception e) {
				System.out.println(e);
			}
		}

		return "redirect:/blog/index.do";

	}

	@RequestMapping("/blog/popup/tistoryCallback.do")
	public String tistoryCallback(HttpServletRequest request, @RequestParam HashMap<String, Object> paramMap, Model model, HttpSession session) throws UnsupportedEncodingException {

		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String apiURL_t;
		apiURL_t = "https://www.tistory.com/oauth/access_token?grant_type=authorization_code&";
		apiURL_t += "client_id=" + cliendId_t;
		apiURL_t += "&client_secret=" + clientSecret_t;
		apiURL_t += "&redirect_uri=" + URLEncoder.encode(redirectURI_t, "UTF-8");
		apiURL_t += "&code=" + code;
		apiURL_t += "&state=" + state;
		String access_token_t = "";

		try {
			URL url = new URL(apiURL_t);
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
				access_token_t = res.toString().replace("access_token=", "");

				session.setAttribute("access_token_t", access_token_t);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return "redirect:/blog/index.do";
	}
	
	@RequestMapping("/blog/popup/naverCallback.do")
	public String naverCallback(HttpServletRequest request, @RequestParam HashMap<String, Object> paramMap, Model model, HttpSession session) throws UnsupportedEncodingException {

		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String apiURL_n;
		apiURL_n = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
		apiURL_n += "client_id=" + cliendId_n;
		apiURL_n += "&client_secret=" + clientSecret_n;
		apiURL_n += "&redirect_uri=" + URLEncoder.encode(redirectURI_n, "UTF-8");
		apiURL_n += "&code=" + code;
		apiURL_n += "&state=" + state;
		String access_token_n = "";

		try {
			URL url = new URL(apiURL_n);
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

			    access_token_n = jsonObj.get("access_token").toString();
				session.setAttribute("access_token_n", access_token_n);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return "redirect:/blog/index.do";
	}
	
}
