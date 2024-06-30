package jzxy.cbq.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jzxy.cbq.entity.Person;
import jzxy.cbq.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin-ui/people")
@RequiredArgsConstructor
public class PersonResource {

    private final PersonRepository personRepository;

    private final ObjectMapper objectMapper;

    @GetMapping
    public Page<Person> getList(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Person getOne(@PathVariable Integer id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return personOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @GetMapping("/by-ids")
    public List<Person> getMany(@RequestParam List<Integer> ids) {
        return personRepository.findAllById(ids);
    }

    @PostMapping
    public Person create(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @PatchMapping("/{id}")
    public Person patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
        Person person = personRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(person).readValue(patchNode);

        return personRepository.save(person);
    }

    @PatchMapping
    public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
        Collection<Person> people = personRepository.findAllById(ids);

        for (Person person : people) {
            objectMapper.readerForUpdating(person).readValue(patchNode);
        }

        List<Person> resultPeople = personRepository.saveAll(people);
        List<Integer> ids1 = resultPeople.stream()
                .map(Person::getId)
                .toList();
        return ids1;
    }

    @DeleteMapping("/{id}")
    public Person delete(@PathVariable Integer id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person != null) {
            personRepository.delete(person);
        }
        return person;
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Integer> ids) {
        personRepository.deleteAllById(ids);
    }
}
