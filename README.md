This project is a VES agent for the SEBA project.

This reads messages off Kafka, and post them to the VES, which is the event REST API of ONAP's DCAE.

To build the project, run build.sh

To push the docker images to a repository, see the push_to_local.sh and push_to_foundry.sh scripts

To run the project on a kubernetes cluster, run kubectl apply -f ves-agent.yaml
