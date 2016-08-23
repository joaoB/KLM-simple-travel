$(document)
		.ready(
				function() {

					var cat = "travel-api-client:psw";
					var credentials = btoa(cat);
					var token = null;
					var awaiting = false;
					getToken();

					$('.from').keyup(function() {
						if (awaiting)
							return;
						
						console.log("make request to api and parse json")
						
					});

					var getToken = function() {

						$
								.ajax({
									url : "/oauth/token",
									type : "post",
									data : {
										grant_type : "client_credentials"
									},
									headers : {
										"Authorization" : "Basic "
												+ credentials,
										"Content-Type" : "application/x-www-form-urlencoded;charset=UTF-8"
									},
									success : function(data, status) {
										token = data.access_token;
										return console.log("The returned data",
												data);
									}
								});
					}

				})