package com.cloudvandana.crud.service;

import com.cloudvandana.crud.config.SalesforceProperties;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class SalesforceOAuthService {
  public static final String TOKEN = "SF_TOKEN";
  private static final String INSTANCE_URL = "SF_INSTANCE_URL";
  private static final String STATE = "SF_STATE";
  private static final String CODE_VERIFIER = "SF_CODE_VERIFIER";
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private final RestClient client;
  private final SalesforceProperties props;

  public SalesforceOAuthService(RestClient client, SalesforceProperties props) { this.client = client; this.props = props; }

  public String authorizationUrl(HttpSession session) {
    String state = UUID.randomUUID().toString();
    String codeVerifier = createCodeVerifier();
    session.setAttribute(STATE, state);
    session.setAttribute(CODE_VERIFIER, codeVerifier);
    return UriComponentsBuilder.fromUriString(props.loginUrl() + "/services/oauth2/authorize")
      .queryParam("response_type", "code")
      .queryParam("client_id", props.clientId())
      .queryParam("redirect_uri", props.redirectUri())
      .queryParam("scope", props.scopes())
      .queryParam("state", state)
      .queryParam("code_challenge", createCodeChallenge(codeVerifier))
      .queryParam("code_challenge_method", "S256")
      .build().toUriString();
  }

  @SuppressWarnings("unchecked")
  public void exchangeCode(String code, String state, HttpSession session) {
    Object expected = session.getAttribute(STATE);
    if (expected == null || !expected.toString().equals(state)) throw new IllegalArgumentException("Invalid OAuth state");
    String codeVerifier = (String) session.getAttribute(CODE_VERIFIER);
    if (codeVerifier == null) throw new IllegalArgumentException("Missing OAuth code verifier");
    var form = new LinkedMultiValueMap<String,String>();
    form.add("grant_type", "authorization_code"); form.add("code", code); form.add("client_id", props.clientId());
    form.add("client_secret", props.clientSecret()); form.add("redirect_uri", props.redirectUri());
    form.add("code_verifier", codeVerifier);
    Map<String,Object> token = client.post().uri(props.loginUrl()+"/services/oauth2/token")
      .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(Map.class);
    if (token == null || token.get("access_token") == null) throw new IllegalStateException("Salesforce did not return an access token");
    session.setAttribute(TOKEN, token.get("access_token"));
    session.setAttribute(INSTANCE_URL, token.get("instance_url"));
    session.removeAttribute(STATE);
    session.removeAttribute(CODE_VERIFIER);
  }

  private String createCodeVerifier() {
    byte[] verifier = new byte[32];
    SECURE_RANDOM.nextBytes(verifier);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(verifier);
  }

  private String createCodeChallenge(String codeVerifier) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256")
        .digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is not available", exception);
    }
  }

  public boolean authenticated(HttpSession session) { return session.getAttribute(TOKEN) != null && session.getAttribute(INSTANCE_URL) != null; }
  public String token(HttpSession session) { return (String) session.getAttribute(TOKEN); }
  public String instance(HttpSession session) { return (String) session.getAttribute(INSTANCE_URL); }
  public void logout(HttpSession session) { session.invalidate(); }
}
