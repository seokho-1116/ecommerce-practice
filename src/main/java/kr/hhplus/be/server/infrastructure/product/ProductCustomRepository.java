package kr.hhplus.be.server.infrastructure.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithRank;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  public List<ProductIdWithRank> findTop5SellingProductIdsByBetweenCreatedTsOrderByAmount(
      LocalDateTime from, LocalDateTime to) {
    String query = """
        WITH ranked_products AS (
            SELECT po.product_id AS productId,
                ROW_NUMBER() OVER (ORDER BY SUM(oi.amount) DESC) AS sellingRank
            FROM order_item AS oi
            JOIN product_option AS po ON oi.product_option_id = po.id
            WHERE oi.created_at BETWEEN :from AND :to
            GROUP BY po.product_id
        )
        SELECT ranked_products.productId,
               ranked_products.sellingRank
        FROM ranked_products
        WHERE ranked_products.sellingRank <= 5
        ORDER BY ranked_products.sellingRank
        """;

    return namedParameterJdbcTemplate.query(
        query,
        new MapSqlParameterSource("from", from)
            .addValue("to", to),
        (rs, rowNum) -> new ProductIdWithRank(
            rs.getLong("sellingRank"),
            rs.getLong("productId")
        ));
  }
}
