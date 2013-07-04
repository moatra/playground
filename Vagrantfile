# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "precise64"

  config.vm.box_url = "http://files.vagrantup.com/precise64.box"

  config.vm.network :private_network, ip: "192.168.33.10"
  config.vm.network :forwarded_port, guest: 80, host: 8080
  config.vm.network :forwarded_port, guest: 9999, host: 9999
  config.vm.network :forwarded_port, guest: 9000, host: 9000

 config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--memory", 2048]
  end

  # why shell and not vagrant's ansible provider?
  # ansible doesn't install to a Windows host, so instead we run it locally on the machine
  config.vm.provision :shell,
    :path => "ansible/vagrant.sh"

end
