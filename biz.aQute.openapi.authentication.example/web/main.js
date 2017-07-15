'use strict';

(function() {

	var MODULE = angular.module('biz.aQute.openapi.authentication.example',
			[ 'ngRoute', 'ngResource' ]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/biz.aQute.openapi.authentication.example/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/biz.aQute.openapi.authentication.example/main/htm/about.htm'});
		$routeProvider.otherwise('/');
	});

	MODULE.run( function($rootScope, $location) {
		$rootScope.alerts = [];
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
	});



	var mainProvider = function($rootScope, $scope, $http, $window) {
		$window.addEventListener( 'message', function(event) {
			console.log(event.data)
			$rootScope.alerts.push( { type: 'warn', msg: event.data } )
			$rootScope.$apply();
			$scope.getInfo()
		})
		$scope.message = "-"

		$scope.getInfo = function() {
			$http.get("/.openapi/security")
			.then( function(r) {
				$scope.info = angular.fromJson(r.data)
			}, function(r) {
		        $rootScope.alerts.push( {msg: "basic: " + r.statusText, type:"error" });
		    })
		}

		$scope.basic = function() {
			$http.get("/authentication/basic")
				.then( function(r) {
					$scope.message = r.data
				}, function(r) {
			        $rootScope.alerts.push( {msg: "basic: " + r.statusText, type:"error" });
			    })
		}
		$scope.getInfo()

	}

})();
