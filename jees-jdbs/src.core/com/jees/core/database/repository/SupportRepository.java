package com.jees.core.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 配合SuperEntity可以实现Jpa全局操作，但由于感觉整体结构不理想，在网上翻看了很多实现方式，都没有达到我想要的效果。
 * 所以仅放在测试内容下，仅供参考。
 */
public interface SupportRepository extends JpaRepository<SuperEntity, Object> {
}