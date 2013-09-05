PlayGround
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


Using PlayGround
------------------------
First things first, you'll need to install [Virtual Box](https://www.virtualbox.org/wiki/Downloads) and 
[Vagrant](http://downloads.vagrantup.com/).  Once those are finished, open a command prompt at the root of the PlayGround
repository and execute:

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
Before diving head first into writing your own web application, you'll want to rebrand it from `PlayGround` to 
whatever you decide to name your project.  You'll want to look in the following files and update them appropriately:
* /ansible/group_vars/all
* /project/Build.Scala


Programming With PlayGround
--------------------------------
(todo)

### Git Branching ###
Refer to this post on [Git Branching strategy](http://nvie.com/posts/a-successful-git-branching-model/)

### Databaes Models ###
Putting your models into the `models.*` namespace, and the play-slick plugin will automatically generate your 
evolutions for you.

You may also wish to read up on how to [keep your tables and logic separated](https://github.com/freekh/play-slick/wiki/ScalaSlickTables).
Ideally, your logic for querying the database should be separated from how you use the results.  Keeping all such 
querying code in one location simplifies any refactoring process, and is generally more maintainable.

####Package.scala####
In `models` package object, we create `vals` for each table's class definition.  This is because of Slick's 
documentation at http://slick.typesafe.com/doc/1.0.1/lifted-embedding.html#tables

Since we're using `val`s with a separate class definition, there's an almost circular dependency in methods on the
class definition that are using the `val` for accessing the table.  Normally this isn't a problem, but when developing you 
should keep the following in mind:

* When creating a new table, make sure to create and use the `val` in the package object. 
* All references to the table's `val` inside of the table's class definition must be done inside of a `def` or 
  `lazy val`. Otherwise, you're referencing the table's `val` before it has been initialized.

####Traits.scala####
`traits.scala` provides a barebones mixin called `IdCrud[T]` for providing basic CRUD capabilities to a model, where 
`T` is the type of the `case class` the table represents. Unfortunately, since PostgreSQL doesn't allow `null` when 
inserting primary keys, we can't use the normal `*` projection for inserting new rows and getting the created `id`.
As such, you'll need to implement both `*` and `insert()` when using `IdCrud`.  See `models/user.scala` for an example.

###LESS is More###
Play2 automatically handles recompiling your LESS files to css in dev mode when changes are detected.  No extra hassle 
on your end required.

###RequireJS and Minification###
The PlayGround repo offers one opinion on how to set up your javascript files so that the following criteria are all
met:

* A single "shim.js" file is used for RequireJS configuration across Dev, Test, and Production
* In dev (ie, non-production mode), the application serves the original, un-minified javascript and css files.
* In production, the application serves a single concatenated, minified, and gzipped javascript file.
* In test, the normal paths and shim configs are usable without launching the entire javascript application

####Shim.js####

#####waitSeconds#####
In the provided `shim.js`, `waitSeconds` is set because the default value is too short for the Rhino environment to 
load and parse jQuery, resulting in a timeout error.  Unfortunately, due to a current bug in the Play2 build process,
this error will not fail a build ( https://github.com/playframework/playframework/issues/1233 ).  If for some reason 
your production environment appears to be serving unminified javascript, this error probably occurred and Play2 then
defaulted back to the unminified files.

#####wrap#####
We use `wrap`'s `startFile` to include `shim.js` in the minified production version.  However, `r.js` will complain if 
`startFile` is set without a matching `endFile`.  Unfortunately, we can't use startFile with `end: ""`, hence the 
existence of `blank.js`.

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
        angular.boottrap(document, ['myApp']);
    });


### Application Configuration ###
In a normal Play2! application, the configuration is stored in `./conf/application.conf`.  However, we use ansible to 
create this file from a template.  _Do not_ modify the application.conf directly, as any changes you make will be lost.
The correct way to alter the configuration is to update the template in `./ansible/templates/application.conf/j2`, then
reprovision vagrant (`vagrant provision`) from your host machine or execute `ansible-playbook ansible/site.yml -i 
ansible/vagrant --tags "configuration"` from the application root on the Vagrant VM.


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
[production mode](http://www.playframework.com/documentation/2.1.x/Production))

### Building and Deployment ###
Using ansible for deployment (todo)

Why don't we have the actual production or staging inventory files checked into source control?

* The contents of these files could change independent of the functionality of your code,
  and tying these to source control will unnecessarily clutter your commit history

Okay, but why is the `build` inventory file checked in?  Couldn't I change the bots being used?

* Yes, but `build` will run directly on the build bot, using `ansible_connection=local`.  It doesn't need to know what 
  machine(s) you're using for building, as you are _already_ using them (This does imply a requirement that Ansible 
  is a requirement on each build slave).

####Example Jenkins deployment#####
Step 1: Update version number

    sed -i "s/-SNAPSHOT/.$BUILD_NUMBER/g" ansible/group_vars/all

Step 2: Ensure the build environment is correct.

    ansible-playbook ansible/site.yml -i ansible/buildbot

Step 3: Test and package the application (Jenkins' text logging doesn't like the color format codes, so we disable it)

    play -Dsbt.log.noformat=true clean compile test dist

Step 4: Create the inventory file

    ??? (This step depends on how you are managing your servers)

Step 5: Deploy the application
    
    ansible-playmbook ansible/site.yml -i /path/to/inventory --extra-vars="app_secret=MyActualSecret db_pass=TheActualPassword"


#### Sensitive Information (Passwords and Keys) ####
The above deployment scenario means your secrets are available on the build machine, but are not included in source 
control.  If you wish to use a different method for managing secrets, you can use any of Ansible's mechanics for 
[separating out variables](http://www.ansibleworks.com/docs/playbooks2.html#variable-file-separation).

Todo
-----------------
* Investigate SSL best practices for nginx and ansible deployment
* Better security configurations on production machines (SSH configuration, sudo'ers list, etc)
* `sbt-jasmine-plugin` should attempt to use the `require.js` that Play2 uses internally, instead of including it 
  manually
* Investigate performance impact of synchronous JDBC drivers and PostgreSQL vs 
  [ansyncrhonous drivers](http://reactivemongo.org/) and [MongoDB](http://www.mongodb.org/).
* Investigate [LiquiBase](http://www.liquibase.org/) for managing database evolutions, instead of relying on
  applyEvoloutions.default=true in the init.d script for evolutions
* Write up how to break javascript into multiple RequireJS modules
* Split types for models to SavedInstance (with `id: Long` field) and NewInstance (with no `id` field)
* Investigate a smoother Jenkins master/slave and Ansible integration, so that Ansible is not a requirement on each
  build slave.

License
-----------------
Code provided here is licensed under the [MIT License](http://opensource.org/licenses/MIT)

All included libraries are redistributed in accordance with their licenses.