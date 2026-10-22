package com.example.ratecsstaff.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/** 1) Validation tests: plain unit tests, no Spring needed. */
class StaffRatingValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static StaffRating valid() {
        StaffRating r = new StaffRating();
        r.setName("Ada Lovelace");
        r.setEmail("ada@sfu.ca");
        r.setRoleType(RoleType.PROF);
        r.setClarity(8);
        r.setNiceness(9);
        r.setKnowledgeableScore(10);
        r.setComment("Great lectures.");
        return r;
    }

    private static Set<String> invalidFields(StaffRating r) {
        return validator.validate(r).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void validRatingHasNoViolations() {
        assertThat(validator.validate(valid())).isEmpty();
    }

    @Test
    void commentIsOptional() {
        StaffRating r = valid();
        r.setComment(null);
        assertThat(validator.validate(r)).isEmpty();
    }

    // ----- invalid email rejected -----

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "missing@tld", "@no-local.com", "two@@at.com", "spaces in@x.com"})
    void invalidEmailRejected(String email) {
        StaffRating r = valid();
        r.setEmail(email);
        assertThat(invalidFields(r)).containsExactly("email");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ta@sfu.ca", "first.last+tag@cs.sfu.ca"})
    void validEmailAccepted(String email) {
        StaffRating r = valid();
        r.setEmail(email);
        assertThat(validator.validate(r)).isEmpty();
    }

    // ----- out-of-range scores rejected -----

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11, 100})
    void outOfRangeClarityRejected(int value) {
        StaffRating r = valid();
        r.setClarity(value);
        assertThat(invalidFields(r)).containsExactly("clarity");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11, 100})
    void outOfRangeNicenessRejected(int value) {
        StaffRating r = valid();
        r.setNiceness(value);
        assertThat(invalidFields(r)).containsExactly("niceness");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11, 100})
    void outOfRangeKnowledgeableRejected(int value) {
        StaffRating r = valid();
        r.setKnowledgeableScore(value);
        assertThat(invalidFields(r)).containsExactly("knowledgeableScore");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void boundaryScoresAccepted(int value) {
        StaffRating r = valid();
        r.setClarity(value);
        r.setNiceness(value);
        r.setKnowledgeableScore(value);
        assertThat(validator.validate(r)).isEmpty();
    }

    // ----- missing required fields rejected -----

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void blankNameRejected(String name) {
        StaffRating r = valid();
        r.setName(name);
        assertThat(invalidFields(r)).containsExactly("name");
    }

    @Test
    void missingNameRejected() {
        StaffRating r = valid();
        r.setName(null);
        assertThat(invalidFields(r)).containsExactly("name");
    }

    @Test
    void missingEmailRejected() {
        StaffRating r = valid();
        r.setEmail(null);
        assertThat(invalidFields(r)).containsExactly("email");
    }

    @Test
    void missingRoleRejected() {
        StaffRating r = valid();
        r.setRoleType(null);
        assertThat(invalidFields(r)).containsExactly("roleType");
    }

    @Test
    void missingScoresRejected() {
        StaffRating r = valid();
        r.setClarity(null);
        r.setNiceness(null);
        r.setKnowledgeableScore(null);
        assertThat(invalidFields(r)).containsExactlyInAnyOrder("clarity", "niceness", "knowledgeableScore");
    }

    @Test
    void emptyFormRejectsEveryRequiredField() {
        assertThat(invalidFields(new StaffRating()))
                .containsExactlyInAnyOrder("name", "email", "roleType", "clarity", "niceness", "knowledgeableScore");
    }

    // ----- extra rules of our choice -----

    @Test
    void commentOver500CharactersRejected() {
        StaffRating r = valid();
        r.setComment("x".repeat(501));
        assertThat(invalidFields(r)).containsExactly("comment");
    }

    @Test
    void commentOf500CharactersAccepted() {
        StaffRating r = valid();
        r.setComment("x".repeat(500));
        assertThat(validator.validate(r)).isEmpty();
    }

    @Test
    void nameOver100CharactersRejected() {
        StaffRating r = valid();
        r.setName("n".repeat(101));
        assertThat(invalidFields(r)).containsExactly("name");
    }

    @Test
    void overallScoreIsAverageOfThreeScores() {
        assertThat(valid().getOverallScore()).isEqualTo(9.0); // (8 + 9 + 10) / 3
    }
}