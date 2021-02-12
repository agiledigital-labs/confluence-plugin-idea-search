import React from "react";
import ReactDOM from "react-dom";
import OuterModal from "./components/Modal";

const AJS = window.AJS ? window.AJS : undefined;
const tinymce = window.tinymce ? window.tinymce : undefined;

/**
 * Updates the macro on the page with the new body supplied
 *
 * @param macroNode that will be updated
 * @param params to be attached to the macro
 * @param newBody to be added as the body of the macro
 */
const updateMacro = function (macroNode: any, newBody: any) {
  var $macroDiv = AJS?.$(macroNode);
  AJS?.Rte.getEditor().selection.select($macroDiv[0]);
  AJS?.Rte.BookmarkManager.storeBookmark();

  const macroRenderRequest = {
    contentId: AJS?.Confluence.Editor.getContentId(),
    macro: {
      name: "idea-structured-data",
      defaultParameterValue: "",
      body: newBody,
    },
  };
  tinymce?.confluence.MacroUtils.insertMacro(macroRenderRequest);
};

const OpenModal = (event: any, macroNode: any) => {
  console.log(macroNode.innerText.trim().length);
  const initFormData =
    macroNode.innerText.trim().length > 0
      ? JSON.parse(macroNode.innerText)
      : undefined;
  AJS?.$("body").append(techInput);
  const wrapper = document.getElementById("edit-page-dialog-tech");

  const onClose = (newBody: string) => () => {
    updateMacro(macroNode, newBody);

    //@ts-ignore
    ReactDOM.unmountComponentAtNode(wrapper);
  };
  ReactDOM.render(<OuterModal {...{ onClose, initFormData }} />, wrapper);
};

const techInput = `
 <div id="edit-page-dialog-tech">
</div>
`;

const attachUpdateButton = () => {
  if (
    AJS?.Confluence.PropertyPanel.Macro !== undefined &&
    AJS?.Confluence.PropertyPanel.Macro.registerButtonHandler !== undefined
  ) {
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler(
      "update",
      OpenModal
    );
  } else {
    // Puts this at the end of the event loop, allows the Macro functions to be created
    setTimeout(attachUpdateButton, 100);
  }
};

// Run the script to attach the button
attachUpdateButton();
