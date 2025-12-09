package com.base.auth.repository;

import com.base.auth.model.Category;
import com.base.auth.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    News findByTitle(String title);
    Boolean existsByTitle(String title);
    @Transactional
    @Modifying
    @Query(value = "delete FROM db_user_base_news where category_id = :categoryId", nativeQuery = true)
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);
}