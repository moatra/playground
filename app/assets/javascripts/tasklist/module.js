define(['angular', 'moment', 'underscore'], function(angular, moment, _) {
    "use strict";

    var moduleName = "module-tasklist",
        module = angular.module(moduleName, []);

    module.controller("taskList", ['$scope', '$http', function ($scope, $http) {
        var blank;

        $http.get("/api/task").then(function (response) {
            $scope.tasks = response.data;
        });
        $http.get("/api/task/_new").then(function (response) {
            blank = response.data;
            $scope.newTask = _.clone(blank);
        });

        $scope.create = function () {
            $http.post("/api/task/_new", $scope.newTask).then(function (response) {
                $scope.tasks.items.push(response.data);
            });
            $scope.newTask = _.clone(blank);
        };

        $scope.save = _.debounce(function (task) {
            $http.post("/api/task/" + task.id, task);
        }, 150);

        $scope.delete = function (task) {
            $http.delete("/api/task/" + task.id);
            $scope.tasks.items = _.without($scope.tasks.items, task);
        };
    }]);

    module.filter("timestamp", function () {
        return function(input, format) {
            return moment.unix(input/1000).format(format);
        };
    });

    return moduleName;
});
