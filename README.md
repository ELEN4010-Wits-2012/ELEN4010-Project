Software Development III - Parallel Fluid Simulator (SmokEIE)
=============================================================

![logo](http://s17.postimage.org/6za72q2gv/Logo.png)

Group Members
-------------

Rudolf Hoehler (0600134Y)
Ronald Clark (363095)
Graham Peyton (309684)
Justin Wernick (380536)
Edward Steere (400589)

Prerequisites
-------------
MPIJava needs to be present on the computer. The system's CLASSPATH should be set to the mpijava/lib/classes/ folder, and the LD_LIBRARY_PATH variable must be set to the mpijava/lib/ folder.
MPIJava is available at http://www.hpjava.org/mpiJava.html


Running instructions
--------------------

1. To run the GUI, run 'ant fluid'
2. To run the unit tests, run 'ant test'

Common Git commands
-------------------
* Clone a repo: git clone git@github.com:ELEN4010-Wits-2012/ELEN4010-Project.git
* Stage a file: git add <file>
* Stage and commit a file with a commit message: git commit -a -m 'Commit Message'
* See Git status of files in working directory: git status
* See changes that you have staged: git diff --staged

### Branching:
* View available branches: git branch
* Create a new branch: git branch <Branch name>
* Switch to a branch so that work is saved to it: git checkout <Branch name>
* Push changes to the repo: git push origin <Branch name>
* To merge a branch in, switch to the branch you want to merge into and run: git merge <Branch name>
* When you are done with a branch, delete it with: git branch -d <Branch name>

How do you merge the online repo with your own Master branch?
