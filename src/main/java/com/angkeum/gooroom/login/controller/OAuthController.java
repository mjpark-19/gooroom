package com.angkeum.gooroom.login.controller;

import com.angkeum.gooroom.login.service.OAuthService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public void kakaoCallback(@RequestParam String code){
        String accessToken = oAuthService.getKakaoToken(code);
        log.info(code);
        oAuthService.createKakaoUser(accessToken);
    }

}
