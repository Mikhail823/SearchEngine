package org.spring.repo;

import org.spring.model.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends CrudRepository<Page, Long> {
    Page findByPath(String path);
    Optional<Page> findByIdAndSiteId(int pageId, int siteId);
    @Query(value = "SELECT count(*) from Page where site_id = :id")
    long count(@Param("id") long id);
}
