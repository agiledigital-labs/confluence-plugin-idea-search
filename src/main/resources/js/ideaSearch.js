Confluence.Blueprint.setWizard(
    "au.com.agiledigital.idea_search:ideaSearch-page-blueprint-entry",
    function (wizard) {
        AJS.Confluence.Binder.autocompleteMultiUser();
        AJS.$("#tw").auiSelect2();
        wizard.on("post-render.page1Id", () => (
            AJS.$("#tw").auiSelect2({
                    multiple: true,
                    ajax: {
                        url: `${AJS.contextPath()}/rest/idea/1/technology`,
                        data: (params) => ({q: params}),
                        processResults: (data) => ({
                            results: data.map((item, index) => ({id: index, text: item.label}))
                        }),
                        results: (data) => ({
                            results: data.map((item, index) => ({id: Math.random(), text: item.label}))
                        }),
                    }
                }
            )
        ))
        wizard.on("submit.page1Id", function (e, state) {
            if (!state.pageData.vIdeaTitle) {
                alert("Please provide a name value.");
                return false;
            }
        });
    }
);
