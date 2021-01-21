import React, { useCallback, useEffect, useState } from "react";
import ReactDOM from "react-dom";
import { Button, Paper, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";
import { IChangeEvent, withTheme } from "@rjsf/core";
import { Theme as MaterialUITheme } from "@rjsf/material-ui";

// Make modifications to the theme with your own fields and widgets
import { JSONSchema7, validate } from "json-schema";
// @ts-ignore
import axios from "axios";
import UserSelection from "./UserSelection";
import RestSelection from "./RestSelection";

// const theme = {
//   ...MaterialUITheme,
//   ArrayFieldTemplate: CustomInput,
// };

const Form = withTheme(MaterialUITheme);

const useStyles = makeStyles(() => ({
  root: {
    width: "100%",
  },
  paper: {
    width: "100%",
    marginBottom: "1rem",
  },
  table: {
    minWidth: 750,
  },
  popover: {
    width: "100%",
    padding: "1rem",
    margin: "1px",
  },
}));

const widgets = {
  UserSelection: UserSelection,
  RestSelection: RestSelection,
};
const uiSchema = (context: string) => ({
  team: {
    items: {
      context,
      endpoint: "rest/prototype/1/search/user.json?max-results=6&query=",
      "ui:widget": UserSelection,
    },
  },
  owner: {
    context,
    endpoint: "rest/prototype/1/search/user.json?max-results=6&query=",
    "ui:widget": UserSelection,
  },
  technologies: {
    items: {
      context,
      endpoint: "rest/idea/1/technology?q=",
      "ui:widget": RestSelection,
    },
  },
});
const InnerIdea = ({
  schema,
  context,
  toggleDisabledForm,
  isDisabledForm,
  formData,
  onFormChange,
}: any) => {
  const classes = useStyles();
  // @ts-ignore
  // AJS.Confluence.Binder.autocompleteMultiUser();
  return (
    <div className={classes.root}>
      <Paper className={classes.paper} elevation={0}>
        <Typography variant="h2">
          {isDisabledForm ? "Viewer" : "Editor"} mode
        </Typography>{" "}
        <Button onClick={toggleDisabledForm}>
          {isDisabledForm ? "Edit" : "Submit"}
        </Button>
        <Form
          {...{
            formData,
            onChange: onFormChange,
            schema,
            uiSchema: uiSchema(context),
            widgets,
            disabled: isDisabledForm,
          }}
        >
          <></>
        </Form>
      </Paper>
    </div>
  );
};
const OuterIdea = () => {
  const classes = useStyles();
  const schema = document
    ? // @ts-ignore
      JSON.parse(document.getElementById("schema").value.replace(/'/g, '"'))
    : "";
  const context = document
    ? // @ts-ignore
      document.getElementById("contextPath").value
    : "";

  const [hack, setHack] = useState(false);

  const harkFn = useCallback(
    () =>
      setTimeout(() => {
        setHack(true);
      }, 5000),
    []
  );

  if (!hack) {
    harkFn();
  }

  const [isDisabledForm, setIsDisabledForm] = useState(true);

  const toggleDisabledForm = () => setIsDisabledForm(!isDisabledForm);

  const [restSchema, setRestSchema] = useState<JSONSchema7>(schema);

  // });

  useEffect(() => {
    axios.get(`${context}/rest/idea/1/schema`).then((data) => {
      setRestSchema(data.data);
    });
    setHack(true);
  }, [hack]);

  const validateSchema = validate(restSchema, {});

  const [formData, setFormData] = useState({});

  const onFormChange = (event: IChangeEvent) => {
    setFormData(event.formData);
  };

  if (!validateSchema.valid) {
    return (
      <div className={classes.root}>
        <Paper className={classes.paper} elevation={0}>
          There are the following error
          {validateSchema.errors.length > 1 ? "s" : ""} in the schema
          <ul>
            {validateSchema.errors.map((error, index) => (
              <li key={index}>error</li>
            ))}
          </ul>
        </Paper>
      </div>
    );
  }
  return (
    <InnerIdea
      {...{
        schema: restSchema,
        context,
        toggleDisabledForm,
        isDisabledForm,
        formData,
        onFormChange,
      }}
    />
  );
};
export default OuterIdea;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("container-idea");
  // @ts-ignore
  // eslint-disable-next-line @typescript-eslint/no-unused-expressions
  wrapper ? ReactDOM.render(<OuterIdea />, wrapper) : false;
});
