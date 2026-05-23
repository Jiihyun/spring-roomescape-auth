package roomescape.store.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.store.domain.Store;

@Repository
public class StoreDao {

    private static final RowMapper<Store> ROW_MAPPER = (resultSet, rowNum) ->
            new Store(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public StoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("store")
                .usingGeneratedKeyColumns("id");
    }

    public Store save(Store store) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", store.getName());

        Number generatedId = jdbcInsert.executeAndReturnKey(parameters);

        return store.createWithId(generatedId.longValue());
    }

    public Optional<Store> findById(long id) {
        String sql = """
                SELECT id,
                       name
                FROM store
                WHERE id = ?
                """;

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    public List<Store> findAll() {
        String sql = """
                SELECT id,
                       name
                FROM store
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public boolean existsById(long id) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM store
                    WHERE id = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, id));
    }
}
