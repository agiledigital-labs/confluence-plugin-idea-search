const simpleTextInput = `<section id="edit-page-dialog" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
    <header class="aui-dialog2-header">
        <h2 class="aui-dialog2-header-main">Captain...</h2>
        <a class="aui-dialog2-header-close">
            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
        </a>
    </header>
        <div class="aui-dialog2-content tags-input edit-page-dialog-input">
          <input id="simple-edit-page-dialog-input" tabindex="0"
                 role="text" aria-expanded="false"/>
    </div>
    <footer class="aui-dialog2-footer">
        <div class="aui-dialog2-footer-actions">
            <button id="dialog-submit-button" class="aui-button aui-button-primary">Make it so</button>
        </div>
    </footer>
</section>`

const statusInput = `<section id="edit-page-dialog" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
    <header class="aui-dialog2-header">
        <h2 class="aui-dialog2-header-main">Captain...</h2>
        <a class="aui-dialog2-header-close">
            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
        </a>
    </header>
        <div class="aui-dialog2-content tags-input edit-page-dialog-input">
        <label
            for="videastatus">Select the new status</label>
        <select id="videastatus" name="vIdeaStatus">
          <option value="new">New</option>
          <option value="inProgress">In progress</option>
          <option value="completed">Completed</option>
          <option value="abandoned">Abandoned</option>
        </select>
    </div>
    <footer class="aui-dialog2-footer">
        <div class="aui-dialog2-footer-actions">
            <button id="dialog-submit-button" class="aui-button aui-button-primary">Make it so</button>
        </div>
    </footer>
</section>`

const techInput = `<section id="edit-page-dialog" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
    <header class="aui-dialog2-header">
        <h2 class="aui-dialog2-header-main">Captain...</h2>
        <a class="aui-dialog2-header-close">
            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
        </a>
    </header>
    <div class="aui-dialog2-content">
    
      <form action="#" class="aui">
    <fieldset>
        <div class="tags-input edit-page-dialog-input">
        <label for="videatechnology">start typing technologies</label>
        <input  multiple=true
         name="vIdeaTechnology"
                id="videatechnology"
        >
        </input>
    </div>
        </fieldset>
  </form>
</div>
    <footer class="aui-dialog2-footer">
        <div class="aui-dialog2-footer-actions">
            <button id="dialog-submit-button" class="aui-button aui-button-primary">Make it so</button>
        </div>
    </footer>
</section>`

const userInput = `<section id="edit-page-dialog" class="aui-dialog2 aui-dialog2-small aui-layer" role="dialog" aria-hidden="true">
    <header class="aui-dialog2-header">
        <h2 class="aui-dialog2-header-main">Captain...</h2>
        <a class="aui-dialog2-header-close">
            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
        </a>
    </header>
    <div class="aui-dialog2-content">
    
      <form action="#" class="aui">
    <fieldset>
        <div class="tags-input edit-page-dialog-input" id="status-multiselect-container">
        <label for="videaowner">hn</label>
        <input id="status" class="autocomplete-multiuser" type="text" role="textbox" aria-expanded="false"
                 aria-controls="autocomplete-results" size="5"
               name="vIdeaOwner" data-none-message="No users found"
              />
    </div>
        </fieldset>
  </form>
</div>
    <footer class="aui-dialog2-footer">
        <div class="aui-dialog2-footer-actions">
            <button id="dialog-submit-button" class="aui-button aui-button-primary">Make it so</button>
        </div>
    </footer>
</section>`


var updateMacro = function (macroNode, params, newBody) {


  var $macroDiv = AJS.$(macroNode);
  AJS.Rte.getEditor().selection.select($macroDiv[0]);
  AJS.Rte.BookmarkManager.storeBookmark();


  var macroRenderRequest = {
    contentId: Confluence.Editor.getContentId(),
    macro: {
      name: "idea-structured-field",
      params,
      defaultParameterValue: "",
      body: newBody
    }
  };

  tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
};

