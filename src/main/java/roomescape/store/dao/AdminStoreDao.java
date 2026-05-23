package roomescape.store.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminStoreDao {

    private final JdbcTemplate jdbcTemplate;

    public AdminStoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long memberId, long storeId) {
        String sql = """
                INSERT INTO admin_store (member_id, store_id)
                VALUES (?, ?)
                """;
        jdbcTemplate.update(sql, memberId, storeId);
    }

    public boolean existsByMemberIdAndStoreId(long memberId, long storeId) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM admin_store
                    WHERE member_id = ?
                        AND store_id = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, memberId, storeId));
    }
}
