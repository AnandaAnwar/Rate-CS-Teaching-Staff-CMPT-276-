package com.example.ratecsstaff.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** OO design: polymorphic role behaviour. */
class StaffMemberProfileTest {

    @Test
    void factoryReturnsTheRightSubclassForEachRole() {
        assertThat(StaffMemberProfile.forRole(RoleType.TA, "A")).isInstanceOf(TaProfile.class);
        assertThat(StaffMemberProfile.forRole(RoleType.PROF, "A")).isInstanceOf(ProfProfile.class);
        assertThat(StaffMemberProfile.forRole(RoleType.INSTRUCTOR, "A")).isInstanceOf(InstructorProfile.class);
        assertThat(StaffMemberProfile.forRole(RoleType.STAFF, "A")).isInstanceOf(StaffProfile.class);
    }

    @Test
    void sameCallGivesDifferentTitlesPerRole() {
        StaffMemberProfile ta = StaffMemberProfile.forRole(RoleType.TA, "Ada Lovelace");
        StaffMemberProfile prof = StaffMemberProfile.forRole(RoleType.PROF, "Ada Lovelace");

        assertThat(ta.displayTitle()).isEqualTo("Ada Lovelace (TA)");
        assertThat(prof.displayTitle()).isEqualTo("Prof. Ada Lovelace");
        assertThat(ta.roleDescription()).isNotEqualTo(prof.roleDescription());
    }

    @Test
    void ratingExposesItsProfile() {
        StaffRating r = new StaffRating();
        r.setName("Grace Hopper");
        r.setRoleType(RoleType.INSTRUCTOR);
        assertThat(r.getProfile().displayTitle()).isEqualTo("Grace Hopper (Instructor)");
    }
}