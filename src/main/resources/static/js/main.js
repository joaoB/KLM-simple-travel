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
					
					$('#from').selectize({
					    valueField: 'title',
					    labelField: 'title',
					    searchField: 'title',
					    options: [],
					    create: false,
					    render: {
					        option: function(item, escape) {
					            var actors = [];
					            for (var i = 0, n = item.abridged_cast.length; i < n; i++) {
					                actors.push('<span>' + escape(item.abridged_cast[i].name) + '</span>');
					            }

					            return '<div>' +
					                '<img src="' + escape(item.posters.thumbnail) + '" alt="">' +
					                '<span class="title">' +
					                    '<span class="name">' + escape(item.title) + '</span>' +
					                '</span>' +
					                '<span class="description">' + escape(item.synopsis || 'No synopsis available at this time.') + '</span>' +
					                '<span class="actors">' + (actors.length ? 'Starring ' + actors.join(', ') : 'Actors unavailable') + '</span>' +
					            '</div>';
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
					                callback(res.movies);
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