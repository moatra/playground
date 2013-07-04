Play2 Bootstrap
=====================================

This projects is a starting foundation for using the following stack of technologies together:

* [Vagrant](http://www.vagrantup.com/) (Virtual Machine management)
* [Ansible](http://www.ansibleworks.com/docs/) (Machine configuration management)
* [PostgreSQL](http://www.postgresql.org/) (Database)
* [Play2 - Scala](http://www.playframework.com/) (Web Framework)
* [Slick](http://slick.typesafe.com/) (Database query/access library)
    + [play-slick](https://github.com/freekh/play-slick) (Play Framework integration plugin for Slick)
* [RequireJS](http://requirejs.org/) (Modular JS development)
* [AngularJS](http://angularjs.org/) (Front-end JS framework)
* [Bootstrap](http://twitter.github.io/bootstrap/) (Front-end CSS framework)
    + [Font Awesome](http://fortawesome.github.io/Font-Awesome/) (Vector icon font, replaces and extends Bootstrap's
      Glyphicons)
* [Jasmine](http://pivotal.github.io/jasmine/) (Behavior-based JS unit tests)
    + [sbt-jasmine-plugin](https://github.com/guardian/sbt-jasmine-plugin) (Execute Jasmine unit tests as part of a SBT
      build)
* [JSLint](http://www.jslint.com/) (JS code quality enforcement)
    + [sbt-jslint](https://github.com/philcali/sbt-jslint) (Execute JSLint checks as part of a SBT build)


Using Play2 Bootstrap
------------------------
First things first, you'll need to install [Virtual Box](https://www.virtualbox.org/wiki/Downloads) and 
[Vagrant](http://downloads.vagrantup.com/).  Once those are finished, open a command prompt at the root of the Play2
Bootstrap repository and execute:

    vagrant up
    vagrant ssh
    cd /vagrant
    play debug run

The application will be available on your dev machine at localhost:8080.  Congratulations, you're ready to start 
programming.

### Debugging ###
Running `play debug run` will open a remote debugger on port 9999 of the host machine.

#### Note for Windows Host Machines ####
Vagrant's file syncing does not broadcast file events when syncing changes from the host machine.  This unfortunately 
means that the `play run` command will not notice the file changes and will not automagically update the running code.
You'll have to open a second ssh session into the guest machine (`vagrant ssh` from another console) and `touch` one
of the source files. (`touch /vagrant/app/views/main.scala.html` should work)

### First Steps ###
Before diving head first into writing your own web application, you'll want to rebrand it from `Play2 Bootstrap` to 
whatever you decide to name your project.  You'll want to look in the following files and update them appropriately:
* /ansible/group_vars/all
* /ansible/roles/dbserver/vars
* /ansible/roles/webserver/tasks
* /ansible/roles/webserver/templates/play2bootstrap.j2 (rename this file)
* /conf/application.conf
* /project/Build.Scala


Programming With Play2 Bootstrap
--------------------------------
(todo)

### Databaes Models ###
Putting your models into the `models.*` namespace, and the play-slick plugin will automatically generate your 
evolutions for you.

You may also wish to read up on how to [keep your tables and logic separated](https://github.com/freekh/play-slick/wiki/ScalaSlickTables).

### AngularJS and RequireJS Best Practice ###
It is somewhat unfortunate that AngularJS and RequireJS both implement their own dependency managment system.  I 
believe proper integration between the two is on the roadmap for future AngularJS revisions, but that still leaves the
conundrum of how to handle it now.

What I have found to be fairly effective is to have RequireJS modules create their own AngularJS module, and return the
name of the AngularJS module.  This eliminates the need for "magic names" that appear across RequireJS modules. For example:

    directive/integer.js:
    ---------------------

    define(['angular'], function (angular) {
        "use strict";
    
        var INTEGER_REGEXP = /^\-?\d*$/,
            moduleName = "directive-integer";
        angular.module(moduleName, []).directive('integer', function () {
        	// directive to cast an input's value to an integer
            return {
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.unshift(function (viewValue) {
                        if (INTEGER_REGEXP.test(viewValue)) {
                            // it is valid
                            ctrl.$setValidity('integer', true);
                            return parseInt(viewValue, 10);
                        }
                        // it is invalid, return undefined (no model update)
                        ctrl.$setValidity('integer', false);
                        return null;
                    });
                }
            };
        });
        return moduleName;
    });

    main.js:
    --------

    require(['angular', 'directive/integer'], function (angular, intDir) {
        "use strict";

        angular.module('myApp', [intDir]);
        angular.bootstrap(document, ['myApp']);
    });


Writing Unit Tests
--------------------
You should definitely write unit tests for both your Scala and Javascript code.

### Scala Unit Tests ###
([See Play's documentation](http://www.playframework.com/documentation/2.1.1/ScalaTest))

### JavaScript Unit Tests ###
To write JavaScript unit tests, add or modify a file in `/test/js/specs` that ends in `spec.js`.  These files will be
pulled in via RequireJS in the test runner and executed.  The test runner uses a combination of 
[Rhino](https://developer.mozilla.org/en-US/docs/Rhino), [Jasmine](http://pivotal.github.io/jasmine/), and 
[EnvJS](http://www.envjs.com/) to seamlessly execute your tests from the command line.

To debug these unit tests, you can execute `sbt jasmine-gen-runner` or `play jasmine-gen-runner` to generate a html
file you can load into a browser for debugging. Running `jasmine-gen-runner` will create a file that works on the
guest machine if you execute the command from the guest machine, and will work on the host machine if you execute the
command from the host machine.

#### Note for Windows Host Machines ####
Running `jasmine-gen-runner` from a Windows host machine will generate an html file that does not currently work. Feel
free to submit a pull request to my fork of the plugin on [GitHub](https://github.com/moatra/sbt-jasmine-plugin).

Production Environments
------------------------
To run in production mode, use `play start`.
(Alternatively: see Play's documentation on starting in 
[production mode](http://www.playframework.com/documentation/2.1.1/Production))

### Deployment ###
Using ansible for deployment (todo)

#### Sensitive Information (Passwords and Keys) ####
(todo)  

### Asset Minification ###
Starting the application in production mode will minify both CSS and JS components, as well as combining all the 
various RequireJS modules into a singule file.  `/app/views/main.scala.html` shows how to include the appropriate 
version by detecting if Play is in production mode or not.

Todo
-----------------
* Better nginx configuration (Enable gzip, SSL certificate examples, etc)
* Flesh out production machine deployment techniques
* Utilize Ansible templates to create `/conf/application.conf` with passwords and connection strings
* Better security configurations on production machines (SSH configuration, sudo'ers list, etc)
* `sbt-jasmine-plugin` should attempt to use the `require.js` that Play2 uses internally, instead of including it 
  manually
* Investigate performance impact of synchronous JDBC drivers and PostgreSQL vs 
  [ansyncrhonous drivers](http://reactivemongo.org/) and [MongoDB](http://www.mongodb.org/).

License
-----------------
Code provided here is licensed under the [MIT License](http://opensource.org/licenses/MIT)

All included libraries are redistributed in accordance with their licenses.