module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON("package.json"),
        jshint: {
            files: ["gruntfile.js", "app/assets/javascripts/**/*.js", "test/assets/javascripts/**/*.js", ],
            options: {
                jshintrc: true,
                ignores: ["**/vendor/**"]
            }
        },
        less: {
            options: {
                paths: ["app/assets/stylesheets"]
            },
            debug: {
                src: "app/assets/stylesheets/style.less",
                dest: "public/stylesheets/style.css"
            },
            compile: {
                options: {
                    compress: true,
                    cleancss: true
                },
                src: "app/assets/stylesheets/style.less",
                dest: "public/stylesheets/style.min.css"
            }
        },
        requirejs: {
            options: {
                baseUrl: "app/assets/javascripts/",
                mainConfigFile: "app/assets/javascripts/paths.js",
                dir: "public/javascripts/"
            },
            debug: {
                options: {
                    optimize: "none",
                    modules: [
                        {
                            create: true,
                            name: "main",
                            include: ["paths"],
                            insertRequire: ["app"]
                        }
                    ]
                }
            },
            compile: {
                options: {
                    optimize: "uglify2",
                    modules: [
                        {
                            create: true,
                            name: "main.min",
                            include: ["vendor/almond", "paths", "app"],
                            insertRequire: ["app"]
                        }
                    ]
                }
            }
        },
        compress: {
            compile: {
                options: {
                    mode: "gzip"
                },
                expand: true,
                cwd: "public/",
                src: ["**/*", "!images/**/*", "!**/*.gz"],
                dest: "public/"
            }
        },
        imagemin: {
            minify: {
                expand: true,
                cwd: "app/assets/images/",
                src: "**/*.{png,jpg,gif}",
                dest: "public/images/"
            }
        },
        copy: {
            otherimages: {
                expand: true,
                cwd: "app/assets/images/",
                src: ["**/*", "!**/*.{png,jpg,gif}"],
                dest: "public/images"
            }
        },
        watch: {
            options: {
                atBegin: true
            },
            js: {
                files: ["app/assets/stylesheets/**/*"],
                tasks: ["less:debug"]
            },
            less: {
                files: ["app/assets/javascripts/**/*"],
                tasks: ["requirejs:debug"]
            },
            minifyimages: {
                files: ["app/assets/images/**/*.{png,jpg,gif}"],
                tasks: ["imagemin"]
            },
            copynonimages: {
                files: ["app/assets/images/**/*", "!app/assets/images/**/*.{png,jpg,gif}"],
                tasks: ["copy"]
            }
        },
        karma: {
            unit: {
                options: {
                    basePath: '',
                    frameworks: ['mocha', 'requirejs', 'chai'],
                    files: [
                        'app/assets/javascripts/paths.js',
                        'test/javascript/test-main.js',
                        {pattern: 'app/assets/javascripts/**/*.js', included: false},
                        {pattern: 'test/javascript/specs/**/*.js', included: false}
                    ],
                    reporters: ['progress'],
                    port: 9876,
                    colors: true,
                    logLevel: 'INFO',
                    autoWatch: false,
                    browsers: ['PhantomJS'],
                    captureTimeout: 60000,
                    singleRun: true
                }
            }
        }
    });

    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.loadNpmTasks("grunt-contrib-less");
    grunt.loadNpmTasks("grunt-contrib-jshint");
    grunt.loadNpmTasks("grunt-contrib-requirejs");
    grunt.loadNpmTasks("grunt-contrib-compress");
    grunt.loadNpmTasks("grunt-contrib-imagemin");
    grunt.loadNpmTasks("grunt-contrib-copy");
    grunt.loadNpmTasks("grunt-karma");

    // Default task(s).
    grunt.registerTask("default", ["jshint", "karma"]);
    grunt.registerTask("test", ["jshint", "karma"]);
    grunt.registerTask("images", ["imagemin", "copy"]);
    grunt.registerTask("debug", ["less:debug", "requirejs:debug", "imagemin", "copy"]);
    grunt.registerTask("compile", ["less:compile", "requirejs:compile", "compress", "imagemin", "copy"]);

};
