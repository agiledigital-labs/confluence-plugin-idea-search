Confluence.Blueprint.setWizard(
    'au.com.agiledigital.idea_search:create-by-sample-template',
    function (wizard) {
      wizard.on('submit.page1Id', function (e, state) {
        if (!state.pageData.vIdeaTitle) {
          alert('Please provide a name value.');
          return false;
        }
      });
    }
);
