#! /bin/bash

set -e

ssh -t $1 "export GIT_SSH=~/schema-summaries/scripts/git+ssh.sh && cd ~/schema-summaries && git checkout master && git pull && scripts/test-summarization-pipeline.sh"

