define(['angular', 'underscore'], function (angular, _) {
    "use strict";

    var moduleName = "module-data",
        module = angular.module(moduleName, []);

    module.controller("dataBrowser", ['$scope', '$http', function ($scope, $http) {
        $scope.lastPage = 0;
        $scope.firstItem = 0;
        $scope.lastItem = 0;
        $scope.getPage = function (page) {
            $scope.busy = true;

            $http.get("/api/task", {
                params: {
                    page: Math.max(0, Math.min($scope.lastPage, page || 0))
                }
            }).then(function (response) {
                $scope.tasks = response.data;
                $scope.lastPage = Math.max(0, Math.ceil(($scope.tasks.total / $scope.tasks.size) - 1));
                $scope.firstItem = Math.min($scope.tasks.page * $scope.tasks.size + 1, $scope.tasks.total);
                $scope.lastItem = Math.min($scope.tasks.total, ($scope.tasks.page + 1) * $scope.tasks.size);

            }).finally(function () {
                $scope.busy = false;
            });
        };
        $scope.getPage(0);
    }]);

    return moduleName;
});
