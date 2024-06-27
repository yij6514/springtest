package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

interface CoffeeRepository extends CrudRepository<Coffee, String> {}

//h2를 위한 jpa의 어노테이션
@Entity
class Coffee{
	@Id
	private String id;
	private String name;
	
	public Coffee() {
		
	}

	public Coffee(String id, String name){
		this.id = id;
		this.name = name;
	}

	public Coffee(String name){
		this(UUID.randomUUID().toString(), name);
	}

	public String getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}

@RestController
//localhost:8080/coffee 로 요청하는 http 요청을 수행
@RequestMapping("/coffees")
class RestApiDemoController{
	//arraylist 대신에 사용되는 사용되는 객체
	private final CoffeeRepository coffeeRepository;
	
	//기본적인 생성자
	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
		
		//crudRepository에 saveall 메소드는 일련의 리스트를 사용한다.
		this.coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
				));
	}
	
	//HTTP Get 메소드로 localhost:8080/coffees 로 리스트에 있는 커피를 모두 보여줌
	@GetMapping
	Iterable<Coffee> getCoffees() {
		//findall 메소드는 안에 들어있는 객체를 전부 반환
		return coffeeRepository.findAll();
	}
	
	//HTTP Get 메소드로 localhost:8080/coffees/id 로 리스트 중에 있는 특정 id의 커피를 보여줌
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		return coffeeRepository.findById(id);
	}
	
	//HTTP Post 메소드로 localhost:8080/coffees 로 받을때 json에 있는 정보로 커피를 추가함
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		return coffeeRepository.save(coffee);
	}
	
	//HTTP Put 메소드로 localhost:8080/coffees/id 로 받을때 json에 있는 정보로 만약 coffees 리스트에 없으면 return 함수로 추가 있다면 수정함
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		return (!coffeeRepository.existsById(id))
				? new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.CREATED)
				: new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.OK);
	}
	
	//HTTP Post 메소드로 localhost:8080/coffees/id 로 받을때 id의 정보로 커피를 삭제함
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
	}
}