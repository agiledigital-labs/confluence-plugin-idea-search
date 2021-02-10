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

const rowsPerPage: number = 10;
const defaultPage: number = 1;

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
  const contextPath = window.AJS ? window.AJS.contextPath() : "/confluence";

  const [searchTerm, setSearchTerm] = useState({
    owner: "",
    status: "",
    technologies: "",
    title: "",
    description: "",
    url: "",
  });

  const handleChange = (term: string, value: string) => {
    setSearchTerm((prevTerm) => ({
      ...prevTerm,
      [term.toLowerCase()]: value,
    }));
  };

  const [justPages, setJustPages] = useState<Array<IdeaPage>>();

  useEffect(() => {
    axios
      .get(
        `${contextPath}/rest/idea/1/ideapages?description=` +
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
          content: <a href={`${contextPath}/${page.url}`}>{page.title}</a>,
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
            <a href={`${contextPath}/display/~${page.owner}`}>
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
          <Textfield
            id={`${header}`}
            placeholder={header}
            className={classes.root}
            onChange={(e) => {
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
        rowsPerPage={rowsPerPage}
        defaultPage={defaultPage}
        loadingSpinnerSize="large"
        isLoading={false}
        isFixedSize
        defaultSortKey="Title"
        defaultSortOrder="ASC"
      />
    </div>
  );
};

export default OuterTable;

window.addEventListener("load", function () {
  const wrapper = document.getElementById("container");
  // @ts-ignore
  wrapper ? ReactDOM.render(<OuterTable />, wrapper) : false;
});
