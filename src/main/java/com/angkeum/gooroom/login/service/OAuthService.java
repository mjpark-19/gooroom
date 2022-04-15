package com.angkeum.gooroom.login.service;



import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


@Service
public class OAuthService {
    Logger log = LoggerFactory.getLogger(OAuthService.class);

    public String getKakaoToken(String code){
        String accessToken = "";
        String refreshToken = "";
        String regURL = "https://kauth.kakao.com/oauth/token";
        try{
            URL url = new URL(regURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            log.info(regURL);
            System.out.println(regURL);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()
            ));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            //TODO REST_API_KEY 입력
            sb.append("&client_id=634e4c6f7e0c4f07491d5c9958590174");
            //TODO 인가코드 받은 redirect_uri 입력
            sb.append("&redirect_uri=http://localhost:8080/login/oauth2/kakao");
            sb.append("&code="+code);
            //sb.append("&client_secret=R8cXfVvrJWtLKK89aw8eIc2CWBtj3vDo");
            System.out.println("스트링빌더: "+sb.toString());
            bw.write(sb.toString());
            bw.flush();

            //결과코드가 200이면 성공
            int resCode = conn.getResponseCode();
            log.info("responseCode =" + String.valueOf(resCode));
            System.out.println("responseCode =" + String.valueOf(resCode));
            //요청을 총해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body" + result);
            System.out.println("response body" + result);
            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            //JsonFactory factory = new JsonFactory();
            //JsonParser parser = factory.createParser(result);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);


       /*     accessToken = parser.getValueAsString("access_token");
            refreshToken = parser.getValueAsString("refresh_token");*/

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken =  element.getAsJsonObject().get("refresh_token").getAsString();
            log.info("accessToken: " + accessToken);
            log.info("refreshToken: " +refreshToken);
            System.out.println("accessToken: " + accessToken);
            System.out.printf("refreshToken: " +refreshToken);
            br.close();
            bw.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return accessToken;
    }

    public HashMap<String, Object> createKakaoUser(String token) {
        HashMap<String, Object> userInfo = new HashMap<>();
        System.out.println("token : " + token);
        String Bearer = "Bearer " + token;
        System.out.println("Bearer : " + Bearer);
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", Bearer); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            String requestProperty = conn.getRequestProperty("Authorization");
            System.out.println("responseCode : " + responseCode);
            System.out.println("requestProperty : " + requestProperty);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            /*프로필(profile)- json
             String - email, age_range, birthday, birthyear, gender, phone_number*/

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            boolean hasAgeRange = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_age_range").getAsBoolean();
            boolean hasBirthday = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_birthday").getAsBoolean();
            boolean hasGender = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_gender").getAsBoolean();

            String email = "";
            String age = "";
            String birthday = "";
            String gender = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

               /* age = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("age").getAsString();
                birthday = element.getAsJsonObject().get("properties").getAsJsonObject().get("birthday").getAsString();
                gender = element.getAsJsonObject().get("properties").getAsJsonObject().get("gender").getAsString();*/

            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("ageRange : " + age);
            System.out.println("birthday : " + birthday);
            System.out.println("gender : " + gender);

            userInfo.put("id", id);
            userInfo.put("email", email);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public void kakaoLogout(String token){
        String reqUrl = "https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization","Bearer "+ token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while((line = br.readLine())!= null){
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
