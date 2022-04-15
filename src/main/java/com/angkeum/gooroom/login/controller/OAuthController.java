package com.angkeum.gooroom.login.controller;

import com.angkeum.gooroom.login.service.OAuthService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/login/oauth2")
public class OAuthController {

    @Autowired
    OAuthService oAuthService;

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);


    /**
     * 카카오 callback
     *[GET] login/oauth2/kakao
     * **/
    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code, HttpSession session){
        String accessToken = oAuthService.getKakaoToken(code);
        log.info(code);
        HashMap<String, Object> userInfo= oAuthService.createKakaoUser(accessToken);
        log.info(String.valueOf(userInfo));

        if(userInfo.get("email") != null){
         session.setAttribute("userId", userInfo.get("email"));
         session.setAttribute("access_Token", accessToken);
        }
    }



    @RequestMapping("/logout")
    public String logout(HttpSession session){
        oAuthService.kakaoLogout((String)session.getAttribute("access_Token"));
        session.removeAttribute("access_Token");
        session.removeAttribute("userId");
        return "index";
    }
}
