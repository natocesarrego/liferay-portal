#!/bin/bash

# Get list of blockers from Jira

BLOCKERS=(LPD-15716 LPD-6960)

# Check if all blockers were indeed merged

for BLOCKER in ${BLOCKERS[@]}; do
    echo "BLOCKER = "${BLOCKER}
    git log --grep=${BLOCKER}

    GIT_LOG_GREP_RESULT=$(git log --grep=${BLOCKER}" ")

    if [ -z "$GIT_LOG_GREP_RESULT" ] ; then
        echo "Blocker ${BLOCKER} not merged yet"

        exit
    fi
done

# Notify Release Team, ideally, through a specific Slack channel

echo "All blockers merged. Notifying Release Team."