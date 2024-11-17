console.log("This is javascript")

const toggleSidebar = () => {

	if ($(".sidebar").is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {

		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}

};

/*Search data*/

const search = () => {

	/*console.log("Searching...");*/

	let query = $("#search-input").val();


	if (query == '') {
		$(".search-result").hide();
	} else {
		/*console.log(query);*/

		//sending request to server

		let url = `http://localhost:8181/search/${query}`;

		fetch(url).then((response) => {

			return response.json();
		})
			.then((data) => {

				/*console.log(data);*/

				let text = `<div class='list-group'>`;

				data.forEach(contactDetails => {

					text += `<a href='/user/contactDetalis/${contactDetails.contactId}' class='list-group-item list-group-item-action'> ${contactDetails.contactName}</a>`
				})

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			});

	}
}

//first request to server to create order

const paymentStart = () => {

	console.log("payment Start");

	let amount = $("#payment_field").val();
	console.log(amount);

	if (amount == '' || amount == null) {
		/*alert("Amount is Required !!");*/
		swal("Failed!!", " Amount is Required !!", "error");
		return;
	}

	// we will use ajax to send request to server to create order -jquery

	$.ajax(

		{
			url: '/user/create_order',
			data: JSON.stringify({ amount: amount, info: 'order_request' }),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success: function(response) {
				//invoked when success
				console.log(response)
				if (response.status == "created") {
					//open payment form
					let options = {
						key: 'rzp_test_PjTD22SiIdpggZ',
						amount: response.amount,
						currency: 'INR',
						name: 'Smart Contact Manager',
						description: 'Donation',
						order_id: response.id,
						handler: function(response) {
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log('Payment Successful!!')
							/*alert('congrates !! Payment Successful!!')*/

							updatePaymentOnServer(response.razorpay_payment_id, 
										response.razorpay_order_id, 
										"paid");

							/*swal("Good job!", "congrates !! Payment Successful!!", "success");*/
						},
						prefill: {
							name: "",
							email: "",
							contact: "",
						},
						notes: {
							address: "Payment Integration Testing",
						},
						theme: {
							color: "#3399cc",
						}
					};

					let razorpay = new Razorpay(options);

					razorpay.on('payment.failed', function(response) {
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						/*alert('Oops Payment failes!!')*/
						swal("Failed!!", "Oops Payment failes!!", "error");
					});

					razorpay.open();
				}
			},
			error: function(error) {
				//invoked when error
				console.log(error)
				alert("Something went wrong !!")
			}
		}
	)
};

function updatePaymentOnServer(payment_id, order_id, status)
 {

	$.ajax({

		url: '/user/update_order',
		data: JSON.stringify({
			payment_id: payment_id,
			order_id: order_id,
			status: status,
		}),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function(response) {
			swal("Good job!", "congrates !! Payment Successful!!", "success");
		},
		error: function(error) {
			swal("Failed!!", 
			"Your payment is successful , but we did not get on server , we will contact you as soon as possible",
			 "error");
		}
	});
}


