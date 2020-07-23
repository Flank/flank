# Estimating Eng Work: Snakes, Tigers, Dragons

## Introduction
This document describes a model for estimating the amount of work an engineering task requires. 
It is not about estimating how long that work will take — that is a different discussion for a different time. 
It is not about how to arrive at these estimates — that is also a different discussion for a different time.
The primary aim of estimating size is to increase the velocity of those doing the work, i.e. engineering team. 
These estimates might give some amount of signal to other stakeholders, e.g. product management or eng leadership, but that is not the goal. 
Those stakeholders must be informed by different means. Because these estimates serve the needs of the team, 
they need to be arrived at by the engineers who are going to be doing the work. 
Any observer who wishes to adjust the team-arrived estimates needs to sign up for executing those tasks.

## Snakes
A “snake” is a small task that can be executed without interrupting or requiring attention from any other member of the team. 
Small wordsmithing updates to a design doc, or a runbook are good examples of a snake. 
Creating a new diff to fix a bug that one noticed while working on something unrelated — not a snake — because a diff review will require attention from another team member. 
Drive-by fixes inside a diff that’s already going out for review can sometimes be a snake, depending on the complexity of the fix.
The bottom line is, tasks that are sized as a “snake” do not need to be talked about, they just need to be done. 
As a corollary, there is no task that is 2 snakes or 7 snakes. It’s either a snake or one of the creatures below.
The idea of a “snake” comes from US Army Ranger training. 
They’re told that if, when moving through an area, they see a snake that presents a danger, they should just kill the snake. 
They should not yell “check this out, SNAKE!” 
[Jim Barksdale](https://en.wikipedia.org/wiki/James_L._Barksdale), 
who [created](https://en.wikipedia.org/wiki/AT%26T_Wireless_Services#McCaw_Cellular) two [industries](https://en.wikipedia.org/wiki/Netscape), 
making our jobs today possible, also used the analogy. 
“Do not have a meeting about a snake. Do not form a committee to discuss a snake. Do not send out a survey about a snake. Just kill it.”

## Tigers
One “tiger” is an amount of work that is well understood by the team. 
It is a real creature with shared characteristics, so, once you have dispatched one, you have an idea of what it’s like to dispatch another one. 
The team has to agree on what constitutes one tiger and comparing tigers across teams is a counterproductive exercise.
E.g. 1 Tiger is: fixing an NPE (or language-equivalent), writing a unit test to ensure that the fix actually fixed something, 
reviewing that, landing the diff, following through to ensure it makes it to production. Tasks can be 1, 2, 3, 5, 8, or 13 tigers — nothing in between. 
If a task is estimated to be bigger than 13 tigers, it needs to be broken up.

## Dragons
A dragon is a mythical creature; each one is unlike any other encountered before. 
Not enough is known at estimation time to meaningfully discuss the task and assign a number of tigers. 
When a team identifies a dragon, the next step is to create a task that generates information required to turn that dragon into tiger-sized tasks 
(often that de-dragonning task is best done as a time-boxed exercise, but that’s a different discussion for a different time).

*posted on internal slack channel by @bootstraponline*
