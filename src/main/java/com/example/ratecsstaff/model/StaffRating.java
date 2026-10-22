package com.example.ratecsstaff.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * One rating of one staff member.
 *
 * Email rule: emails are NOT unique. The email identifies the staff member being rated,
 * and many students may each submit their own rating of the same person.
 */
@Entity
@Table(name = "staff_ratings")
public class StaffRating {

    private static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(regexp = EMAIL_PATTERN, message = "Enter a valid email address, like name@example.com")
    @Size(max = 150, message = "Email must be at most 150 characters")
    @Column(nullable = false, length = 150)
    private String email;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType roleType;

    @NotNull(message = "Clarity is required")
    @Min(value = 1, message = "Clarity must be between 1 and 10")
    @Max(value = 10, message = "Clarity must be between 1 and 10")
    @Column(nullable = false)
    private Integer clarity;

    @NotNull(message = "Niceness is required")
    @Min(value = 1, message = "Niceness must be between 1 and 10")
    @Max(value = 10, message = "Niceness must be between 1 and 10")
    @Column(nullable = false)
    private Integer niceness;

    @NotNull(message = "Knowledgeable score is required")
    @Min(value = 1, message = "Knowledgeable score must be between 1 and 10")
    @Max(value = 10, message = "Knowledgeable score must be between 1 and 10")
    @Column(nullable = false)
    private Integer knowledgeableScore;

    @Size(max = 500, message = "Comment must be at most 500 characters")
    @Column(name = "comment_text", length = 500)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public StaffRating() {
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** Copies the editable fields from a submitted form (used when editing). */
    public void copyFrom(StaffRating other) {
        this.name = other.name;
        this.email = other.email;
        this.roleType = other.roleType;
        this.clarity = other.clarity;
        this.niceness = other.niceness;
        this.knowledgeableScore = other.knowledgeableScore;
        this.comment = other.comment;
    }

    /** Average of the three scores (0 if any is missing). */
    public double getOverallScore() {
        if (clarity == null || niceness == null || knowledgeableScore == null) {
            return 0.0;
        }
        return (clarity + niceness + knowledgeableScore) / 3.0;
    }

    /** The polymorphic role object. The web pages use this. */
    public StaffMemberProfile getProfile() {
        return StaffMemberProfile.forRole(roleType, name);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim(); }

    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }

    public Integer getClarity() { return clarity; }
    public void setClarity(Integer clarity) { this.clarity = clarity; }

    public Integer getNiceness() { return niceness; }
    public void setNiceness(Integer niceness) { this.niceness = niceness; }

    public Integer getKnowledgeableScore() { return knowledgeableScore; }
    public void setKnowledgeableScore(Integer knowledgeableScore) { this.knowledgeableScore = knowledgeableScore; }

    public String getComment() { return comment; }
    public void setComment(String comment) {
        this.comment = (comment == null || comment.isBlank()) ? null : comment.trim();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
