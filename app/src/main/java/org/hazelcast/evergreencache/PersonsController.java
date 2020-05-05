package org.hazelcast.evergreencache;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PersonsController {

    private final PersonRepository repository;

    public PersonsController(PersonRepository repository) {
        this.repository = repository;
    }

    @ModelAttribute
    public Form form() {
        return new Form(repository.count());
    }

    @GetMapping("/")
    public String displayAll(Model model) {
        model.addAttribute("form", new Form(repository.findAll()));
        return "persons";
    }

    @PostMapping("/update/{index}")
    public String update(@PathVariable int index, @ModelAttribute Form form) {
        var person = form.getPersons().get(index);
        repository.save(person);
        return "redirect:/";
    }
}