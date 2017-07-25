'use strict';

(function() {

	var MODULE = angular.module('biz.aQute.openapi.oauth2.example', [
			'ngRoute', 'ngResource' ]);

	MODULE
			.config(function($routeProvider) {
				$routeProvider
						.when(
								'/',
								{
									controller : mainProvider,
									templateUrl : '/biz.aQute.openapi.oauth2.example/main/htm/home.htm'
								});
				$routeProvider
						.when(
								'/about',
								{
									templateUrl : '/biz.aQute.openapi.oauth2.example/main/htm/about.htm'
								});
				$routeProvider.otherwise('/');
			});

	MODULE.run(function($rootScope, $location) {
		$rootScope.alerts = [];
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
	});

	var mainProvider = function($scope, $http, $window) {

		$scope.loggedin = "not logged in"

		$window.addEventListener('message', function(event) {
			console.log(event.data)
			$scope.alerts.push({
				type : 'warning',
				msg : event.data
			})
			var found = event.data.match(/\?error=([^&]+)&error_/i);
			var error = found[1];
			$scope.loggedin = error == 'ok' ? "" : error
			$scope.$apply();
		})

		$scope.authenticated = function(name) {
			if (name) {
				$http.get('/openapi/security/google/authenticated/' + name)
						.then(
								function(d) {
									$scope.alerts.push({
										type : 'success',
										msg : d.data
									});
								},
								function(d) {
									$scope.alerts.push({
										type : 'danger',
										msg : 'Failed with [' + d.status + '] '
												+ d.statusText
									});
								});
			}
		};
		$scope.unauthenticated = function(name) {
			if (name) {
				$http.get('/openapi/security/google/unauthenticated/' + name)
						.then(
								function(d) {
									$scope.alerts.push({
										type : 'success',
										msg : d.data
									});
								},
								function(d) {
									$scope.alerts.push({
										type : 'danger',
										msg : 'Failed with [' + d.status + '] '
												+ d.statusText
									});
								});
			}
		};

	}

})();
