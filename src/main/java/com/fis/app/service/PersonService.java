package com.fis.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fis.app.dto.PersonDto;
import com.fis.app.dto.PersonRequestDto;
import com.fis.app.dto.ResponseThirdPartyDto;
import com.fis.app.entity.Person;
import com.fis.app.exception.PersonException;
import com.fis.app.repository.PersonRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PersonService {

	@Value("${demo.service.test.api-third-party}")
	private String apiThirdParty;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private PersonRepository personRepository;
	
	public PersonDto getPerson(PersonRequestDto dto) {
		
		log.info("Incoming request getPerson {}", dto.getId());
		
		PersonDto d = new PersonDto();
		
		Person p = personRepository.findById(dto.getId()).orElseThrow(()->new PersonException("Id Not Found"));
		
		d.setId(p.getId());
		d.setName(p.getName());
		d.setEmail(p.getEmail());
		
		return d;
	}
	
	public PersonDto getPersonFromThirdParty(PersonRequestDto p) {
		
		String queryParam = apiThirdParty+p.getId(); 
		
		log.info("Incoming request getPersonFromThirdParty {}", p.getId());
		
		ResponseEntity<ResponseThirdPartyDto> res =   restTemplate.getForEntity(queryParam, ResponseThirdPartyDto.class);;
		
		if(!res.getStatusCode().is2xxSuccessful()) {
			throw new PersonException("Error access to third party API");
		}
		
		PersonDto d = new PersonDto();

		d.setEmail(res.getBody().getEmail());
		d.setId(p.getId());
		d.setName(res.getBody().getName());
		
		return d;
	}
}
