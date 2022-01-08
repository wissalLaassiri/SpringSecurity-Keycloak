package ma.enset.productsapp.web;

import lombok.Data;
import ma.enset.productsapp.repositories.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProductController{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KeycloakRestTemplate keycloakRestTemplate;
    // Cette objet implicittement ajoute le jwt dans les requests

    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/products")
    public String products(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }
    @GetMapping("/suppliers")
    public String suppliers(Model model){
        PagedModel suppliers = keycloakRestTemplate.getForObject("http://localhost:8083/suppliers/", PagedModel.class);
        model.addAttribute("suppliers",suppliers);
        return "suppliers";
    }

    @GetMapping("/jwt")
    @ResponseBody  // psque c'est un controller not restController
    public Map<String,String> getJWT(HttpServletRequest request){
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) token.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        Map<String,String> res = new HashMap<>();
        res.put("access_token",keycloakSecurityContext.getTokenString());
        return res;
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Model model){
        model.addAttribute("errorMessage","Not Authorized");
        return "errors";
    }
}

@Data
class Supplier{
    private Long id;
    private String name;
    private String email;
}
