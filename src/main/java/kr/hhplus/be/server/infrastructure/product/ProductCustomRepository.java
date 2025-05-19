package kr.hhplus.be.server.infrastructure.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithTotalSales;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public List<ProductIdWithTotalSales> findAllSellingProductsWithRank(
      LocalDateTime from, LocalDateTime to) {
    String query = """
            SELECT po.product_id AS productId,
                SUM(oi.amount) AS totalSales
            FROM order_item AS oi
            JOIN product_option AS po ON oi.product_option_id = po.id
            WHERE oi.created_at BETWEEN :from AND :to
            GROUP BY po.product_id
        """;

    return namedParameterJdbcTemplate.query(
        query,
        new MapSqlParameterSource("from", from)
            .addValue("to", to),
        (rs, rowNum) -> new ProductIdWithTotalSales(
            rs.getLong("productId"),
            rs.getLong("totalSales")
        ));
  }
}
