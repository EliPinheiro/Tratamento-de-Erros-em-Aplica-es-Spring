package com.eli.eli_food.api.controler;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.flywaydb.core.internal.util.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eli.eli_food.domain.exception.EntidadeNaoEncontradaException;
import com.eli.eli_food.domain.exception.NegocioException;
import com.eli.eli_food.domain.model.Restaurante;
import com.eli.eli_food.domain.repository.RestauranteRepository;
import com.eli.eli_food.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroRestauranteService cadastroRestaurante;
	
	@GetMapping
	public List<Restaurante> listar(){
		return restauranteRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Restaurante buscar(@PathVariable Long id){
		
		return cadastroRestaurante.buscarOuFalhar(id);
	}

	
	@PostMapping
	public Restaurante adicionar(@RequestBody Restaurante restaurante){
		try {
			return  cadastroRestaurante.salvar(restaurante);
		}catch(EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());//aqui ele vai retornar o codigo 400 pois o recurso existe , perem o parametro da requisição nao
		}
			
		
		
	}
	
	
	@PutMapping("/{id}")
	public Restaurante atualizar(@PathVariable Long id, @RequestBody Restaurante restaurante){
		
			Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(id);
			BeanUtils.copyProperties(restaurante, restauranteAtual, "id", "formasPagamento", "endereco", "dataCadastro", "produtos");
			try {
				return  cadastroRestaurante.salvar(restauranteAtual);
			}catch(EntidadeNaoEncontradaException e) {
				throw new NegocioException(e.getMessage());//aqui ele vai retornar o codigo 400 pois o recurso existe , perem o parametro da requisição nao
			}
		
	}
	
	@PatchMapping("/{id}")
	public Restaurante atualizarParcial(@PathVariable Long id,  @RequestBody Map<String, Object> campos,
			HttpServletRequest request){
		
		
		
		Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(id);

		
		merge(campos, restauranteAtual, request);
		
		return atualizar(id, restauranteAtual);
		
	}

	private void merge(Map<String, Object> camposOrigem, Restaurante restauranteDestino, HttpServletRequest request) {
		
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();    //Cria uma instância de ObjectMapper, uma classe da biblioteca Jackson que facilita a conversão entre objetos Java e representações JSON.
		
		
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		
		Restaurante restauranteOrigem = objectMapper.convertValue(camposOrigem, Restaurante.class); //Essa linha converte o Map<String, Object> camposOrigem em uma instância do tipo Restaurante usando a biblioteca Jackson. O ObjectMapper é responsável por mapear os valores do Map para os campos correspondentes no objeto Restaurante.
		
		camposOrigem.forEach((nomePropriedade, valorPropriedade) -> {
			Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade); //Essa linha usa a classe ReflectionUtils para localizar um campo específico na classe Restaurante com base no nome da propriedade (nomePropriedade)
			field.setAccessible(true); //Esse campo consegue acessar a propriedade da classe mesmo ela sendo privada
			
			Object novoValor = ReflectionUtils.getField(field, restauranteOrigem); //Essa linha obtém o valor atual do campo especificado no objeto restauranteOrigem usando o objeto Field encontrado anteriormente. 
			ReflectionUtils.setField(field, restauranteDestino, novoValor); //Essa linha define o valor de um campo específico no objeto restauranteDestino usando o valor obtido do campo no objeto restauranteOrigem
		});
		}catch (IllegalArgumentException e) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			throw new HttpMessageNotReadableException(e.getMessage(), rootCause, serverHttpRequest);
		}
		
		
		
		
	}
}

















