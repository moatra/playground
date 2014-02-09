PlayGround
=====================================

This projects is a starting foundation for developing, testing, and deploying an application using the following stack of technologies together:

* [Vagrant](http://www.vagrantup.com/) (Virtual Machine management)
* [Ansible](http://www.ansibleworks.com/docs/) (Machine configuration management)
* [PostgreSQL](http://www.postgresql.org/) (Database)
* [Play2 - Scala](http://www.playframework.com/) (Web Framework)
* [Slick](http://slick.typesafe.com/) (Database query/access library)
    + [play-slick](https://github.com/freekh/play-slick) (Play Framework integration plugin for Slick)
* [Grunt](http://gruntjs.com/) (Javascript task runner)  Assists the Play framework with the following:
    + Javascript linting
    + Less compilation and minification
    + Javascript compilation and minification
    + Static image compression
    + Live reloading
    + Javascript test running
* [RequireJS](http://requirejs.org/) (Modular JS development)
    + [AlmondJS](https://github.com/jrburke/almond) (Shim loader for minifed requirejs files)
* [AngularJS](http://angularjs.org/) (Front-end JS framework)
* [Bootstrap](http://twitter.github.io/bootstrap/) (Front-end CSS framework)
    + [Font Awesome](http://fortawesome.github.io/Font-Awesome/) (Vector icon font, replaces and extends Bootstrap's
      Glyphicons)
* [Mocha](http://visionmedia.github.io/mocha/) (Javascript unit framework)
    + [ChaiJS](http://chaijs.com/) (Assertion library)
    + [Karma](http://karma-runner.github.io/) (Unit test runner)
    + [PhantomJS](http://phantomjs.org/) (Headless javascript test environment)


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


Programming With PlayGround
--------------------------------
(todo)

### Git Branching ###
Refer to this post on [Git Branching strategy](http://nvie.com/posts/a-successful-git-branching-model/)

### Database Models ###
You may wish to read up on how to [keep your tables and logic separated](https://github.com/freekh/play-slick/wiki/ScalaSlickTables).
Ideally, your logic for querying the database should be separated from how you use the results.  Keeping all such 
querying code in one location simplifies any refactoring process, and is generally more maintainable.

###LESS is More###
While Play2 can handle LESS and JavaScript compilation, Grunt is much more flexible and capable in its range of abilities.
Simple plugins at project/GruntWatch.scala and porject/GruntTask.scala allow easy integration with sbt.  This means
executing `play run` will still live reload your less and javascript changes, and `play test` or `play stage` will still
invoke Grunt as needed.

###RequireJS and Minification###
The PlayGround repo offers one opinion on how to set up your javascript files so that the following criteria are all
met:

* A single "path.js" file is used for RequireJS path and shim configuration across Dev, Test, and Production
* In dev (ie, non-production mode), the application serves the original, un-minified javascript and css files.
* In production, the application serves a single concatenated, minified, and gzipped javascript file that has
  the RequireJS functionality built in (ie, no need to use RequireJS to load your script in production)
* In test, the normal paths and shim configs are usable without launching the entire javascript application

### AngularJS and RequireJS Best Practice ###
It is somewhat unfortunate that AngularJS and RequireJS both implement their own dependency management system.  I
believe proper integration between the two is on the roadmap for future AngularJS revisions, but that still leaves the
conundrum of how to handle it now.

What I have found to be fairly effective is to have RequireJS modules create their own AngularJS module, and return the
name of the AngularJS module.  This eliminates the need for "magic names" that appear across RequireJS modules.

### Application Configuration ###
In a normal Play2! application, the configuration is stored in `conf/application.conf`.  However, we use ansible to 
create this file from a template.  _Do not_ modify the application.conf directly, as any changes you make will be lost.
The correct way to alter the configuration is to update the template in `ansible/templates/application.conf.j2`, then
reprovision vagrant (`vagrant provision`) from your host machine or execute `ansible-playbook ansible/site.yml -i 
ansible/vagrant --tags "configuration"` from the application root on the Vagrant VM.

###Package.json###
Since npm and grunt are used only for frontend tasks, package.json has an unimportant name and version number.  Additionally,
it lists "download" as a dependency.  Download is actually a dependency of another one of the required packages, but it
was nested so deep it was causing file name length issues with Windows hosts.  Listing it as a top level dependency fixed
the problem.

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
[production mode](http://www.playframework.com/documentation/2.2.x/Production))

### Building and Deployment ###
Using ansible for deployment.  The `app` ansible role will install your app as a service in /etc/init.d.  It will
also reuse the /opt/{{app-name}} directory across versions, so make sure any files you want to persist across version
deploys are stored elsewhere.  (todo)

Why don't we have the actual production or staging inventory files checked into source control?

* The contents of these files could change independent of the functionality of your code,
  and tying these to source control will unnecessarily clutter your commit history

Okay, but why is the `build` inventory file checked in?  Couldn't I change the bots being used?

* Yes, but `build` will run directly on the build bot, using `ansible_connection=local`.  It doesn't need to know what 
  machine(s) you're using for building, as you are _already_ using them (This does imply a requirement that Ansible 
  is a requirement on each build slave).

####Example Jenkins build and deployment process#####
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
* Investigate Datomic/Datomisca for data modeling purposes
* Investigate a smoother Jenkins master/slave and Ansible integration, so that Ansible is not a requirement on each
  build slave.
* Try to port the javascript router to the static compilation
* Round robin deploy with no downtime

License
-----------------
Code provided here is licensed under the [MIT License](http://opensource.org/licenses/MIT)

All included libraries are redistributed in accordance with their licenses.
