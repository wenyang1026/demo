package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoogleLoginController {


    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/oauth2/authorization/google")
    public String googleAuthRedirect() {
        return "redirect:/oauth2/authorize/google";
    }

    @GetMapping("/oauth2/authorize/google")
    public String googleAuth(Model model, OAuth2AuthenticationToken token) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        model.addAttribute("name", client.getPrincipalName());
        return "google";
    }


}
