### Basic Conventions
#### Branch Per Feature
For each feature that is being worked on, make a new branch off of master.  That
means you should run
```
git checkout master
```
*before* you run
```
git checkout -b [hyphenated-branch-name]
```
Otherwise, you branch will be based on whatever branch you're currently on,
which will mess up merging later on.

#### Pull-Rebase
Instead of running
```
git pull origin [hyphenated-branch-name]
```
or some variant to pull code from GitHub, run
```
git pull --rebase origin [hyphenated-branch-name]
```
to pull the changes that have already been pushed and to apply your changes on
top of them.  This keeps commits in chronological order and resolves simple
merge conflicts for you.

#### Merging into Master
Create a pull request to have your changes code reviewed (CR-ed).  At least one
other person (ideally two) should look at your code and provide feedback before
being merged in to master.  Once someone else has looked at and tested your
code, it can be merged in to master.  If you're not comfortable doing this, feel
free to ping someone else to do it for you.

1. Update the branch: `git pull --rebase`
1. Checkout to master: `git checkout master`
1. Update master: `git pull --rebase`
1. Merge the branch in using `--no-ff`: `git merge --no-ff
   [hyphenated-branch-name]`

The `--no-ff` argument prevents a fast-forward from happening.  This means that
you're manually creating a merge commit (but not a merge conflict).  This makes
life easier for everyone, since diffing the merge commit shows all changes made
on that branch, and also helps to mark where branches were merged in to master,
making reverting that pesky feature that's preventing us from deploying trivial.
It also makes the commit tree really easy to read (and pretty!), which is always
a plus.