function waitLoop() {
  if (AJS.Confluence.PropertyPanel.Macro !== undefined && AJS.Confluence.PropertyPanel.Macro.registerButtonHandler !== undefined) {


    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("update", function (e, macroNode) {

      const originalBody = macroNode.textContent
      var categoryArray = macroNode.dataset.macroParameters.split("=");
      var macroParams = {[categoryArray[0]]: categoryArray[1]};

      AJS.$("#edit-page-dialog").remove();

      switch (categoryArray[1]) {
        case "description":
          var newBodyText = '';
          AJS.$("body").append(simpleTextInput)
          AJS.$('.edit-page-dialog-input').on({
            input: ({target}) => {
              newBodyText = AJS.$(target).val().toLowerCase();
            }
          })
          AJS.dialog2("#edit-page-dialog").show();

          AJS.$("#dialog-submit-button").click(function (e) {
            e.preventDefault();
            AJS.dialog2("#edit-page-dialog").hide();
            updateMacro(macroNode, macroParams, newBodyText);
            AJS.$("#edit-page-dialog").remove();
          });
          return
        case 'status':
          var newBodyText = '';
          AJS.$("body").append(statusInput)
          AJS.$('.edit-page-dialog-input').on({
            input: ({target}) => {
              newBodyText = target.value;
            }
          })
          AJS.dialog2("#edit-page-dialog").show();

          AJS.$("#dialog-submit-button").click(function (e) {
            e.preventDefault();
            AJS.dialog2("#edit-page-dialog").hide();
            updateMacro(macroNode, macroParams, newBodyText);
            AJS.$("#edit-page-dialog").remove();
          });
          return
        case "owner":

          AJS.$("body").append(userInput)
          AJS.$('.edit-page-dialog-input').on({
            input: ({target}) => {
              newBodyText = AJS.$(target).val().toLowerCase();
            }
          })

          AJS.Confluence.Binder.autocompleteMultiUser();

          AJS.dialog2("#edit-page-dialog").show();

          AJS.$("#dialog-submit-button").click(function (e) {
            const newTechArray = AJS.$(".select2-search-choice").toArray().map(item => item.innerText).map(text => "<a class=\"confluence-userlink user-mention current-user-mention userlink-0\" data-username=\"" + text +
              "\" href=\"/confluence/display/~" + text +
              "\" data-linked-resource-id=\"65588\" data-linked-resource-version=\"1\" data-linked-resource-type=\"userinfo\" data-base-url=\"http://localhost:1990/confluence\" title=\"\" data-user-hover-bound=\"true\">" + text +
              "</a>").join(", ");


            e.preventDefault();
            AJS.dialog2("#edit-page-dialog").hide();
            updateMacro(macroNode, macroParams, newTechArray);
            AJS.$("#edit-page-dialog").remove();
          });
          return
        case "team":
          AJS.$("body").append(userInput)
          AJS.$('.edit-page-dialog-input').on({
            input: ({target}) => {
              newBodyText = AJS.$(target).val().toLowerCase();
            }
          })

          AJS.Confluence.Binder.autocompleteMultiUser();

          AJS.dialog2("#edit-page-dialog").show();

          AJS.$("#dialog-submit-button").click(function (e) {
            const newTechArray = AJS.$(".select2-search-choice").toArray().map(item => item.innerText).map(text => "<a class=\"confluence-userlink user-mention current-user-mention userlink-0\" data-username=\"" + text +
              "\" href=\"/confluence/display/~" + text +
              "\" data-linked-resource-id=\"65588\" data-linked-resource-version=\"1\" data-linked-resource-type=\"userinfo\" data-base-url=\"http://localhost:1990/confluence\" title=\"\" data-user-hover-bound=\"true\">" + text +
              "</a>").join(", ");


            e.preventDefault();
            AJS.dialog2("#edit-page-dialog").hide();
            updateMacro(macroNode, macroParams, newTechArray);
            AJS.$("#edit-page-dialog").remove();
          });
          return
        case "technologies":
          var newBodyText = '';
          AJS.$("body").append(techInput)

          AJS.$("#videatechnology").auiSelect2({
            multiple: true,
            ajax: {
              url: `${AJS.contextPath()}/rest/idea/1/technology`,
              data: (params) => ({q: params}),
              processResults: (data) => ({
                results: data.map((item, index) => ({
                  id: item.label.toLowerCase(),
                  text: item.label,
                })),
              }),
              results: (data) => ({
                results: data.map((item, index) => ({
                  id: item.label.toLowerCase(),
                  text: item.label,
                })),
              }),
            },
          })
          AJS.dialog2("#edit-page-dialog").show();

          AJS.$("#dialog-submit-button").click(function (e) {
            const newTechArray = AJS.$(".select2-search-choice").toArray().map(item => item.innerText);
            const oldTechArray = originalBody.split(",");
            const newBodyText = [...newTechArray, ...oldTechArray].reduce((pre, cur) => {
              if (pre.includes(cur)) {
                return pre
              } else {
                return [...pre, cur]
              }
            }, []).join(',')
            e.preventDefault();
            AJS.dialog2("#edit-page-dialog").hide();
            updateMacro(macroNode, macroParams, newBodyText);
            AJS.$("#edit-page-dialog").remove();
          });


          return


      }

    });


  } else {
    setTimeout(waitLoop, 500)
  }
}

waitLoop();