require.config({
    waitSeconds: 30,
    wrap: { // include this file in the appropriate modules in prod mode
        startFile: "shim.js",
        endFile: "blank.js"
    },
    paths: {
        underscore: "vendor/underscore",
        angular: "vendor/angular",
        jquery: "vendor/jquery-1.10.2",
        "bs-affix": "vendor/bootstrap/bootstrap-affix",
        "bs-alert": "vendor/bootstrap/bootstrap-alert",
        "bs-button": "vendor/bootstrap/bootstrap-button",
        "bs-carousel": "vendor/bootstrap/bootstrap-carousel",
        "bs-collapse": "vendor/bootstrap/bootstrap-collapse",
        "bs-dropdown": "vendor/bootstrap/bootstrap-dropdown",
        "bs-modal": "vendor/bootstrap/bootstrap-modal",
        "bs-popover": "vendor/bootstrap/bootstrap-popover",
        "bs-scrollspy": "vendor/bootstrap/bootstrap-scrollspy",
        "bs-tab": "vendor/bootstrap/bootstrap-tab",
        "bs-tooltip": "vendor/bootstrap/bootstrap-tooltip",
        "bs-transition": "vendor/bootstrap/bootstrap-transition",
        "bs-typeahead": "vendor/bootstrap/bootstrap-typeahead"
    },
    shim: {
        angular: {
            deps: ['jquery'],
            exports: 'angular'
        },
        underscore: {
            exports: '_'
        },
        "bs-affix": ['jquery'],
        "bs-alert": ['jquery'],
        "bs-button": ['jquery'],
        "bs-carousel": ['jquery'],
        "bs-collapse": ['jquery'],
        "bs-dropdown": ['jquery'],
        "bs-modal": ['jquery'],
        "bs-popover": ['jquery'],
        "bs-scrollspy": ['jquery'],
        "bs-tab": ['jquery'],
        "bs-tooltip": ['jquery'],
        "bs-transition": ['jquery'],
        "bs-typeahead": ['jquery']
    }
});
