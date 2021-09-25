#!/usr/bin/env bash

ssh jenkins@104.40.226.227  '
 sudo microk8s.kubectl  rollout restart deployment/greenpole-stockbroker -n greenpole
'