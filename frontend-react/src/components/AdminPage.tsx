import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";
import TextArea from "@atlaskit/textarea";
import Button from "@atlaskit/button/standard-button";
import SectionMessage from "@atlaskit/section-message";
import Form, { IChangeEvent, WidgetProps } from "@rjsf/core";
import { JSONSchema7 } from "json-schema";
import axios from "axios";

interface formDataType {
  schema: JSONSchema7;
  uiSchema: JSONSchema7;
  indexSchema: JSONSchema7;
}

const minRows: number = 12;

const atlasTextArea = (props: WidgetProps) => {
  return (
    <TextArea
      {...{
        value: props.value,
        minimumRows: minRows,
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
  atlasTextArea,
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
  const testValidate = (data: string): boolean => {
    try {
      JSON.parse(data);
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

const OuterAdminForm = () => {
  const contextPath = window.AJS ? window.AJS.contextPath() : "/confluence";

  const [formData, setFormData] = useState<formDataType>();

  useEffect(() => {
    axios.get(`${contextPath}/rest/idea/1/schema`).then((response) =>
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

  const [submissionFeedback, setSubmissionFeedback] = useState<{
    title?: string;
    appearance?:
      | "info"
      | "warning"
      | "error"
      | "confirmation"
      | "change"
      | undefined;
    message?: string;
    hidden: boolean;
  }>({ hidden: true });

  const updateSchema = (data: formDataType | undefined) => {
    axios.post(`${contextPath}/rest/idea/1/schema`, data).then((response) =>
      response.status === 200
        ? setSubmissionFeedback({
            title: "Schemas saved Successfully",
            appearance: "confirmation",
            message: "Your schema has been saved successfully",
            hidden: false,
          })
        : setSubmissionFeedback({
            title: "Failed to save schemas",
            appearance: "error",
            message: `Could not save schema`,
            hidden: false,
          })
    );
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
          updateSchema(formData);
        }}
      >
        <Button type="submit" appearance="primary">
          Save
        </Button>
        <div hidden={submissionFeedback.hidden}>
          <SectionMessage
            title={submissionFeedback.title} //"This account will be permanently deleted"
            appearance={submissionFeedback.appearance} //"error"
          >
            <p>{submissionFeedback.message}</p>
          </SectionMessage>
        </div>
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
