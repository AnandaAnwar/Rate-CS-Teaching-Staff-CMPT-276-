package com.example.ratecsstaff.controller;

import com.example.ratecsstaff.model.RoleType;
import com.example.ratecsstaff.model.StaffRating;
import com.example.ratecsstaff.repository.StaffRatingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 2) Web/controller tests. "Slice" tests: @WebMvcTest loads only the web layer and the
 * repository is mocked, so these run fast and need no database.
 */
@WebMvcTest(StaffRatingController.class)
class StaffRatingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StaffRatingRepository repository;

    private static StaffRating sample(Long id) {
        StaffRating r = new StaffRating();
        r.setId(id);
        r.setName("Ada Lovelace");
        r.setEmail("ada@sfu.ca");
        r.setRoleType(RoleType.PROF);
        r.setClarity(8);
        r.setNiceness(9);
        r.setKnowledgeableScore(10);
        r.setComment("Great lectures.");
        return r;
    }

    private static MockHttpServletRequestBuilder withValidForm(MockHttpServletRequestBuilder request) {
        return request
                .param("name", "Grace Hopper")
                .param("email", "grace@sfu.ca")
                .param("roleType", "TA")
                .param("clarity", "7")
                .param("niceness", "8")
                .param("knowledgeableScore", "9")
                .param("comment", "Helpful in labs.");
    }

    // ---------- GET index ----------

    @Test
    void indexReturns200AndIncludesRatingsModelAttribute() throws Exception {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sample(1L)));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("ratings"))
                .andExpect(content().string(containsString("Prof. Ada Lovelace")))
                .andExpect(content().string(containsString("9.0")));
    }

    @Test
    void indexShowsEmptyStateWhenThereAreNoRatings() throws Exception {
        mvc.perform(get("/ratings"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No ratings yet")));
    }

    // ---------- GET detail ----------

    @Test
    void detailShowsOneRatingWithEditAndDeleteLinks() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(sample(1L)));

        mvc.perform(get("/ratings/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(model().attributeExists("rating"))
                .andExpect(content().string(containsString("/ratings/1/edit")))
                .andExpect(content().string(containsString("/ratings/1/delete")));
    }

    @Test
    void detailForMissingIdRedirectsToIndexWithMessage() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/ratings/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ratings"))
                .andExpect(flash().attributeExists("error"));
    }

    // ---------- POST create ----------

    @Test
    void newFormReturns200() throws Exception {
        mvc.perform(get("/ratings/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeExists("rating", "roles"));
    }

    @Test
    void createSuccessSavesAndRedirectsToDetailPage() throws Exception {
        when(repository.save(any(StaffRating.class))).thenAnswer(inv -> {
            StaffRating saved = inv.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        mvc.perform(withValidForm(post("/ratings")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ratings/5"))
                .andExpect(flash().attributeExists("message"));

        verify(repository).save(any(StaffRating.class));
    }

    @Test
    void createFailureReturnsFormWithErrorsAndDoesNotSave() throws Exception {
        mvc.perform(post("/ratings")
                        .param("name", "")
                        .param("email", "not-an-email")
                        .param("roleType", "PROF")
                        .param("clarity", "11")
                        .param("niceness", "5")
                        .param("knowledgeableScore", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeHasFieldErrors("rating", "name", "email", "clarity"))
                .andExpect(content().string(containsString("Enter a valid email address")))
                .andExpect(content().string(containsString("Clarity must be between 1 and 10")));

        verify(repository, never()).save(any());
    }

    @Test
    void createWithNonNumericScoreShowsErrorInsteadOfCrashing() throws Exception {
        mvc.perform(post("/ratings")
                        .param("name", "Grace Hopper")
                        .param("email", "grace@sfu.ca")
                        .param("roleType", "TA")
                        .param("clarity", "abc")
                        .param("niceness", "8")
                        .param("knowledgeableScore", "9"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeHasFieldErrors("rating", "clarity"));

        verify(repository, never()).save(any());
    }

    @Test
    void createCannotSetTheIdFromTheForm() throws Exception {
        when(repository.save(any(StaffRating.class))).thenAnswer(inv -> inv.getArgument(0));

        mvc.perform(withValidForm(post("/ratings")).param("id", "42"))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<StaffRating> captor = ArgumentCaptor.forClass(StaffRating.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    // ---------- Update ----------

    @Test
    void editFormIsPrefilledWithExistingValues() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(sample(1L)));

        mvc.perform(get("/ratings/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(content().string(containsString("Ada Lovelace")))
                .andExpect(content().string(containsString("ada@sfu.ca")));
    }

    @Test
    void updateSuccessSavesAndRedirectsToDetailPage() throws Exception {
        StaffRating existing = sample(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(StaffRating.class))).thenAnswer(inv -> inv.getArgument(0));

        mvc.perform(withValidForm(post("/ratings/1/edit")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ratings/1"));

        assertThat(existing.getId()).isEqualTo(1L);
        assertThat(existing.getName()).isEqualTo("Grace Hopper");
        assertThat(existing.getRoleType()).isEqualTo(RoleType.TA);
        verify(repository).save(existing);
    }

    @Test
    void updateFailureRedisplaysFormWithSubmittedValuesAndErrors() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(sample(1L)));

        mvc.perform(withValidForm(post("/ratings/1/edit")).param("email", "bad").param("name", "Typed Name"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeHasFieldErrors("rating", "email"))
                .andExpect(content().string(containsString("Typed Name")))
                .andExpect(content().string(containsString("Enter a valid email address")));

        verify(repository, never()).save(any());
    }

    // ---------- Delete ----------

    @Test
    void deleteConfirmationPageIsShownBeforeDeleting() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(sample(1L)));

        mvc.perform(get("/ratings/1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete"))
                .andExpect(content().string(containsString("Delete this rating?")));

        verify(repository, never()).deleteById(any());
    }

    @Test
    void deletePostRemovesEntryAndRedirectsToIndex() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mvc.perform(post("/ratings/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ratings"))
                .andExpect(flash().attributeExists("message"));

        verify(repository).deleteById(1L);
    }

    @Test
    void deletePostForMissingIdDoesNotCrash() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mvc.perform(post("/ratings/99/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));

        verify(repository, never()).deleteById(any());
    }
}