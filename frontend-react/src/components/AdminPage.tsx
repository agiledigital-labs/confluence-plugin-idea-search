import Button from "@atlaskit/button/standard-button";
import SectionMessage from "@atlaskit/section-message";
import TextArea from "@atlaskit/textarea";
import Form, { IChangeEvent, WidgetProps } from "@rjsf/core";
import axios from "axios";
import { JSONSchema7 } from "json-schema";
import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";

interface formDataType {
  schema: JSONSchema7;
  uiSchema: JSONSchema7;
  indexSchema: JSONSchema7;
}

// minimum number of rows for TextArea
const minRows: number = 12;
// the rest endpoint version
const version: string = "1";

// custom JSX.Element with atlaskit's TextArea
const atlasTextArea = (props: WidgetProps) => {
  return (
    <TextArea
      {...{
        value: props.value,
        minimumRows: minRows,
        required: props.required,
        // call form's onChange function with field value
        onChange: (event) => props.onChange(event.target.value),
      }}
    />
  );
};

// schema structure for json schema form
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

// specifying atlasTextArea as our custom widget
const widgets = {
  atlasTextArea,
};

// specifying ui widgets to use atlasTextArea
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

// validate form and populate form's error messages
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
  // gets context path from atlassian
  // if not found, set to confluence as default
  const contextPath = window.AJS ? window.AJS.contextPath() : "/confluence";

  const [formData, setFormData] = useState<formDataType>();

  // populate form data with schema from the database
  useEffect(() => {
    axios.get(`${contextPath}/rest/idea/${version}/schema`).then((response) =>
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

  // submission feedback to be used to populate atlaskit's section message
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
    axios
      .post(`${contextPath}/rest/idea/${version}/schema`, data)
      .then(() =>
        setSubmissionFeedback({
          title: "Schemas saved Successfully",
          appearance: "confirmation",
          message: "Your schema has been saved successfully",
          hidden: false,
        })
      )
      .catch(() =>
        setSubmissionFeedback({
          title: "Failed to save schemas",
          appearance: "error",
          message: "Could not save schemas",
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
        {/* Submission feedback is hidden unless there is feedback from post request. */}
        <div hidden={submissionFeedback.hidden}>
          <SectionMessage
            title={submissionFeedback.title}
            appearance={submissionFeedback.appearance}
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
