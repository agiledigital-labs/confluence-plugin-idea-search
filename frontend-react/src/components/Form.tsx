import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/styles";
import { IChangeEvent, withTheme } from "@rjsf/core";
import { Theme } from "@rjsf/fluent-ui";
import { JSONSchema7, validate } from "json-schema";
import axios from "axios";
import UserSelection from "./UserSelection";
import RestSelection from "./RestSelection";
import { version } from "./index";
import SectionMessage from "@atlaskit/section-message";

const Form = withTheme(Theme);

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

const InnerFrom = ({ schema, uiSchema, formData, onFormChange }: any) => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <Form
        {...{
          formData,
          onChange: onFormChange,
          schema,
          uiSchema,
          widgets,
          liveValidate: true,
        }}
      >
        <></>
      </Form>
    </div>
  );
};
const OuterIdea = ({
  formData,
  setFormData,
}: {
  formData?: object;
  setFormData: React.Dispatch<React.SetStateAction<object>>;
}) => {
  const [restSchema, setRestSchema] = useState<JSONSchema7>({});
  const [uiSchema, setUiSchema] = useState<JSONSchema7>({});
  const contextPath = window.AJS ? window.AJS.contextPath() : "";

  useEffect(() => {
    axios.get(`${contextPath}/rest/idea/${version}/schema`).then((data) => {
      setRestSchema(JSON.parse(data.data.schema));
      setUiSchema(JSON.parse(data.data.uiSchema));
    });
  }, []);

  const validateSchema = validate(restSchema, {});

  const onFormChange = (event: IChangeEvent) => {
    setFormData(event.formData);
  };

  if (!validateSchema.valid) {
    return (
      <SectionMessage title={"Errors in the provided schema"}>
        <p>
          There {validateSchema.errors.length > 1 ? "are" : "is"} the following
          error
          {validateSchema.errors.length > 1 ? "s" : ""} in the schema
          <ul>
            {validateSchema.errors.map((error, index) => (
              <li key={index}>error</li>
            ))}
          </ul>
        </p>
      </SectionMessage>
    );
  }
  return (
    <InnerFrom
      {...{
        schema: restSchema,
        uiSchema,
        context: contextPath,
        formData,
        onFormChange,
      }}
    />
  );
};
export default OuterIdea;
