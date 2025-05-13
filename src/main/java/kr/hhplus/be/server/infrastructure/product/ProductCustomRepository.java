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


  public List<ProductIdWithRank> findAllSellingProductsWithRank(
      LocalDateTime from, LocalDateTime to) {
    String query = """
        WITH ranked_products AS (
            SELECT po.product_id AS productId,
                SUM(oi.amount) AS totalSales,
                ROW_NUMBER() OVER (ORDER BY SUM(oi.amount) DESC) AS sellingRank
            FROM order_item AS oi
            JOIN product_option AS po ON oi.product_option_id = po.id
            WHERE oi.created_at BETWEEN :from AND :to
            GROUP BY po.product_id
        )
        SELECT ranked_products.productId,
               ranked_products.totalSales,
               ranked_products.sellingRank
        FROM ranked_products
        ORDER BY ranked_products.sellingRank
        """;

    return namedParameterJdbcTemplate.query(
        query,
        new MapSqlParameterSource("from", from)
            .addValue("to", to),
        (rs, rowNum) -> new ProductIdWithRank(
            rs.getLong("sellingRank"),
            rs.getLong("productId"),
            rs.getLong("totalSales")
        ));
  }

  public List<ProductIdWithRank> findTop5SellingProductsFromRankView(
      LocalDateTime from, LocalDateTime to) {
    String query = """
        SELECT psrv.product_id AS productId,
                ROW_NUMBER() OVER (ORDER BY SUM(psrv.total_sales) DESC) AS sellingRank,
                SUM(psrv.total_sales) AS totalSales
        FROM product_selling_rank_view AS psrv
        WHERE psrv.`from` BETWEEN :from AND :to
        GROUP BY psrv.product_id
        LIMIT 5
        """;

    return namedParameterJdbcTemplate.query(
        query,
        new MapSqlParameterSource("from", from)
            .addValue("to", to),
        (rs, rowNum) -> new ProductIdWithRank(
            rs.getLong("sellingRank"),
            rs.getLong("productId"),
            rs.getLong("totalSales")
        ));
  }
}
