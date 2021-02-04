import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/styles";
import { IChangeEvent, withTheme } from "@rjsf/core";
import { Theme } from "@rjsf/fluent-ui";
import { JSONSchema7, validate } from "json-schema";
import axios from "axios";
import UserSelection from "./UserSelection";
import RestSelection from "./RestSelection";


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

const InnerIdea = ({ schema, context, formData, onFormChange }: any) => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <Form
        {...{
          formData,
          onChange: onFormChange,
          schema,
          uiSchema: uiSchema(context),
          widgets,
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
  const classes = useStyles();

  const [restSchema, setRestSchema] = useState<JSONSchema7>({});

  useEffect(() => {
    axios.get(`/confluence/rest/idea/1/schema`).then((data) => {
      setRestSchema(JSON.parse(data.data.schema));
    });
  }, []);

  const validateSchema = validate(restSchema, {});

  const onFormChange = (event: IChangeEvent) => {
    setFormData(event.formData);
  };

  if (!validateSchema.valid) {
    return (
      <div className={classes.root}>
        There are the following error
        {validateSchema.errors.length > 1 ? "s" : ""} in the schema
        <ul>
          {validateSchema.errors.map((error, index) => (
            <li key={index}>error</li>
          ))}
        </ul>
      </div>
    );
  }
  return (
    <InnerIdea
      {...{
        schema: restSchema,
        context: "/confluence",
        formData,
        onFormChange,
      }}
    />
  );
};
export default OuterIdea;
