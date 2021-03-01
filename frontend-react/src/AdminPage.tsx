import { OuterAdminForm as AdminPage } from "./components/AdminPage";
import ReactDOM from "react-dom";
import React from "react";

window.addEventListener("load", function () {
  const wrapper = window.document.getElementById("admin-container");
  return wrapper ? ReactDOM.render(<AdminPage />, wrapper) : false;
});
