import AdminPage from "./components/AdminPage";
import ReactDOM from "react-dom";
import React from "react";

window.addEventListener("load", function () {
  const wrapper = document.getElementById("admincontainer");
  // @ts-ignore
  wrapper ? ReactDOM.render(<AdminPage />, wrapper) : false;
});
