package com.example.ratecsstaff.controller;

import com.example.ratecsstaff.model.RoleType;
import com.example.ratecsstaff.model.StaffRating;
import com.example.ratecsstaff.repository.StaffRatingRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/** MVC controller for the full CRUD workflow. */
@Controller
public class StaffRatingController {

    private static final String NOT_FOUND_MESSAGE = "That rating no longer exists.";

    private final StaffRatingRepository repository;

    public StaffRatingController(StaffRatingRepository repository) {
        this.repository = repository;
    }

    /** Never let a submitted form set these fields. */
    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "createdAt", "updatedAt");
    }

    /** Makes the list of roles available to every page (for the dropdown). */
    @ModelAttribute("roles")
    public RoleType[] roles() {
        return RoleType.values();
    }

    // ---------- READ ----------

    @GetMapping({"/", "/ratings"})
    public String index(Model model) {
        model.addAttribute("ratings", repository.findAllByOrderByCreatedAtDesc());
        return "index";
    }

    @GetMapping("/ratings/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<StaffRating> rating = repository.findById(id);
        if (rating.isEmpty()) {
            return notFound(redirect);
        }
        model.addAttribute("rating", rating.get());
        return "detail";
    }

    // ---------- CREATE ----------

    @GetMapping("/ratings/new")
    public String newForm(Model model) {
        model.addAttribute("rating", new StaffRating());
        prepareForm(model, "Add a rating", "/ratings");
        return "form";
    }

    @PostMapping("/ratings")
    public String create(@Valid @ModelAttribute("rating") StaffRating rating,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "Add a rating", "/ratings");
            return "form";
        }
        StaffRating saved = repository.save(rating);
        redirect.addFlashAttribute("message", "Rating added.");
        return "redirect:/ratings/" + saved.getId();
    }

    // ---------- UPDATE ----------

    @GetMapping("/ratings/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<StaffRating> rating = repository.findById(id);
        if (rating.isEmpty()) {
            return notFound(redirect);
        }
        model.addAttribute("rating", rating.get());
        prepareForm(model, "Edit rating", "/ratings/" + id + "/edit");
        return "form";
    }

    @PostMapping("/ratings/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("rating") StaffRating form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        Optional<StaffRating> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return notFound(redirect);
        }
        if (result.hasErrors()) {
            // The submitted values stay in the "rating" model attribute, so the form is refilled.
            prepareForm(model, "Edit rating", "/ratings/" + id + "/edit");
            return "form";
        }
        StaffRating entity = existing.get();
        entity.copyFrom(form);
        repository.save(entity);
        redirect.addFlashAttribute("message", "Rating updated.");
        return "redirect:/ratings/" + id;
    }

    // ---------- DELETE (with a confirmation page) ----------

    @GetMapping("/ratings/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<StaffRating> rating = repository.findById(id);
        if (rating.isEmpty()) {
            return notFound(redirect);
        }
        model.addAttribute("rating", rating.get());
        return "delete";
    }

    @PostMapping("/ratings/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        if (!repository.existsById(id)) {
            return notFound(redirect);
        }
        repository.deleteById(id);
        redirect.addFlashAttribute("message", "Rating deleted.");
        return "redirect:/ratings";
    }

    // ---------- helpers ----------

    private void prepareForm(Model model, String pageTitle, String formAction) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", formAction);
    }

    private String notFound(RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", NOT_FOUND_MESSAGE);
        return "redirect:/ratings";
    }
}