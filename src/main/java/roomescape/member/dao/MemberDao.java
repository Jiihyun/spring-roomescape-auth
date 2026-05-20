package roomescape.member.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Role;


@Repository
public class MemberDao {

    private static final RowMapper<Member> ROW_MAPPER = (rs, rowNum) ->
            new Member(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    Role.valueOf(rs.getString("role"))
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");
    }

    public Member save(Member member) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName());
        parameters.put("email", member.getEmail());
        parameters.put("password", member.getPassword());
        parameters.put("role", member.getRole().name());

        Number generatedId = jdbcInsert.executeAndReturnKey(parameters);

        return member.createWithId(generatedId.longValue());
    }

    public Optional<Member> findByEmail(String email) {
        String sql = """
                SELECT id,
                       name,
                       email,
                       password,
                       role
                FROM member
                WHERE email = ?
                """;

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, ROW_MAPPER, email));
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    public Optional<Member> findById(long id) {
        String sql = """
                SELECT id,
                       name,
                       email,
                       password,
                       role
                FROM member
                WHERE id = ?
                """;

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM member
                    WHERE email = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, email));
    }
}
