import React from "react";
import ReactDOM from "react-dom";
import axios from "axios";
import TextArea from "@atlaskit/textarea";
import Button from "@atlaskit/button/standard-button";

const OuterAdminForm = () => {
  const updateSchema = (type: string, data: string) => {
    console.log("Inside update schema now again");
    axios
      .put("http://shouv-box:1990/confluence/rest/idea/1/schema?type=" + type, {
        data,
      })
      .then((response) => console.log(response.data));
  };

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
      />
      <Button
        appearance="primary"
        onClick={(e) => {
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
    </div>
  );
};

export default OuterAdminForm;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("admincontainer");
  // @ts-ignore
  wrapper ? ReactDOM.render(<OuterAdminForm />, wrapper) : false;
});
