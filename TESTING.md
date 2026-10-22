# Testing

## How to run

Run `.\mvnw.cmd test` from the project folder (on macOS/Linux: `./mvnw test`).

No PostgreSQL is needed. Controller tests mock the repository, and persistence tests use an in-memory H2 database.
There are 52 automated tests.

## Choice: slice tests

We use **slice tests** (`@WebMvcTest` and `@DataJpaTest`) plus plain unit tests instead of full `@SpringBootTest` integration tests.
Slice tests load only the layer under test, so they run in seconds, fail for one clear reason,
and do not depend on an external database. The trade-off is that they do not prove that all the layers work together
against real PostgreSQL, so we also checked the running app by hand (locally and on Render).

## What is tested

| Required behaviour | Test class | Tests |
|---|---|---|
| Invalid email rejected | StaffRatingValidationTest | invalidEmailRejected (5 bad inputs), validEmailAccepted |
| Out-of-range score rejected | StaffRatingValidationTest | outOfRange...Rejected for each score (0, -1, 11, 100), boundaryScoresAccepted (1 and 10) |
| Missing required fields rejected | StaffRatingValidationTest | missing or blank name, email, role and scores, emptyFormRejectsEveryRequiredField |
| Extra rules (comment and name length) | StaffRatingValidationTest | commentOver500CharactersRejected, nameOver100CharactersRejected |
| GET index returns 200 with model attributes | StaffRatingControllerTest | indexReturns200AndIncludesRatingsModelAttribute, empty state |
| POST create success redirects | StaffRatingControllerTest | createSuccessSavesAndRedirectsToDetailPage |
| POST create failure returns form with errors | StaffRatingControllerTest | createFailureReturnsFormWithErrorsAndDoesNotSave, non-numeric score |
| Update, values kept on failure | StaffRatingControllerTest | updateSuccess..., updateFailureRedisplaysForm... |
| Delete with confirmation | StaffRatingControllerTest | confirmation page, POST delete, missing id |
| Saving and retrieving works | StaffRatingRepositoryTest | savingAndRetrievingAnEntryWorks, timestamps, update |
| Delete removes the entry | StaffRatingRepositoryTest | deleteRemovesTheEntry |
| Polymorphism | StaffMemberProfileTest | right subclass per role, different titles per role |