package co.com.udem.inmobiliariaclient.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
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

	@Autowired
	private LoadBalancerClient loadBalancer;

	@PostMapping("/autenticar")
	public AutenticationResponseDTO autenticar(@RequestBody AutenticationRequestDTO autenticationRequestDTO) {

		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/auth/signin";

		ResponseEntity<String> postResponse = restTemplate.postForEntity(baseUrl, autenticationRequestDTO,
				String.class);
		Gson g = new Gson();
		AutenticationResponseDTO autenticationResponseDTO = g.fromJson(postResponse.getBody(),
				AutenticationResponseDTO.class);
		userToken.setUsername(autenticationResponseDTO.getUsername());
		userToken.setToken(autenticationResponseDTO.getToken());
		String usuario = userTokenRepository.findByUsername(autenticationResponseDTO.getUsername());
		if (usuario == null) {
			userTokenRepository.save(userToken);
		} else {
			userTokenRepository.updateToken(autenticationResponseDTO.getToken(),
					autenticationResponseDTO.getUsername());
		}

		currentToken = userTokenRepository.obtenerToken(autenticationResponseDTO.getUsername());

		return autenticationResponseDTO;

	}

	@GetMapping("/registro/listarUsuarios")
	public ResponseEntity<Object> listarUsuarios() {

		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/registro/listarUsuarios";
		Object listadoUsuarios = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

			Gson g = new Gson();
			Object registroDto = g.fromJson(response.getBody(), Object.class);
			listadoUsuarios = registroDto;
			res.put("respuesta", listadoUsuarios);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@GetMapping("/registro/listarUsuario/{id}")
	public ResponseEntity<Object> listarUsuariosId(@PathVariable String id) {

		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/registro/listarUsuario/";
		Object listadoUsuarios = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.GET, entity, String.class);

			Gson g = new Gson();
			Object registroDto = g.fromJson(response.getBody(), Object.class);
			listadoUsuarios = registroDto;
			res.put("respuesta", listadoUsuarios);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PostMapping("/registro/registrarUsuario")
	public ResponseEntity<Object> registrarUsuario(@RequestBody RegistroDTO registroDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/registro/registrarUsuario";
		Object listadoUsuarios = null;
		Map<String, Object> res = new HashMap<>();
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, registroDTO, String.class);

			Gson g = new Gson();
			Object registroDto = g.fromJson(response.getBody(), Object.class);
			listadoUsuarios = registroDto;
			res.put("respuesta", listadoUsuarios);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PutMapping("/registro/modificarUsuario/{id}")
	public ResponseEntity<Object> modificarUsuario(@RequestBody RegistroDTO registroDTO, @PathVariable Long id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/registro/modificarUsuario/";
		Object listadoUsuarios = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<RegistroDTO> entity = new HttpEntity<>(registroDTO, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.PUT, entity, String.class);

			Gson g = new Gson();
			Object registroDto = g.fromJson(response.getBody(), Object.class);
			listadoUsuarios = registroDto;
			res.put("respuesta", listadoUsuarios);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@DeleteMapping("/registro/eliminarUsuario/{id}")
	public ResponseEntity<Object> eliminarUsuario(@PathVariable String id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/registro/eliminarUsuario/";
		Object listadoUsuarios = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.DELETE, entity,
					String.class);

			Gson g = new Gson();
			Object registroDto = g.fromJson(response.getBody(), Object.class);
			listadoUsuarios = registroDto;
			res.put("respuesta", listadoUsuarios);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	/* Tipo Id */

	@GetMapping("/tipoidentificacion/obtenerTipoId")
	public ResponseEntity<Object> listarTiposId() {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/tipoidentificacion/obtenerTipoId";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@GetMapping("/tipoidentificacion/obtenerTipoId/{id}")
	public ResponseEntity<Object> listarTipoId(@PathVariable String id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/tipoidentificacion/obtenerTipoId/";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.GET, entity, String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PostMapping("/tipoidentificacion/registrarTipoId")
	public ResponseEntity<Object> registrarTipoID(@RequestBody TipoIdentificacionDTO tipoIdDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/tipoidentificacion/registrarTipoId";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}
		HttpEntity<TipoIdentificacionDTO> entity = new HttpEntity<>(tipoIdDTO, headers);
		try {

			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
			Gson g = new Gson();
			Object listadoDTO = g.fromJson(response.getBody(), Object.class);
			listado = listadoDTO;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PutMapping("/tipoidentificacion/modificarTipoId/{id}")
	public ResponseEntity<Object> modificarTipo(@RequestBody TipoIdentificacionDTO tipoIdDTO, @PathVariable Long id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/tipoidentificacion/modificarTipoId/";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<TipoIdentificacionDTO> entity = new HttpEntity<>(tipoIdDTO, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.PUT, entity, String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@DeleteMapping("/tipoidentificacion/eliminarTipoId/{id}")
	public ResponseEntity<Object> eliminarTipoId(@PathVariable String id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/tipoidentificacion/eliminarTipoId/";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.DELETE, entity,
					String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	/* Propiedad */

	@GetMapping("/propiedad/listarPropiedades")
	public ResponseEntity<Object> listarPropiedades() {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/listarPropiedades";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("estado", e.getMostSpecificCause() + " " + e.getRawStatusCode());
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@GetMapping("/propiedad/listarPropiedad/{id}")
	public ResponseEntity<Object> listarPropiedad(@PathVariable String id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/listarPropiedad/";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.GET, entity, String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PostMapping("/propiedad/registrarPropiedad")
	public ResponseEntity<Object> registrarPropiedad(@RequestBody PropiedadDTO propiedadDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/registrarPropiedad";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}
		HttpEntity<PropiedadDTO> entity = new HttpEntity<>(propiedadDTO, headers);
		try {

			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
			Gson g = new Gson();
			Object listadoDTO = g.fromJson(response.getBody(), Object.class);
			listado = listadoDTO;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PutMapping("/propiedad/modificarPropiedad/{id}")
	public ResponseEntity<Object> modificarPropiedad(@RequestBody PropiedadDTO propiedadDTO, @PathVariable Long id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/modificarPropiedad/";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<PropiedadDTO> entity = new HttpEntity<>(propiedadDTO, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.PUT, entity, String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@DeleteMapping("/propiedad/eliminarPropiedad/{id}")
	public ResponseEntity<Object> eliminarPropiedad(@PathVariable String id) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/eliminarPropiedad/";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(baseUrl + id, HttpMethod.DELETE, entity,
					String.class);

			Gson g = new Gson();
			Object listadoDto = g.fromJson(response.getBody(), Object.class);
			listado = listadoDto;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	/*
	 * Filtro con criteria query api: Genera automáticamente la consulta de acuerdo
	 * a los valores enviados
	 */

	@PostMapping("/propiedad/filtrarPropiedad")
	public ResponseEntity<Object> filtrarPropiedad(@RequestBody FiltroDTO filtroDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/filtrarPropiedad";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}
		HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
		try {

			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
			Gson g = new Gson();
			Object listadoDTO = g.fromJson(response.getBody(), Object.class);
			listado = listadoDTO;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PostMapping("/propiedad/filtrarPorValor")
	public ResponseEntity<Object> filtrarValor(@RequestBody FiltroDTO filtroDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/filtrarPorValor";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}
		HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
		try {

			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
			Gson g = new Gson();
			Object listadoDTO = g.fromJson(response.getBody(), Object.class);
			listado = listadoDTO;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PostMapping("/propiedad/filtrarPorArea")
	public ResponseEntity<Object> filtrarPorArea(@RequestBody FiltroDTO filtroDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/filtrarPorArea";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}
		HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
		try {

			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
			Gson g = new Gson();
			Object listadoDTO = g.fromJson(response.getBody(), Object.class);
			listado = listadoDTO;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

	@PostMapping("/propiedad/filtrarPorHabitaciones")
	public ResponseEntity<Object> filtrarPorHabitaciones(@RequestBody FiltroDTO filtroDTO) {
		ServiceInstance serviceInstance = loadBalancer.choose("proyecto-inmobiliaria");
		System.out.println(serviceInstance.getUri());
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl = baseUrl + "/propiedad/filtrarPorHabitaciones";
		Object listado = null;
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (currentToken != null) {
			headers.set("Authorization", "Bearer " + currentToken);
		}
		HttpEntity<FiltroDTO> entity = new HttpEntity<>(filtroDTO, headers);
		try {

			ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
			Gson g = new Gson();
			Object listadoDTO = g.fromJson(response.getBody(), Object.class);
			listado = listadoDTO;
			res.put("respuesta", listado);
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (HttpStatusCodeException e) {
			res.put("respuesta", e.getResponseBodyAsString());
			return new ResponseEntity<>(res, HttpStatus.valueOf(e.getRawStatusCode()));
		}
	}

}
