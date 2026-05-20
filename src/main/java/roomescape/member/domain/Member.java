package roomescape.member.domain;

import roomescape.reservation.domain.Role;

public class Member {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;

    public static Member createUser(String name, String email, String password) {
        return new Member(null, name, email, password, Role.USER);
    }

    public Member(Long id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member createWithId(long id) {
        return new Member(id, this.name, this.email, this.password, this.role);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Member member = (Member) o;
        if (id != null && member.id != null) {
            return java.util.Objects.equals(id, member.id);
        }
        return java.util.Objects.equals(name, member.name) &&
                java.util.Objects.equals(email, member.email) &&
                java.util.Objects.equals(role, member.role);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return java.util.Objects.hash(id);
        }
        return java.util.Objects.hash(name, email, role);
    }
}
