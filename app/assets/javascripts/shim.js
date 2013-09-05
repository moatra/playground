require.config({
    waitSeconds: 30,
    wrap: { // include this file in the appropriate modules in prod mode
        startFile: "shim.js",
        endFile: "blank.js"
    },
    paths: {
        underscore: "lib/underscore",
        angular: "lib/angular",
        jquery: "lib/jquery-1.10.2",
        "bs-affix": "lib/bootstrap/bootstrap-affix",
        "bs-alert": "lib/bootstrap/bootstrap-alert",
        "bs-button": "lib/bootstrap/bootstrap-button",
        "bs-carousel": "lib/bootstrap/bootstrap-carousel",
        "bs-collapse": "lib/bootstrap/bootstrap-collapse",
        "bs-dropdown": "lib/bootstrap/bootstrap-dropdown",
        "bs-modal": "lib/bootstrap/bootstrap-modal",
        "bs-popover": "lib/bootstrap/bootstrap-popover",
        "bs-scrollspy": "lib/bootstrap/bootstrap-scrollspy",
        "bs-tab": "lib/bootstrap/bootstrap-tab",
        "bs-tooltip": "lib/bootstrap/bootstrap-tooltip",
        "bs-transition": "lib/bootstrap/bootstrap-transition",
        "bs-typeahead": "lib/bootstrap/bootstrap-typeahead"
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