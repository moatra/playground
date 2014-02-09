define(['angular', 'tasklist/module', 'data/module'], function (angular, tasklist, data) {
    "use strict";

    angular.element(document).ready(function () {
        angular.bootstrap(document.documentElement, [tasklist, data]);
    });
});
