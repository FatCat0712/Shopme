package com.shopme.common.entity;


import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(length = 128, nullable = false, unique = true)
        private String email;

        @Column(length = 100, nullable = false)
        private String password;

        @Column(name = "first_name", length = 45, nullable = false)
        private String firstName;

        @Column(name = "last_name", length = 45, nullable = false)
        private String lastName;

        @Column(length = 64)
        private String photos;
        private boolean enabled;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "users_roles",
                joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
                inverseJoinColumns  = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
        )
        private Set<Role> roles = new HashSet<>();

        public User() {
        }

        public User(String email, String password, String firstName, String lastName) {
                this.email = email;
                this.password = password;
                this.firstName = firstName;
                this.lastName = lastName;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getFirstName() {
                return firstName;
        }

        public void setFirstName(String firstName) {
                this.firstName = firstName;
        }

        public String getLastName() {
                return lastName;
        }

        public void setLastName(String lastName) {
                this.lastName = lastName;
        }

        public String getPhotos() {
                return photos;
        }

        public void setPhotos(String photos) {
                this.photos = photos;
        }

        public boolean isEnabled() {
                return enabled;
        }

        public void setEnabled(boolean enabled) {
                this.enabled = enabled;
        }

        public Set<Role> getRoles() {
                return roles;
        }

        public void setRoles(Set<Role> roles) {
                this.roles = roles;
        }

        public void addRole(Role role) {
                this.roles.add(role);
        }

        @Override
        public String toString() {
                return "User{" +
                        "id=" + id +
                        ", email='" + email + '\'' +
                        ", password='" + password + '\'' +
                        ", firstName='" + firstName + '\'' +
                        ", lastName='" + lastName + '\'' +
                        ", roles=" + roles +
                        '}';
        }

        @Transient
        public String getPhotosImagePath() {
                if(id == null || photos == null)  return "/images/default-user.png";
                return "/user-photos/" + this.id + "/" + this.photos;
        }

        @Transient
        public String getFullName() {
                return this.firstName + " " + this.lastName;
        }

        public boolean hasRole(String roleName) {
                return roles.stream().anyMatch(role -> role.getName().equals(roleName));
        }
}
