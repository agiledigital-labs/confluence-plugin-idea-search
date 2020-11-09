# Confluence Plugin Idea Search

Provides a blue print for idea capture, search, and display.

# Requirements

The plugin is built using the Atlassian SDK, for installation instructions see [Atlassian documentation](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/).

# Adding to confluence

Either add the addon throught he Atlassian Market place (link to come) or by directly installing the .jar file from [one of the releases](https://github.com/agiledigital-labs/confluence-plugin-idea-search/releases)

# Developing

1. Clone the repository
2. Install the [Atlassian SDK](https://developer.atlassian.com/server/framework/atlassian-sdk/)
3. In the root directory run `atlas-run`
4. Once Confluence has started you can access it via http://localhost:1990/confluence. Note you will need to change the baseUrl in the confluence settings otherwise you will have issues with the creation of resources

- To run without tests use the command `atlas-run -Dmaven.test.skip=true`
- We suggest dumpings logs to a file due to the large amount lines that are outputted. A command to do this would `rm -rf logs.log; atlas-run > logs.log`

To run the hot reload, have the confluence server running. Run the command `atlas-mvn package` which will build the app. The server will see the change and hot load the new version into the app.

## Developer tooling

The following tools are required/recommended to develop with this repository

- **Atlassian sdk**: https://developer.atlassian.com/server/framework/atlassian-sdk/
- **Yarn**: https://classic.yarnpkg.com/en/docs/install
- **Prettier**: https://prettier.io/docs/en/install.html
- **Nvm**: https://github.com/nvm-sh/nvm#installing-and-updating

# Contributing

PR are welcomed

## Commit message syntax

This repository is using [semantic release](https://semantic-release.gitbook.io/semantic-release/), therefore the commit messages should follow the [angular guidelines](https://github.com/angular/angular.js/blob/master/DEVELOPERS.md#-git-commit-guidelines) or [another guide](https://blog.greenkeeper.io/introduction-to-semantic-release-33f73b117c8) .

## Branching strategy

Branches should be attached either to an issue in this repository or a ticket on the jira,

### A branch attached to an issue

Please name the branch `type`/issueNumber/`description`

where type is either;

- feat
- fix
- chore
- docs
- etc from the semvar naming strategy

For example:
`feat/issue3/add-idea-blueprint`

### A branch attached to a Jira ticket

Please name the branch `type`/ticketNumber/`description`

where type is either;

- feat
- fix
- chore
- docs
- etc from the semvar naming strategy

For example:
`feat/ADE-101/add-idea-blueprint`

### Branch Flow

Your Branch => develop => qa => master

## Making a PR

Please do the following before creating a PR;

- have an issue or a jira ticket,
- write a descriptive PR title
- Add the @agiledigital-labs/atlassian-plugin team as a reviewer
- We will review and provide feedback as soon as we can, it is easier if you help us along

# Supported versions

We are supporting versions from 7.6.2 onwards at this stage, if there are any issues please raise an issue on this repo
