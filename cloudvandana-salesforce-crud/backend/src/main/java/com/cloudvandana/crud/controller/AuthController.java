package com.cloudvandana.crud.controller;
import com.cloudvandana.crud.service.SalesforceOAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final SalesforceOAuthService oauth; private final String frontend;
  public AuthController(SalesforceOAuthService oauth,@Value("${app.frontend-url}") String frontend){this.oauth=oauth;this.frontend=frontend;}
  @GetMapping("/login") public RedirectView login(HttpSession s){return new RedirectView(oauth.authorizationUrl(s));}
  @GetMapping("/callback") public RedirectView callback(@RequestParam String code,@RequestParam String state,HttpSession s){oauth.exchangeCode(code,state,s);return new RedirectView(frontend+"/?auth=success");}
  @GetMapping("/status") public Map<String,Boolean> status(HttpSession s){return Map.of("authenticated",oauth.authenticated(s));}
  @PostMapping("/logout") public Map<String,String> logout(HttpSession s){oauth.logout(s);return Map.of("message","Logged out");}
}
