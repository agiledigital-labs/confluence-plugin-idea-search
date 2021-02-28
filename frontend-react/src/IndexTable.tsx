import ReactDOM from "react-dom";
import React from "react";
import IndexTable from "./components/IndexTable";
// https://stackoverflow.com/questions/22362563/creating-a-dynamic-table-in-confluence-that-is-sortable
// Confluence takes control of any table that is on the page when it is loaded.
// Need to make it likely that the table is not on the page on load by delaying the mounting.
// This is a hack to get around otherwise helpful behaviour
window.addEventListener("load", () => {
  setTimeout(() => {
    const wrapper = window.document.getElementById("index-container");
    return wrapper ? ReactDOM.render(<IndexTable />, wrapper) : false;
  }, 500);
});
