package com.cloudvandana.crud.controller;
import com.cloudvandana.crud.service.SalesforceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/salesforce")
public class SalesforceController {
 private final SalesforceService sf; public SalesforceController(SalesforceService sf){this.sf=sf;}
 @GetMapping("/objects") public Map<String,Object> objects(){return Map.of("objects",new String[]{"Account","Opportunity","Lead","Contact","Case"});}
 @GetMapping("/objects/{object}/fields") public Map<String,Object> fields(@PathVariable String object){return Map.of("fields",sf.fields(object));}
 @GetMapping("/objects/{object}") public Map<String,Object> list(@PathVariable String object,@RequestParam(defaultValue="0") int page,HttpSession s){return sf.list(object,page,s);}
 @GetMapping("/objects/{object}/{id}") public Map<String,Object> get(@PathVariable String object,@PathVariable String id,HttpSession s){return sf.get(object,id,s);}
 @PostMapping("/objects/{object}") public Map<String,Object> create(@PathVariable String object,@RequestBody Map<String,Object> body,HttpSession s){return sf.create(object,body,s);}
 @PatchMapping("/objects/{object}/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void update(@PathVariable String object,@PathVariable String id,@RequestBody Map<String,Object> body,HttpSession s){sf.update(object,id,body,s);}
 @DeleteMapping("/objects/{object}/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable String object,@PathVariable String id,HttpSession s){sf.delete(object,id,s);}
 @ExceptionHandler(Exception.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> error(Exception e){return Map.of("error",e.getMessage()==null?"Request failed":e.getMessage());}
}
