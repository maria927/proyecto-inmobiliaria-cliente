package co.com.udem.inmobiliariaclient.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import co.com.udem.inmobiliariaclient.domain.AutenticationRequestDTO;
import co.com.udem.inmobiliariaclient.domain.AutenticationResponseDTO;
import co.com.udem.inmobiliariaclient.domain.FiltroDTO;
import co.com.udem.inmobiliariaclient.domain.PropiedadDTO;
import co.com.udem.inmobiliariaclient.domain.RegistroDTO;
import co.com.udem.inmobiliariaclient.domain.TipoIdentificacionDTO;
import co.com.udem.inmobiliariaclient.entities.UserToken;
import co.com.udem.inmobiliariaclient.repository.UserTokenRepository;

@RestController

public class AutenticationClientController {
	
	private String currentToken;
	
	@Autowired
    RestTemplate restTemplate;
   
    @Autowired
    UserTokenRepository userTokenRepository;
   
    @Autowired
    UserToken userToken;

    
    @PostMapping("/autenticar")
    public AutenticationResponseDTO autenticar(@RequestBody AutenticationRequestDTO autenticationRequestDTO) {
    	
    	//Con balanceador de carga
    	
//    	ServiceInstance serviceInstance=loadBalancer.choose("clubfutbol");
//        System.out.println(serviceInstance.getUri());
//        String baseUrl=serviceInstance.getUri().toString();
//        baseUrl=baseUrl+"/auth/signin";
//        ResponseEntity<String> postResponse = restTemplate.postForEntity(baseUrl, autenticationRequestDTO, String.class);
//        System.out.println("Respuesta: "+postResponse.getBody());  //Recibe un xml
//        
//        Gson g = new Gson();
//        AutenticationResponseDTO autenticationResponseDTO = g.fromJson(postResponse.getBody(), AutenticationResponseDTO.class);
//        userToken.setUsername(autenticationResponseDTO.getUsername());
//        userToken.setToken(autenticationResponseDTO.getToken());
//        userTokenRepository.save(userToken);
//        return autenticationResponseDTO.getToken();
 
    	
    	 ///////////Sin balanceador//////////////
    	
        ResponseEntity<String> postResponse = restTemplate.postForEntity("http://localhost:9092/auth/signin", autenticationRequestDTO, String.class);
        Gson g = new Gson();
        AutenticationResponseDTO autenticationResponseDTO = g.fromJson(postResponse.getBody(), AutenticationResponseDTO.class);
        userToken.setUsername(autenticationResponseDTO.getUsername());
        userToken.setToken(autenticationResponseDTO.getToken());
        String usuario = userTokenRepository.findByUsername(autenticationResponseDTO.getUsername());
        if (usuario == null) {
        	userTokenRepository.save(userToken);
		}
        else
        {
        	userTokenRepository.updateToken(autenticationResponseDTO.getToken(),autenticationResponseDTO.getUsername());
        }

    	currentToken = userTokenRepository.obtenerToken(autenticationResponseDTO.getUsername());
        
        return autenticationResponseDTO;
       
    }
    
