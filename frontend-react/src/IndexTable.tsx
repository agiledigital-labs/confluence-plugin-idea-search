import ReactDOM from "react-dom";
import React from "react";
import IndexTable from "./components/IndexTable";

window.addEventListener("load", function () {
  const wrapper = document.getElementById("index-container");
  // @ts-ignore
  wrapper ? ReactDOM.render(<IndexTable />, wrapper) : false;
});
