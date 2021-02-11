import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";
import axios from "axios";
import DynamicTable from "@atlaskit/dynamic-table";
import { makeStyles } from "@material-ui/core";
import Textfield from "@atlaskit/textfield";
import { isEmpty } from "lodash";

interface IdeaPage {
  owner?: string;
  status?: string;
  technologies?: string[];
  title?: string;
  description?: string;
  url?: string;
}

const useStyles = makeStyles(() => ({
  root: {
    width: "100%",
  },
  heading: {
    justifyContent: "center",
    display: "flex",
    fontSize: "larger",
  },
}));

const OuterTable = () => {
  const classes = useStyles();

  const [searchTerm, setSearchTerm] = useState({
    owner: "",
    status: "",
    technologies: "",
    title: "",
    description: "",
    url: "",
  });

  const handleChange = (term: string, value: string) => {
    console.log(term.toLowerCase());
    setSearchTerm((prevTerm) => ({
      ...prevTerm,
      [term.toLowerCase()]: value,
    }));
  };

  const [justPages, setJustPages] = useState<Array<IdeaPage>>();

  useEffect(() => {
    console.log(searchTerm);
    axios
      .get(
        "http://wren:1990/confluence/rest/idea/1/ideaPages?description=" +
          searchTerm.description +
          "&title=" +
          searchTerm.title +
          "&status=" +
          searchTerm.status +
          "&owner=" +
          searchTerm.owner
      )
      .then((response) => setJustPages(response.data));
  }, [searchTerm]);

  const rows = justPages
    ?.filter(
      (page) =>
        // check if there is at least one tech in the list containing search
        !isEmpty(
          page.technologies?.filter((tech) =>
            // check if searchterm is in the tech name
            tech.toLowerCase().includes(searchTerm.technologies.toLowerCase())
          )
        )
    )
    .map((page: IdeaPage) => ({
      key: `row-${page.title}`,
      cells: [
        {
          key: `cell-${page.title}`,
          content: <a href={page.url}>{page.title}</a>,
        },
        {
          key: `cell-${page.description}`,
          content: page.description,
        },
        {
          key: `cell-${page.technologies}`,
          content: page.technologies?.join(", "),
        },
        {
          key: `cell-${page.status}`,
          content: page.status,
        },
        {
          key: `cell-${page.owner}`,
          content: (
            <a href={"http://wren:1990/confluence/display/~" + page.owner}>
              {"@" + page.owner}
            </a>
          ),
        },
      ],
    }));

  const headers = ["Title", "Description", "Technologies", "Status", "Owner"];

  const head = {
    cells: headers.map((header) => ({
      key: header,
      content: (
        <div>
          <div className={classes.heading}>{header}</div>
          {console.log(header)}
          <Textfield
            id={`${header}asdf`}
            placeholder={header}
            className={classes.root}
            onBlur={(e) => console.log(e.target.value)}
            onChange={(e) => {
              console.log(e.currentTarget.value);
              // @ts-ignore
              handleChange(header, e.target.value);
            }}
          />
        </div>
      ),
    })),
  };

  return (
    <div>
      <DynamicTable
        head={head}
        rows={rows}
        rowsPerPage={10}
        defaultPage={1}
        loadingSpinnerSize="large"
        isLoading={false}
        isFixedSize
        defaultSortKey="Title"
        defaultSortOrder="ASC"
        onSort={() => console.log("onSort")}
        onSetPage={() => console.log("onSetPage")}
      />
    </div>
  );
};

export default OuterTable;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("index-container");
  // @ts-ignore
  wrapper ? ReactDOM.render(<OuterTable />, wrapper) : false;
});
