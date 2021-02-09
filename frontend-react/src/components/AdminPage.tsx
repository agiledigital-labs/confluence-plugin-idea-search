import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";
import TextArea from "@atlaskit/textarea";
import Button from "@atlaskit/button/standard-button";
import Form, { IChangeEvent, WidgetProps } from "@rjsf/core";
import { JSONSchema7 } from "json-schema";
import axios from "axios";

const atlasTextArea = (props: WidgetProps) => {
  return (
    <TextArea
      {...{
        value: props.value,
        minimumRows: 12,
        required: props.required,
        onChange: (event) => props.onChange(event.target.value),
      }}
    />
  );
};

const schema: JSONSchema7 = {
  properties: {
    schema: {
      type: "string",
      title: "Schema",
    },
    uiSchema: {
      type: "string",
      title: "UI Schema",
    },
    indexSchema: {
      type: "string",
      title: "Index Page Schema",
    },
  },
};

const widgets = {
  atlasTextArea: atlasTextArea,
};

const uiSchema = {
  schema: {
    "ui:widget": atlasTextArea,
  },
  uiSchema: {
    "ui:widget": atlasTextArea,
  },
  indexSchema: {
    "ui:widget": atlasTextArea,
  },
};

const validate = (formData: any, errors: any) => {
  console.log(
    "Inside the function, comparing " +
      formData.schema +
      " and " +
      formData.uiSchema
  );

  console.log(formData);

  const testValidate = (data: string): boolean => {
    try {
      console.log(JSON.parse(data));
      return true;
    } catch (e) {
      return false;
    }
  };

  Object.keys(formData)
    .map((key) => ({
      key,
      valid: testValidate(formData[key]),
    }))
    .filter((t) => !t.valid)
    .forEach((e) => errors[e.key].addError(`${e.key}: is not valid`));

  return errors;
};

const updateSchema = (data: any) => {
  console.log("Inside update schema now again");
  axios
    .put("http://shouv-box:1990/confluence/rest/idea/1/schema", data)
    .then((response) => console.log(response.data));
};

const OuterAdminForm = () => {
  const [formData, setFormData] = useState<{
    schema: JSONSchema7;
    uiSchema: JSONSchema7;
    indexSchema: JSONSchema7;
  }>();

  useEffect(() => {
    axios
      .get("http://shouv-box:1990/confluence/rest/idea/1/schema")
      .then((response) =>
        setFormData({
          schema: response.data.schema,
          uiSchema: response.data.uiSchema,
          indexSchema: response.data.indexSchema,
        })
      );
  }, []);

  const onFormChange = (event: IChangeEvent) => {
    setFormData(event.formData);
  };

  return (
    <div>
      <Form
        liveValidate
        schema={schema}
        uiSchema={uiSchema}
        widgets={widgets}
        formData={formData}
        onChange={onFormChange}
        validate={validate}
        onSubmit={() => {
          alert("Submitted!");
          console.log(formData);
          updateSchema(formData);
        }}
      >
        <Button type="submit" appearance="primary">
          Save
        </Button>
      </Form>
    </div>
  );
};

export default OuterAdminForm;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("admincontainer");
  // @ts-ignore
  wrapper ? ReactDOM.render(<OuterAdminForm />, wrapper) : false;
});
