package roomescape.storetheme.dao;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import roomescape.storetheme.domain.StoreTheme;

@Repository
public class StoreThemeDao {

    private final JdbcTemplate jdbcTemplate;

    public StoreThemeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(StoreTheme storeTheme) {
        String sql = """
                INSERT INTO store_theme (store_id, theme_id)
                VALUES (?, ?)
                """;
        jdbcTemplate.update(sql, storeTheme.getStoreId(), storeTheme.getThemeId());
    }

    public boolean existsByStoreIdAndThemeId(long storeId, long themeId) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM store_theme
                    WHERE store_id = ?
                        AND theme_id = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, storeId, themeId));
    }

    public Optional<Long> findFirstStoreIdByThemeId(long themeId) {
        String sql = """
                SELECT MIN(store_id)
                FROM store_theme
                WHERE theme_id = ?
                """;
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Long.class, themeId));
    }

    public int deleteByThemeId(long themeId) {
        String sql = """
                DELETE FROM store_theme
                WHERE theme_id = ?
                """;
        return jdbcTemplate.update(sql, themeId);
    }
}
