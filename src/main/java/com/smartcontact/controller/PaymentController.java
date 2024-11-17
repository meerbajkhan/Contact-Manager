package com.smartcontact.controller;

import java.security.Principal;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.razorpay.*;
import com.smartcontact.entities.MyOrder;
import com.smartcontact.repository.MyOrderRepository;
import com.smartcontact.repository.UserRepository;

@Controller
public class PaymentController {

	// creating order for payment
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/user/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception {
//		System.out.println("Hey Order function executed");
		System.out.println(data);
		
		int amount = Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_PjTD22SiIdpggZ", "f6L9K86FOfsMvl2QzInv9O96");
		
		JSONObject jsonObject = new JSONObject();
//		amount = amount*100/100;
		System.out.println(amount);
		jsonObject.put("amount", amount*100);
		jsonObject.put("currency", "INR");
		jsonObject.put("receipt", "txn_24521");
		
		//creating new order
		
		Order order = razorpayClient.orders.create(jsonObject);
		System.out.println(order);
		
		//save the order in database
		
		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(amount+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserEmail(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepository.save(myOrder);
		
		//if you want you can save this to your data
		
		return order.toString();
	}
	
	@PostMapping("/user/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		
		MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myOrder);
		
		System.out.println("payment Data" + data);
		return ResponseEntity.ok(Map.of("msg","update"));
	}
}
