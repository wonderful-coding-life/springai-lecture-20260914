package com.example.demo.repository;

import com.example.demo.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByMemberName(String memberName);
    ProductOrder findByMemberNameAndOrderNumber(String memberName, String orderNumber);
}
