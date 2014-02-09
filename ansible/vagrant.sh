#!/bin/bash
if !(hash ansible-playbook 2>/dev/null); then 
	sudo apt-get update
	sudo apt-get install python-software-properties -y
	sudo add-apt-repository ppa:rquillo/ansible
	sudo apt-get update
	sudo apt-get install ansible -y
fi

if [ ! -d "/etc/ansible" ]; then 
	sudo mkdir /etc/ansible
fi

sudo cp /vagrant/ansible/vagrant /etc/ansible/hosts
sudo chmod -x /etc/ansible/hosts  # ansible will try to execute it if it's marked executable; vagrant's shared folders on windows won't persist this change, hence the copy
sudo ansible-playbook /vagrant/ansible/site.yml --connection=local
