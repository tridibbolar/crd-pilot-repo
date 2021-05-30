package com.tcg.micro.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.tcg.micro.rest.domain.Employee;
import com.tcg.micro.rest.exception.ResourceException;
import com.tcg.micro.rest.repository.EmployeeRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class EmployeeController {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private EmployeeRepository theEmployeeRepository;

	@GetMapping("/employees")
	public List<Employee> retrieveAllEmployees() {
		LOG.debug("Retrieve all Resource -------");
		return theEmployeeRepository.findAll();
	}

	@GetMapping("/employees/{id}")
	public Resource<Employee> retrieveEmployee(@PathVariable long id) {
		Optional<Employee> options = theEmployeeRepository.findById(id);

		if (!options.isPresent()){
			LOG.error("Resource not found for the Id # {}",id);
			throw new ResourceException("Resource Not found with id-" + id);
		}

		Resource<Employee> resource = new Resource<Employee>(options.get());

		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllEmployees());

		resource.add(linkTo.withRel("all-students"));

		return resource;
	}

	@DeleteMapping("/employees/{id}")
	public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
		Optional<Employee> student = theEmployeeRepository.findById(id);

		if (!student.isPresent()){
			LOG.error("Resource not found for the Id # {}",id);
			throw new ResourceException("Delete Failed. Resource Not found with id-" + id);
		}

		theEmployeeRepository.deleteById(id);
		return new ResponseEntity<>("Resource Deleted Successfully with Id # "+id, HttpStatus.OK);
	}

	@PostMapping("/employees")
	public ResponseEntity<String> createEmployee(@RequestBody Employee resource) {
		Optional<Employee> std = theEmployeeRepository.findById(resource.getId());

		if (std.isPresent()){
			LOG.error("Resource found for the Id # {}. New entry not possible.",resource.getId());
			throw new ResourceException("Resource found for the Id # "+resource.getId()+" New entry not possible.");
			//throw new Exception("Employee found for the Id # "+resourse.getId()+" New entry not possible.");
		}
		Employee savedResource = theEmployeeRepository.save(resource);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedResource.getId()).toUri();

		//return ResponseEntity.created(location).build();

		return new ResponseEntity<>("Resource created with Id # "+savedResource.getId(), HttpStatus.OK);

	}

	@PutMapping("/employees/{id}")
	public ResponseEntity<String> updateEmployee(@RequestBody Employee resource, @PathVariable long id) {

		Optional<Employee> optional = theEmployeeRepository.findById(id);

		if (!optional.isPresent()){
			LOG.error("Resource not found for the Id # {}",id);
			throw new ResourceException("Update Failed. Resource Not found with id-" + id);
			//return ResponseEntity.notFound().build();
		}

		resource.setId(id);
		theEmployeeRepository.save(resource);
		//return ResponseEntity.noContent().build();
		return new ResponseEntity<>("Resource updated with Id # "+id, HttpStatus.OK);
	}
}
