package com.danalent.common;

import java.io.*;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class NaverBlogUtil {
	public String naverBlogWrite(String token, String filePath, String fileName){
		String retVal = "";
        String header = "Bearer " + token; // Bearer 다음에 공백 추가
        try {
            // api url 설정
            String apiURL = "https://openapi.naver.com/blog/writePost.json";
            NaverMultipartUtil mu = new NaverMultipartUtil(apiURL);
            // 접큰 토큰 헤더 추가
            mu.addHeaderField("Authorization", header);
            mu.readyToConnect();
            // blog 글쓰기 필수 요청변수 title 추가
            String title = fileName;
            // blog 글쓰기 필수 요청변수 contents - 첨부이미지는 <img src='#0' />, <img src='#1' /> ... 으로 참조 가능
            String contents = "<img src='#0' />";
            mu.addFormField("title", title);
            mu.addFormField("contents", contents);
            //mu.addFormField("options.openType", "closed");

            // [시작] image 첨부 로직 - 필요시 이미지수 만큼 반복
            File uploadFile1 = new File(filePath + fileName);
            mu.addFilePart("image", uploadFile1);
            /*
            File uploadFile2 = new File("YOUR_FILE_NAME_1");
            mu.addFilePart("image", uploadFile2);
            */
            // [종료] 이미지 첨부 로직 - 필요시 이미지수 만큼 반복

            // HTTP 호출 결과 수신
            List response = mu.finish();
            retVal = response.get(4).toString().substring(response.get(4).toString().indexOf("\"postUrl\":\""), response.get(4).toString().lastIndexOf("\"")).replace("\"postUrl\":\"","");
            
        } catch (Exception e) {
        	retVal = "";
        }
		return retVal;
	}
}
