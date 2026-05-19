package roomescape.reservation.domain;

public enum Role {

    ADMIN("관리자"),
    USER("일반");

    private String description;

    Role(final String description) {
        this.description = description;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