    @GetMapping("/registro/listarUsuarios")
    public  ResponseEntity<Object> listarUsuarios() {
        Object listadoUsuarios = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/registro/listarUsuarios", HttpMethod.GET, entity, String.class);
				
			    Gson g = new Gson();
		        Object registroDto = g.fromJson(response.getBody(), Object.class);
				listadoUsuarios = registroDto;
				res.put("respuesta", listadoUsuarios);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			  res.put("respuesta", e.getResponseBodyAsString());
			  return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @GetMapping("/registro/listarUsuario/{id}")
    public  ResponseEntity<Object> listarUsuariosId(@PathVariable String id) {
        Object listadoUsuarios = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/registro/listarUsuario/"+id, HttpMethod.GET, entity, String.class);
				
			    Gson g = new Gson();
		        Object registroDto = g.fromJson(response.getBody(), Object.class);
				listadoUsuarios = registroDto;
				res.put("respuesta", listadoUsuarios);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			    res.put("respuesta", e.getResponseBodyAsString());
			    return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @PostMapping("/registro/registrarUsuario")
    public ResponseEntity<Object> registrarUsuario(@RequestBody RegistroDTO registroDTO) {
        Object listadoUsuarios = null;
        Map<String, Object> res = new HashMap<>();
        try {
			ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9092/registro/registrarUsuario", registroDTO, String.class);
				
			    Gson g = new Gson();
		        Object registroDto = g.fromJson(response.getBody(), Object.class);
				listadoUsuarios = registroDto;
				res.put("respuesta", listadoUsuarios);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @PutMapping("/registro/modificarUsuario/{id}")
    public  ResponseEntity<Object> modificarUsuario(@RequestBody RegistroDTO registroDTO, @PathVariable Long id) {
        Object listadoUsuarios = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<RegistroDTO> entity = new HttpEntity<>(registroDTO, headers);
        
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/registro/modificarUsuario/"+id, HttpMethod.PUT, entity, String.class);
			
			    Gson g = new Gson();
		        Object registroDto = g.fromJson(response.getBody(), Object.class);
				listadoUsuarios = registroDto;
				res.put("respuesta", listadoUsuarios);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @DeleteMapping("/registro/eliminarUsuario/{id}")
    public  ResponseEntity<Object> eliminarUsuario(@PathVariable String id) {
        Object listadoUsuarios = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/registro/eliminarUsuario/"+id, HttpMethod.DELETE, entity, String.class);
				
			    Gson g = new Gson();
		        Object registroDto = g.fromJson(response.getBody(), Object.class);
				listadoUsuarios = registroDto;
				res.put("respuesta", listadoUsuarios);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    /*  Tipo Id */
    
    @GetMapping("/tipoidentificacion/obtenerTipoId")
    public  ResponseEntity<Object> listarTiposId() {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/tipoidentificacion/obtenerTipoId", HttpMethod.GET, entity, String.class);
				
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @GetMapping("/tipoidentificacion/obtenerTipoId/{id}")
    public  ResponseEntity<Object> listarTipoId(@PathVariable String id) {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/tipoidentificacion/obtenerTipoId/"+id, HttpMethod.GET, entity, String.class);
				
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @PostMapping("/tipoidentificacion/registrarTipoId")
    public ResponseEntity<Object> registrarTipoID(@RequestBody TipoIdentificacionDTO tipoIdDTO) {
    	  Object listado = null;
          Map<String, Object> res = new HashMap<>();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          if (currentToken != null) {
              headers.set("Authorization", "Bearer "+currentToken);
  		}
          HttpEntity<TipoIdentificacionDTO> entity = new HttpEntity<>(tipoIdDTO, headers);
        try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/tipoidentificacion/registrarTipoId", HttpMethod.POST, entity, String.class);
        		Gson g = new Gson();
		        Object listadoDTO = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDTO;
				res.put("respuesta", listado);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @PutMapping("/tipoidentificacion/modificarTipoId/{id}")
    public  ResponseEntity<Object> modificarTipo(@RequestBody TipoIdentificacionDTO tipoIdDTO, @PathVariable Long id) {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<TipoIdentificacionDTO> entity = new HttpEntity<>(tipoIdDTO, headers);
        
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/tipoidentificacion/modificarTipoId/"+id, HttpMethod.PUT, entity, String.class);
			
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return new ResponseEntity<>(res, HttpStatus.OK);
		
		} catch(HttpStatusCodeException e){
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		} 
    }
    
    @DeleteMapping("/tipoidentificacion/eliminarTipoId/{id}")
    public  Map<String, Object> eliminarTipoId(@PathVariable String id) {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/tipoidentificacion/eliminarTipoId/"+id, HttpMethod.DELETE, entity, String.class);
				
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    /*  Propiedad */
    
    @GetMapping("/propiedad/listarPropiedades")
    public  Map<String, Object> listarPropiedades() {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/listarPropiedades", HttpMethod.GET, entity, String.class);
				
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @GetMapping("/propiedad/listarPropiedad/{id}")
    public  Map<String, Object> listarPropiedad(@PathVariable String id) {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/listarPropiedad/"+id, HttpMethod.GET, entity, String.class);
				
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @PostMapping("/propiedad/registrarPropiedad")
    public  Map<String, Object> registrarPropiedad(@RequestBody PropiedadDTO propiedadDTO) {
    	  Object listado = null;
          Map<String, Object> res = new HashMap<>();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          if (currentToken != null) {
              headers.set("Authorization", "Bearer "+currentToken);
  		}
          HttpEntity<PropiedadDTO> entity = new HttpEntity<>(propiedadDTO, headers);
        try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/registrarPropiedad", HttpMethod.POST, entity, String.class);
        		Gson g = new Gson();
		        Object listadoDTO = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDTO;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @PutMapping("/propiedad/modificarPropiedad/{id}")
    public  Map<String, Object> modificarPropiedad(@RequestBody PropiedadDTO propiedadDTO, @PathVariable Long id) {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<PropiedadDTO> entity = new HttpEntity<>(propiedadDTO, headers);
        
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/modificarPropiedad/"+id, HttpMethod.PUT, entity, String.class);
			
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @DeleteMapping("/propiedad/eliminarPropiedad/{id}")
    public  Map<String, Object> eliminarPropiedad(@PathVariable String id) {
        Object listado = null;
        Map<String, Object> res = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.set("Authorization", "Bearer "+currentToken);
		}

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/eliminarPropiedad/"+id, HttpMethod.DELETE, entity, String.class);
				
			    Gson g = new Gson();
		        Object listadoDto = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDto;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    /*  Filtro con criteria query api: Genera automáticamente la consulta de acuerdo
     * a los valores enviados*/
    
    @PostMapping("/propiedad/filtrarPropiedad")
    public  Map<String, Object> filtrarPropiedad(@RequestBody FiltroDTO filtroDTO) {
    	  Object listado = null;
          Map<String, Object> res = new HashMap<>();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          if (currentToken != null) {
              headers.set("Authorization", "Bearer "+currentToken);
  		}
          HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
        try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/filtrarPropiedad", HttpMethod.POST, entity, String.class);
        		Gson g = new Gson();
		        Object listadoDTO = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDTO;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @PostMapping("/propiedad/filtrarPorValor")
    public  Map<String, Object> filtrarValor(@RequestBody FiltroDTO filtroDTO) {
    	  Object listado = null;
          Map<String, Object> res = new HashMap<>();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          if (currentToken != null) {
              headers.set("Authorization", "Bearer "+currentToken);
  		}
          HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
        try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/filtrarPorValor", HttpMethod.POST, entity, String.class);
        		Gson g = new Gson();
		        Object listadoDTO = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDTO;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @PostMapping("/propiedad/filtrarPorArea")
    public  Map<String, Object> filtrarPorArea(@RequestBody FiltroDTO filtroDTO) {
    	  Object listado = null;
          Map<String, Object> res = new HashMap<>();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          if (currentToken != null) {
              headers.set("Authorization", "Bearer "+currentToken);
  		}
          HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
        try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/filtrarPorArea", HttpMethod.POST, entity, String.class);
        		Gson g = new Gson();
		        Object listadoDTO = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDTO;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    @PostMapping("/propiedad/filtrarPorHabitaciones")
    public  Map<String, Object> filtrarPorHabitaciones(@RequestBody FiltroDTO filtroDTO) {
    	  Object listado = null;
          Map<String, Object> res = new HashMap<>();
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          if (currentToken != null) {
              headers.set("Authorization", "Bearer "+currentToken);
  		}
          HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
        try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:9092/propiedad/filtrarPorHabitaciones", HttpMethod.POST, entity, String.class);
        		Gson g = new Gson();
		        Object listadoDTO = g.fromJson(response.getBody(), Object.class);
		        listado = listadoDTO;
				res.put("respuesta", listado);
				return res;
		
		} catch(HttpStatusCodeException e){
			res.put("estado", e.getMostSpecificCause()+ " " + e.getRawStatusCode());
			return res;
		} 
    }
    
    

}
