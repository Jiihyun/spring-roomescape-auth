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
}
