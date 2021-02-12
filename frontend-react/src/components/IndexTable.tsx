import DynamicTable from "@atlaskit/dynamic-table";
import Textfield from "@atlaskit/textfield";
import { makeStyles } from "@material-ui/core";
import axios from "axios";
import { isEmpty } from "lodash";
import queryString from "query-string";
import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom";

interface IdeaPage {
  owner?: string;
  status?: string;
  technologies?: string[];
  title?: string;
  description?: string;
  url?: string;
}

const AJS = window.AJS ? window.AJS : undefined;

// rows to be shown on each page of the paginated table
const rowsPerPage: number = 10;
// the default rendered page for paginated table
const defaultPage: number = 1;
// the rest endpoint version
const version: string = "1";

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
  // gets context path from atlassian
  // if not found, set to confluence as default
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "/confluence";

  // search term will be empty fields on initial render
  const [searchTerm, setSearchTerm] = useState({
    owner: "",
    status: "",
    technologies: "",
    title: "",
    description: "",
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
        `${contextPath}/rest/idea/${version}/ideapages?${queryString.stringify(
          searchTerm
        )}`
      )
      .then((response) => setJustPages(response.data));
  }, [searchTerm, contextPath]);

  const rows = justPages
    ?.filter(
      (page) =>
        // check if there is at least one tech in the list containing search
        !isEmpty(
          page.technologies?.filter((tech) =>
            // check if search term is in the tech name
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
            // specifying className to use useStyle() for css
            className={classes.root}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
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
