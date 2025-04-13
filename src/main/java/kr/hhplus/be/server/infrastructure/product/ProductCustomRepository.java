package kr.hhplus.be.server.infrastructure.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  public List<ProductWithRank> findTop5SellingProductsByBetweenCreatedTsOrderBySellingPrice(
      LocalDateTime from, LocalDateTime to) {
    String query = """
        SELECT p.id AS productId,
                p.name AS productName,
                p.base_price AS basePrice,
                p.description AS productDescription,
                oi_rank.amount AS amount,
                oi_rank.sellingRank AS sellingRank,
                oi_rank.productId AS productId
        FROM
        (SELECT po.product_id AS productId,
                SUM(oi.amount) AS amount,
                ROW_NUMBER() OVER (ORDER BY amount DESC) AS sellingRank
        FROM order_item AS oi
        JOIN product_option AS po ON oi.product_option_id = po.id
        WHERE oi.created_at BETWEEN :from AND :to
        GROUP BY po.product_id) AS oi_rank
        JOIN product AS p ON oi_rank.productId = p.id
        WHERE oi_rank.sellingRank <= 5
        """;

    return namedParameterJdbcTemplate.query(
        query,
        new MapSqlParameterSource("from", from)
            .addValue("to", to),
        (rs, rowNum) -> new ProductWithRank(
            rs.getLong("sellingRank"),
            rs.getLong("amount"),
            rs.getLong("productId"),
            rs.getString("productName"),
            rs.getString("productDescription"),
            rs.getLong("basePrice")
        ));
  }
}
