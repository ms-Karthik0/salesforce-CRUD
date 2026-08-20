package com.cloudvandana.crud.service;

import com.cloudvandana.crud.config.SalesforceProperties;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class SalesforceService {
  private final RestClient client; private final SalesforceOAuthService oauth; private final SalesforceProperties props;
  private static final Map<String,List<String>> FIELDS = Map.of(
    "Account", List.of("Id","Name","Phone","Website","Industry","Type","BillingCity","CreatedDate"),
    "Opportunity", List.of("Id","Name","StageName","Amount","CloseDate","Probability","Type","CreatedDate"),
    "Lead", List.of("Id","FirstName","LastName","Company","Email","Phone","Status","LeadSource"),
    "Contact", List.of("Id","FirstName","LastName","Email","Phone","Title","Department","CreatedDate"),
    "Case", List.of("Id","CaseNumber","Subject","Status","Priority","Origin","Type","CreatedDate")
  );
  public SalesforceService(RestClient client, SalesforceOAuthService oauth, SalesforceProperties props) { this.client=client;this.oauth=oauth;this.props=props; }
  private String api(HttpSession s,String path){ return oauth.instance(s)+"/services/data/v"+props.apiVersion()+path; }
  private void auth(RestClient.RequestHeadersSpec<?> r,HttpSession s){ r.header("Authorization","Bearer "+oauth.token(s)).accept(MediaType.APPLICATION_JSON); }
  public List<String> fields(String object){ requireObject(object); return FIELDS.get(object); }
  public Map<String,Object> list(String object,int page,HttpSession s){ requireObject(object); if(!oauth.authenticated(s)) throw new IllegalStateException("Not authenticated");
    int offset=Math.max(page,0)*20; String soql="SELECT "+String.join(",",FIELDS.get(object))+" FROM "+object+" ORDER BY CreatedDate DESC LIMIT 20 OFFSET "+offset;
    var req=client.get().uri(queryUri(api(s,"/query"), soql)); auth(req,s); return req.retrieve().body(Map.class); }
  public Map<String,Object> get(String object,String id,HttpSession s){ requireObject(object); String soql="SELECT "+String.join(",",FIELDS.get(object))+" FROM "+object+" WHERE Id='"+safe(id)+"' LIMIT 1"; return query(soql,s); }
  public Map<String,Object> create(String object,Map<String,Object> body,HttpSession s){ requireObject(object); return client.post().uri(api(s,"/sobjects/"+object)).header("Authorization","Bearer "+oauth.token(s)).contentType(MediaType.APPLICATION_JSON).body(filter(body,object)).retrieve().body(Map.class); }
  public void update(String object,String id,Map<String,Object> body,HttpSession s){ requireObject(object); client.patch().uri(api(s,"/sobjects/"+object+"/"+safe(id))).header("Authorization","Bearer "+oauth.token(s)).contentType(MediaType.APPLICATION_JSON).body(filter(body,object)).retrieve().toBodilessEntity(); }
  public void delete(String object,String id,HttpSession s){ requireObject(object); client.delete().uri(api(s,"/sobjects/"+object+"/"+safe(id))).header("Authorization","Bearer "+oauth.token(s)).retrieve().toBodilessEntity(); }
  @SuppressWarnings("unchecked") private Map<String,Object> query(String soql,HttpSession s){ var req=client.get().uri(queryUri(api(s,"/query"), soql));auth(req,s);return req.retrieve().body(Map.class); }
  private java.net.URI queryUri(String endpoint, String soql){ return UriComponentsBuilder.fromUriString(endpoint).queryParam("q", soql).build().toUri(); }
  private Map<String,Object> filter(Map<String,Object> body,String object){ Map<String,Object> out=new HashMap<>(body); out.keySet().removeIf(k->!FIELDS.get(object).contains(k)||k.equals("Id")||k.equals("CreatedDate")); return out; }
  private String safe(String v){ if(v==null||!v.matches("[A-Za-z0-9]{15,18}")) throw new IllegalArgumentException("Invalid Salesforce Id"); return v; }
  private void requireObject(String o){ if(!FIELDS.containsKey(o)) throw new IllegalArgumentException("Unsupported Salesforce object"); }
}
