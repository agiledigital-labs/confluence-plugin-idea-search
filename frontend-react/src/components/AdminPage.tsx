import React /*, { useEffect, useState }*/ from "react";
import ReactDOM from "react-dom";
import axios from "axios";
//import DynamicTable from "@atlaskit/dynamic-table";
//import { makeStyles } from "@material-ui/core";
import TextArea from "@atlaskit/textarea";
import Button from "@atlaskit/button/standard-button";
//import { isEmpty } from "lodash";

// interface IdeaPage {
//   owner?: string;
//   status?: string;
//   technologies?: string[];
//   title?: string;
//   description?: string;
//   url?: string;
// }

// const useStyles = makeStyles(() => ({
//   root: {
//     width: "70%",
//   },
//   heading: {
//     justifyContent: "center",
//     display: "flex",
//     fontSize: "larger",
//   },
// }));

const OuterAdminTable = () => {
  //   const classes = useStyles();
  //   console.log(classes);

  //   const [searchTerm, setSearchTerm] = useState({
  //     owner: "",
  //     status: "",
  //     technologies: "",
  //     title: "",
  //     description: "",
  //     url: "",
  //   });

  //   const handleChange = (term: string, value: string) => {
  //     console.log(term.toLowerCase());
  //     setSearchTerm((prevTerm) => ({
  //       ...prevTerm,
  //       [term.toLowerCase()]: value,
  //     }));
  //   };

  //   const [justPages, setJustPages] = useState<Array<IdeaPage>>();

  //   console.log(justPages);

  //   useEffect(() => {
  //     console.log(searchTerm);
  //     axios
  //       .get(
  //         "http://shouv-box:1990/confluence/rest/idea/1/ideaPages?description=" +
  //           searchTerm.description +
  //           "&title=" +
  //           searchTerm.title +
  //           "&status=" +
  //           searchTerm.status +
  //           "&owner=" +
  //           searchTerm.owner
  //       )
  //       .then((response) => setJustPages(response.data));
  //   }, [searchTerm]);

  const updateSchema = (type: string, data: string) => {
    console.log("Inside update schema now again");
    axios
      .put("http://shouv-box:1990/confluence/rest/idea/1/schema", {
        type,
        data,
      })
      .then((response) =>
        console.log("response coming from rest is: " + response.data)
      );
  };

  return (
    <div>
      <TextArea minimumRows={12} placeholder="idea-schema" name="idea-schema" />
      <Button
        appearance="primary"
        onClick={(e) => {
          // @ts-ignore
          console.log(document.getElementsByName("idea-schema")[0].value);
        }}
      >
        Save
      </Button>
      <TextArea minimumRows={12} placeholder="ui-schema" name="ui-schema" />
      <Button
        appearance="primary"
        onClick={(e) => {
          // @ts-ignore
          console.log(document.getElementsByName("ui-schema")[0].value);
        }}
      >
        Save
      </Button>
      <TextArea
        minimumRows={12}
        placeholder="other-schema"
        name="other-schema"
      />
      <Button
        appearance="primary"
        onClick={(e) => {
          // @ts-ignore
          console.log(document.getElementsByName("other-schema")[0].value);

          updateSchema(
            "other-schema",
            // @ts-ignore
            document.getElementsByName("other-schema")[0].value
          );
        }}
      >
        Save
      </Button>
    </div>
  );
};

export default OuterAdminTable;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("admincontainer");
  // @ts-ignore
  wrapper ? ReactDOM.render(<OuterAdminTable />, wrapper) : false;
});
