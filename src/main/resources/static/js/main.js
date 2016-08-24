	$(document)
			.ready(
					function() {
	
						var cat = "travel-api-client:psw";
						var credentials = btoa(cat);
						var token = null;
						var awaiting = false;
	
						var getByTerm = function(term) {
							if (awaiting)
								return;
							awaiting = true;
	
							$.ajax({
								url : "http://localhost:8080/airports?term=" + term,
	
								success : function(data, status) {
	
									return console.log("The returned data", data);
								},
	
								complete : function(data) {
									awaiting = false;
								},
								beforeSend : function(xhr, settings) {
									xhr.setRequestHeader('Authorization', 'Bearer '
											+ token);
								}
							});
						}
	
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
	
						/*$('#from').keyup(function() {
							getByTerm($(this).val())
	
							console.log("make request to api and parse json")
	
						});*/
						
						$('#select-from, #select-to').selectize({
						    valueField: 'code',
						    labelField: 'code',
						    searchField: 'name',
						    options: [],
						    create: false,
						    render: {
						        option: function(item, escape) {
						            var actors = [];
						            console.log('here!!!')
						            console.log(item)

						            return '<div>' + escape(item.name) + " (" +  escape(item.code) + ')</div>';
						        },
						        item: function(item, escape){
						            return '<div>'
						              + escape(item.name) + ' (' 
						              + escape(item.code) + ')'
						              + '</div>';
						          }
						    },
						    load: function(query, callback) {
						        if (!query.length) return callback();
						        $.ajax({
						            url: 'http://localhost:8080/airports',
						            type: 'GET',
						            
						            data: {
						                term: query
						            },
						            error: function() {
						                callback();
						            },
						            success: function(res) {
							            console.log('in success')
						                callback(res._embedded.locations);
						            },
									beforeSend : function(xhr, settings) {
										xhr.setRequestHeader('Authorization', 'Bearer '
												+ token);
									}
						        });
						    }
						});
	
						getToken();
	
					})