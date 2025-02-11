package br.com.impacta.bootcamp.provaconceito.repository;

import br.com.impacta.bootcamp.provaconceito.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author carlos
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
