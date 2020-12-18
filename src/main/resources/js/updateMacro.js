// HTML dialog for simple text input
const simpleTextInput = `
<section id="edit-page-dialog-text" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
  <header class="aui-dialog2-header">
    <h2 class="aui-dialog2-header-main">Update the description for this idea</h2>
    <a class="aui-dialog2-header-close">
      <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
    </a>
  </header>
  <div class="aui-dialog2-content tags-input edit-page-dialog-input">
    <input id="simple-edit-page-dialog-input" tabindex="0" role="text" aria-expanded="false"/>
  </div>
  <footer class="aui-dialog2-footer">
    <div class="aui-dialog2-footer-actions">
      <button id="dialog-submit-button" class="aui-button aui-button-primary">Update</button>
    </div>
  </footer>
</section>`;

// HTML dialog for idea status input
const statusInput = `
<section id="edit-page-dialog-status" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
  <header class="aui-dialog2-header">
    <h2 class="aui-dialog2-header-main">Update the status for this idea</h2>
    <a class="aui-dialog2-header-close">
      <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
    </a>
  </header>
  <div class="aui-dialog2-content tags-input edit-page-dialog-input">
    <label for="idea-status">Select the new status</label>
    <select id="idea-status" name="Idea-Status">
      <option value="new">New</option>
      <option value="inProgress">In progress</option>
      <option value="completed">Completed</option>
      <option value="abandoned">Abandoned</option>
    </select>
  </div>
  <footer class="aui-dialog2-footer">
    <div class="aui-dialog2-footer-actions">
      <button id="dialog-submit-button" class="aui-button aui-button-primary">Update</button>
    </div>
  </footer>
</section>`;

// HTML dialog to search and add technologies
const techInput = `
<section id="edit-page-dialog-tech" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
  <header class="aui-dialog2-header">
    <h2 class="aui-dialog2-header-main">Add new technologies to this idea</h2>
    <a class="aui-dialog2-header-close">
      <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
    </a>
  </header>
  <div class="aui-dialog2-content">
    <form action="#" class="aui">
      <fieldset>
        <div class="tags-input edit-page-dialog-input">
          <label for="idea-technology">Technologies</label>
          <input  multiple=true name="Idea-Technology" id="idea-technology"  />
        </div>
      </fieldset>
    </form>
  </div>
  <footer class="aui-dialog2-footer">
      <div class="aui-dialog2-footer-actions">
        <button id="dialog-submit-button" class="aui-button aui-button-primary">Update</button>
      </div>
  </footer>
</section>`;

// HTML dialog to search and add users
const userInput = `
<section id="edit-page-dialog-user" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
  <header class="aui-dialog2-header">
    <h2 class="aui-dialog2-header-main">Update owner or team for this idea</h2>
    <a class="aui-dialog2-header-close">
      <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
    </a>
  </header>
  <div class="aui-dialog2-content">
    <form action="#" class="aui">
      <fieldset>
        <div class="tags-input edit-page-dialog-input" id="status-multiselect-container">
          <label for="idea-owner">Users</label>
          <input id="status" class="autocomplete-multiuser" type="text" role="textbox" aria-expanded="false" 
            aria-controls="autocomplete-results" size="5"
            name="Idea-Owner" data-none-message="No users found"  />
        </div>
      </fieldset>
    </form>
  </div>
  <footer class="aui-dialog2-footer">
    <div class="aui-dialog2-footer-actions">
      <button id="dialog-submit-button" class="aui-button aui-button-primary">Update</button>
    </div>
  </footer>
</section>`;

/**
 * Handle the update of the user macro
 *
 * @param getNewUserArray function that returns an array of user link elements
 * @param macroNode to be updated
 * @param macroParams existing on the macro that is being updated
 */
const updateUserList = (getNewUserArray, macroNode, macroParams) => {
  AJS.$("body").append(userInput);

  AJS.Confluence.Binder.autocompleteMultiUser();

  AJS.dialog2("#edit-page-dialog-user").show();

  AJS.$("#dialog-submit-button").click((e) => {
    const newUserArray = getNewUserArray();
    e.preventDefault();
    AJS.dialog2("#edit-page-dialog-user").hide();
    updateMacro(macroNode, macroParams, newUserArray);
    AJS.$("#edit-page-dialog-user").remove();
  });
};

/**
 * Handle the addition of new technologies
 *
 * @param originalBody of the macro that will be added to
 * @param newTechnologies function that returns list of technologies to be added
 * @param macroNode to be updated
 * @param macroParams existing on the macro that is being updated
 */
