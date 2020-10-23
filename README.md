# Confluence Plugin Idea Search
Provides a blue print for idea capture, search, and display.


# Requirements

The plugin is built using the Atlassian SDK, for installation instructions see [Atlassian documentation](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/).

# Adding to confluence

Either add the addon throught he Atlassian Market place (link to come) or by directly installing the .jar file from [one of the releases](https://github.com/agiledigital-labs/confluence-plugin-idea-search/releases)


# Developing

* Clone the repository, install the Atlassian SDK, 
* In the root directory run atlas-run

# Contributing

PR are welcomed

## Commit message syntax

This repository is using [semantic release](https://semantic-release.gitbook.io/semantic-release/), therefore the commit messages should follow the [angular guidelines](https://github.com/angular/angular.js/blob/master/DEVELOPERS.md#-git-commit-guidelines) or [another guide](https://blog.greenkeeper.io/introduction-to-semantic-release-33f73b117c8) .

## Branching strategy

Branches should be attached either to an issue in this repository or a ticket on the jira, 

### A branch attached to an issue

Please name the branch `type`/issueNumber/`description`

where type is either;
* feat
* fix
* chore
* docs
* etc from the semvar naming strategy

For example:
`feat/issue3/add-idea-blueprint`

### A branch attached to a Jira ticket

Please name the branch `type`/ticketNumber/`description`

where type is either;
* feat
* fix
* chore
* docs
* etc from the semvar naming strategy

For example:
`feat/ADE-101/add-idea-blueprint`

## Making a PR

Please do the following before creating a PR;
* have an issue or a jira ticket,
* write a descriptive PR title
* Add the @agiledigital-labs/atlassian-plugin team as a reviewer
