package encore.server.domain.user.repository;

import encore.server.domain.user.entity.PreferredKeyword;
import encore.server.domain.user.enumerate.PreferredKeywordEnum;
import java.util.List;
import org.springframework.data.repository.Repository;

/*
  Read-Only Repository
 */
public interface PreferredKeywordRepository extends Repository<PreferredKeyword, Long> {

  List<PreferredKeyword> findAllByDeletedAtIsNullAndPreferredKeywordEnumIn(List<PreferredKeywordEnum> preferredKeywordEnums);
}
