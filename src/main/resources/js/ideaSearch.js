Confluence.Blueprint.setWizard(
    'au.com.agiledigital.idea_search:ideaSearch-page-blueprint-entry',
    function (wizard) {
      AJS.Confluence.Binder.autocompleteMultiUser();

      wizard.on('submit.page1Id', function (e, state) {
        if (!state.pageData.vIdeaTitle) {
          alert('Please provide a name value.');
          return false;
        }
      });
    }
);
