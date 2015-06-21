#!/usr/bin/env bash
sbt run org.fayalite.MainServer & 'cd app-dynamic; sbt ~fastOptJS;'