import Form from "./components/Form";
import ReactDOM from "react-dom";
import React from "react";
import { Box } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";
import { Wizard } from "./components";

const useStyles = makeStyles(() => ({
  scroll: {
    width: "100%",
    maxHeight: "335px",
  },
}));

const SimpleForm = ({ data, changeData }: any) => {
  const classes = useStyles();

  return (
    <>
      <Box overflow="scroll" className={classes.scroll}>
        <Form
          {...{
            formData: data,
            setFormData: changeData,
          }}
        />
      </Box>
    </>
  );
};

window.AJS.Confluence.Blueprint.setWizard(
  "au.com.agiledigital.structured_form:formDataSearch-page-blueprint-entry",
  (wizard: Wizard) => {
    let data = {};
    const changeData = (newData: object) => {
      data = newData;
      window.document
        .getElementById("vformdata")
        ?.setAttribute("value", JSON.stringify(data));
    };
    wizard.on("post-render.page2Id", () => {
      const wrapper = window.document.getElementById("form-container");
      return wrapper
        ? ReactDOM.render(<SimpleForm {...{ data, changeData }} />, wrapper)
        : false;
    });
  }
);