const addTechnologies = (
  originalBody,
  newTechnologies,
  macroNode,
  macroParams
) => {
  AJS.$("body").append(techInput);

  AJS.$("#idea-technology").auiSelect2({
    multiple: true,
    ajax: {
      url: `${AJS.contextPath()}/rest/idea/1/technology`,
      data: (params) => ({ q: params }),
      processResults: (data) => ({
        results: data.map((item) => ({
          id: item.label.toLowerCase(),
          text: item.label,
        })),
      }),
      results: (data) => ({
        results: data.map((item) => ({
          id: item.label.toLowerCase(),
          text: item.label,
        })),
      }),
    },
  });
  AJS.dialog2("#edit-page-dialog-tech").show();

  AJS.$("#dialog-submit-button").click((e) => {
    const oldTechArray = originalBody.split(",");

    // Construct the new technology list
    const newTechText = [...newTechnologies(), ...oldTechArray]
      //Remove duplicates
      .reduce((pre, cur) => {
        if (
          pre.includes(cur) ||
          cur.replaceAll(" ", "") ===
            " Add your technologies ".replaceAll(" ", "")
        ) {
          return pre;
        } else {
          return [...pre, cur];
        }
      }, [])
      .join(",");

    e.preventDefault();

    AJS.dialog2("#edit-page-dialog-tech").hide();

    updateMacro(macroNode, macroParams, newTechText);

    AJS.$("#edit-page-dialog-tech").remove();
  });
};

/**
 * Handle the switch of dialog based on the category
 *
 * @param e event from confluence
 * @param macroNode node that the event is on
 */
const switchByCategory = (e, macroNode) => {
  const originalBody = macroNode.textContent;

  const getStringsFromInput = () =>
    AJS.$(".select2-search-choice")
      .toArray()
      .map((item) => item.innerText);

  /**
   * Create the link to a user in confluence
   *
   * @returns confluence user link
   */
  const getNewUserArray = () =>
    getStringsFromInput()
      .map(
        (text) =>
          '<a class="confluence-userlink user-mention current-user-mention userlink-0" data-username="' +
          text +
          '" href="/confluence/display/~' +
          text +
          '" data-linked-resource-id="65588" data-linked-resource-version="1" data-linked-resource-type="userinfo" ' +
          'data-base-url="http://localhost:1990/confluence" title="" data-user-hover-bound="true">' +
          text +
          "</a>"
      )
      .join(", ");

  // The category assigned to the macro
  const categoryArray = macroNode.dataset.macroParameters.split("=");
  // Put the macro params into the correct format
  const macroParams = { [categoryArray[0]]: categoryArray[1] };

  const handleFormSubmit = (category, newBodyText) => {
    const formID = "#edit-page-dialog-" + category;

    AJS.$("#dialog-submit-button").click(function (e) {
      e.preventDefault();

      updateMacro(macroNode, macroParams, newBodyText());

      AJS.dialog2(formID).hide();
      AJS.$(formID).remove();
    });
  };

  /**
   * Display the status dialog, and update the macro body on submit
   */
  const updateStatus = () => {
    AJS.$("body").append(statusInput);

    const newStatus = () => AJS.$("#idea-status").val();

    AJS.dialog2("#edit-page-dialog-status").show();
    handleFormSubmit("status", newStatus);
  };

  /**
   * Display the description dialog and update the macro body on submit
   */
  const updateDescription = () => {
    AJS.$("body").append(simpleTextInput);
    const newBodyText = () => AJS.$("#simple-edit-page-dialog-input").val();

    AJS.dialog2("#edit-page-dialog-text").show();

    handleFormSubmit("text", newBodyText);
  };

  /**
   * Show the correct dialog for the category of the macro
   */
  switch (macroNode.dataset.macroParameters.split("=")[1]) {
    case "description":
      updateDescription(handleFormSubmit);
      return;
    case "status":
      updateStatus(handleFormSubmit);
      return;
    case "owner":
      updateUserList(getNewUserArray, macroNode, macroParams);
      return;
    case "team":
      updateUserList(getNewUserArray, macroNode, macroParams);
      return;
    case "technologies":
      addTechnologies(
        originalBody,
        getStringsFromInput,
        macroNode,
        macroParams
      );
      return;
  }
};

/**
 * Updates the macro on the page with the new body supplied
 *
 * @param macroNode that will be updated
 * @param params to be attached to the macro
 * @param newBody to be added as the body of the macro
 */
const updateMacro = function (macroNode, params, newBody) {
  var $macroDiv = AJS.$(macroNode);
  AJS.Rte.getEditor().selection.select($macroDiv[0]);
  AJS.Rte.BookmarkManager.storeBookmark();

  const macroRenderRequest = {
    contentId: Confluence.Editor.getContentId(),
    macro: {
      name: "idea-structured-field",
      params,
      defaultParameterValue: "",
      body: newBody,
    },
  };

  tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
};

// Timeout to put the button attachment at the end of the event-loop
const timeout = 500;

/**
 * Wait for the dependancies before attaching to the new macro button
 */
const attachUpdateButton = () => {
  if (
    AJS.Confluence.PropertyPanel.Macro !== undefined &&
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler !== undefined
  ) {
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler(
      "update",
      switchByCategory
    );
  } else {
    // Puts this at the end of the event loop, allows the Macro functions to be created
    setTimeout(attachUpdateButton, timeout);
  }
};

// Run the script to attach the button
attachUpdateButton();
