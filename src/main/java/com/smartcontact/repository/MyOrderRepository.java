package com.smartcontact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcontact.entities.MyOrder;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {

	public MyOrder findByOrderId(String orderId);
}
