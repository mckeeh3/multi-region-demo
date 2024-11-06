#!/bin/bash

# Check if hostname is provided as an argument
if [ -z "$1" ]; then
  baseUrl="http://localhost:9000"
else
  baseUrl="https://$1"
fi

mvn gatling:test -Dgatling.simulationClass=io.akka.demo.gatling.SimpleUserSimulation -DbaseUrl="${baseUrl}"
