import React from "react";
import OuterIdea from "./Idea";
import ReactDOM from "react-dom";

const AJS = window.AJS ? window.AJS : undefined;

const OpenModal = (event: any, macroNode: any) => {
  console.log(event);
  console.log("tester");
  console.log(macroNode);

  window.addEventListener("load", function () {
    const wrapper = document.getElementById("container-idea");
    // @ts-ignore
    // eslint-disable-next-line @typescript-eslint/no-unused-expressions
    wrapper ? ReactDOM.render(<OuterIdea />, wrapper) : false;
  });
};

const attachUpdateButton = () => {
  if (
    AJS &&
    AJS.Confluence.PropertyPanel.Macro !== undefined &&
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler !== undefined
  ) {
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler(
      "updated",
      OpenModal
    );
  } else {
    // Puts this at the end of the event loop, allows the Macro functions to be created
    setTimeout(attachUpdateButton, 100);
  }
};

// Run the script to attach the button
attachUpdateButton();
