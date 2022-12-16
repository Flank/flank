# Contribution process
This document describes contribution details for full-time contributors.

### Daily community monitoring
* Check if there are any new critical issues in [reported issues](https://github.com/Flank/flank/issues).
* Check if there are any new issues on the [flank slack channel](https://firebase-community.slack.com/archives/C72V6UW8M).
This is good practice to check out those points once a day by someone from the team.

## Looking for a new task
1. Perform daily monitoring if needed.
2. Ask the team if someone needs help.
We prefer teamwork over solo-work, our goal is quality so verification is important.

## Estimating tasks
In the flank, we are using 3 points scale for estimates tasks complexity (snake, tiger, dragon).
For more details see [estimation.md](3_estimation.md)

## Working on a new task
Perform estimation if needed. If the task looks complex it's good practice asking a team for help.
Typically, estimates should be already done on a weekly review, but issues reported after review may have a lack of estimation.
According to task complexity choose the best strategy.

#### Snakes
Snake is easy and don't need any explanations. So if the task requires some implementation, everything that you need is a new branch,
pull request with proper description and some tests. After you finished don't forget to mention someone from the team for help.

#### Tigers
If you are starting work on a tiger, always consider sharing some work with someone from the team, for more details see `Buddy system` section.
Typically, every tiger brings some common things to do like:
* Pull request with proper description which typically may contain the following information:
    * description
    * user story
    * definition of done
    * how to reproduce
* Documentation. for more details see the `Documentation` section.
* Unit tests
* Dedicated YAML config with required assets for manual or integration testing

#### Dragons
Dragon requires research, so you should always start from writing some documentations draft.
This draft should contain any important pieces of information about the task and should be synchronized with the status of knowledge according to any progression.
The dragon task may be started by one developer but shouldn't be handle alone too long.
The task could be a dragon as long as it's unknown and mysterious when there is a plan on how to deal with it,
it's automatically become one or many tigers.
So the main goal when deal with a dragon is to prepare documentation on how to solve the problem.


### Documentation
Be aware, some tasks sometimes couldn't be resolved for many reasons, so it's really important to have documentation always up to date.
Having documentation up to date gives the ability to drop work at any moment and back to in the future without loss of any information.

### Buddy system
See [buddy_system.md](4_buddy_system.md) to read about team work in a flank.

### Ktlint styling and pre-commit hooks
To ensure that the correct styling for Flank is upheld ensure that [environment setup](1_environment_setup.md) is read, and the correct `flankScripts` commands have been run
so that Ktlint is applied to the idea project, and the pre-commit hook is working correctly.
