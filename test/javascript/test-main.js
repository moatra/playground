var tests = [];
for (var file in window.__karma__.files) {
    if (window.__karma__.files.hasOwnProperty(file)) {
        if (/javascript\/specs\/.*.js$/.test(file)) {
            tests.push(file);
        }
    }
}

requirejs.config({
    deps: tests,
    callback: window.__karma__.start,
    baseUrl: "/base/app/assets/javascripts"
});


