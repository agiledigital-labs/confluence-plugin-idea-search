import Form from "./components/Form";
import ReactDOM from "react-dom";
import React from "react";
import { Box } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles(() => ({
  scroll: {
    width: "100%",
    maxHeight: "335px",
  },
}));

const Test = ({ data, changeData }: any) => {
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

// @ts-ignore
window.Confluence.Blueprint.setWizard(
  "au.com.agiledigital.structured_form:ideaSearch-page-blueprint-entry",
  // @ts-ignore
  function (wizard) {
    let data = {};
    const changeData = (newData: object) => {
      data = newData;
      console.log("asdf");
      window.document
        .getElementById("vformdata")
        ?.setAttribute("value", JSON.stringify(data));
    };
    wizard.on("post-render.page2Id", () => {
      const wrapper = window.document.getElementById("form-container");
      // eslint-disable-next-line @typescript-eslint/no-unused-expressions
      wrapper
        ? ReactDOM.render(<Test {...{ data, changeData }} />, wrapper)
        : false;
    });
  }
);
