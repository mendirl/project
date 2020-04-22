#!/usr/bin/env bash

ACTION=$1

case "$ACTION" in

install)
  docker volume create odfe-master
  docker volume create odfe-data1
  docker volume create odfe-data2
  docker volume create odfe-single
  docker volume create odfe-coordinating

  docker network create mendirl
  docker network create odfe-network-cluster
  docker network create odfe-network-single
  exit $?;;

remove)
  docker volume rm odfe-master
  docker volume rm odfe-data1
  docker volume rm odfe-data2
  docker volume rm odfe-single
  docker volume rm odfe-coordinating

  docker network rm mendirl
  docker network rm odfe-network-cluster
  docker network rm odfe-network-single
  exit $?;;

prune)
  docker system prune
  exit $?;;

single-start)
  docker-compose --compatibility -f monitoring/docker-compose-odfe-single.yml up -d
  exit $?;;

single-stop)
  docker-compose --compatibility -f monitoring/docker-compose-odfe-single.yml down
  exit $?;;

single-config)
  docker-compose --compatibility -f monitoring/docker-compose-odfe-single.yml config
  exit $?;;

cluster-start)
  docker-compose --compatibility -f monitoring/docker-compose-odfe-cluster.yml up -d
  exit $?;;

cluster-stop)
  docker-compose --compatibility -f monitoring/docker-compose-odfe-cluster.yml down
  exit $?;;

cluster-config)
  docker-compose --compatibility -f monitoring/docker-compose-odfe-cluster.yml config
  exit $?;;

*)
  echo "Usage: $0 {install|remove|prune|single-start|single-stop|single-config|cluster-start|cluster-stop|cluster-config}"; exit 1;
esac


exit 0
