import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";
import axios from "axios";
import TextArea from "@atlaskit/textarea";
import Button from "@atlaskit/button/standard-button";
import Form, { WidgetProps } from "@rjsf/core";
import { JSONSchema7 } from "json-schema";

const atlasTextArea = (props: WidgetProps) => {
  return (
    <TextArea
      minimumRows={12}
      onChange={(e) => console.log(e.target.value)}
      //placeholder={props.placeholder}
      {...{ value: props.value }} //, onChange: props.onChange }}
    />
  );
};

const OuterAdminForm = () => {
  const [schemas, setSchemas] = useState<{
    schema: string;
    uiSchema: string;
    indexSchema: string;
  }>();

  useEffect(() => {
    axios
      .get("http://shouv-box:1990/confluence/rest/idea/1/schema")
      .then((response) =>
        setSchemas({
          schema: response.data.schema,
          uiSchema: response.data.uiSchema,
          indexSchema: response.data.indexSchema,
        })
      );
  }, []);

  console.log(schemas);

  // const formData = {
  //   schema: schemas?.schema,
  //   uiSchema: "this is not undefined", //schemas?.uiSchema,
  //   indexSchema: schemas?.indexSchema,
  // };

  const updateSchema = (type: string, data: string) => {
    console.log("Inside update schema now again");
    axios
      .put("http://shouv-box:1990/confluence/rest/idea/1/schema?type=" + type, {
        data,
      })
      .then((response) => console.log(response.data));
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

  // console.log(widgets);
  // console.log(uiSchema);

  const [errors, setError] = useState<Array<string>>([]);

  const [val, setVal] = useState("{}");
  const changeVal = (e: any) => {
    setVal(e.target.value);

    if (!e || !JSON.parse(e.target.value)) {
      setError([errors[0], errors[1], "Use valid json"]);
      return;
    }
    setError([errors[0], errors[1], ""]);
  };

  console.log(val, errors);

  const submit = () => {
    console.log(val);
  };

  // @ts-ignore
  const onSubmit = ({ formData }) => console.log("Data submitted: ", formData);

  return (
    <div>
      <TextArea minimumRows={12} placeholder="Idea Schema" name="idea-schema" />
      <Button
        appearance="primary"
        onClick={(e) => {
          updateSchema(
            "idea-schema",
            // @ts-ignore
            document.getElementsByName("idea-schema")[0].value
          );
          // @ts-ignore
          document.getElementsByName("idea-schema")[0].value = "";
        }}
      >
        Save
      </Button>
      <TextArea minimumRows={12} placeholder="UI Schema" name="ui-schema" />
      <Button
        appearance="primary"
        onClick={(e) => {
          updateSchema(
            "ui-schema",
            // @ts-ignore
            document.getElementsByName("ui-schema")[0].value
          );
          // @ts-ignore
          document.getElementsByName("ui-schema")[0].value = "";
        }}
      >
        Save
      </Button>
      <TextArea
        minimumRows={12}
        placeholder="Index Page Schema"
        name="index-schema"
        {...{ value: val, onChange: changeVal }}
      />
      <Button
        appearance="primary"
        onClick={(e) => {
          submit();
          updateSchema(
            "index-schema",
            // @ts-ignore
            document.getElementsByName("index-schema")[0].value
          );
          // @ts-ignore
          document.getElementsByName("index-schema")[0].value = "";
        }}
      >
        Save
      </Button>
      <Form
        schema={schema}
        uiSchema={uiSchema}
        widgets={widgets}
        //formData={formData}
        onSubmit={onSubmit}
      >
        <Button
          appearance="primary"
          onClick={(e) => {
            // updateSchema(
            //   "ui-schema",
            //   // @ts-ignore
            //   document.getElementsByName("ui-schema")[0].value
            // );
            // // @ts-ignore
            // document.getElementsByName("ui-schema")[0].value = "";
            // console.log(formData);

            console.log;
          }}
        >
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
