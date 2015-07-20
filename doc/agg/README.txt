cd ansible
ansible-playbook deploy-master.yml -i ./<inventory-name> --private-key=<path-to-ssh-private-key>
ansible-playbook deploy-worker.yml -i ./<inventory-name> --private-key=<path-to-ssh-private-key>

cd ansible
ansible-playbook deploy-master.yml -i ./boards --private-key=$FP
ansible-playbook deploy-worker.yml -i ./boards --private-key=$FP

