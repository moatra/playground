require.config({
    paths: {
        underscore: "lib/underscore",
        angular: "lib/angular",
        jquery: "lib/jquery-1.10.2",
        "bs-affix": "lib/bootstrap-affix",
        "bs-alert": "lib/bootstrap-alert",
        "bs-button": "lib/bootstrap-button",
        "bs-carousel": "lib/bootstrap-carousel",
        "bs-collapse": "lib/bootstrap-collapse",
        "bs-dropdown": "lib/bootstrap-dropdown",
        "bs-modal": "lib/bootstrap-modal",
        "bs-popover": "lib/bootstrap-popover",
        "bs-scrollspy": "lib/bootstrap-scrollspy",
        "bs-tab": "lib/bootstrap-tab",
        "bs-tooltip": "lib/bootstrap-tooltip",
        "bs-transition": "lib/bootstrap-transition",
        "bs-typeahead": "lib/bootstrap-typeahead"
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
