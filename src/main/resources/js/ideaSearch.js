Confluence.Blueprint.setWizard(
  'au.com.agiledigital.idea_search:create-by-sample-template',
  function (wizard) {
    wizard.on('submit.page1Id', function (e, state) {
      console.log(state.pageData);
      var ideaTitle = state.pageData.vIdeaTitle;
      var ideaDescription = state.pageData.vIdeaDescription;
      var ideaOwner = state.pageData.vIdeaOwner;
      var ideaStatus = state.pageData.vIdeaStatus;
      var ideaTeam = state.pageData.vIdeaTeam;
      console.log(ideaDescription, ideaOwner, ideaStatus, ideaTeam);
      if (!ideaTitle) {
        alert('Please provide a name value.');
        return false;
      }
    });
  }
);
