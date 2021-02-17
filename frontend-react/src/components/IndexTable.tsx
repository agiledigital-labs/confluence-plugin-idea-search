import DynamicTable from "@atlaskit/dynamic-table";
import Textfield from "@atlaskit/textfield";
import { makeStyles } from "@material-ui/core";
import axios from "axios";
import queryString from "query-string";
import React, { useEffect, useState } from "react";
import { FormDataType, version } from "./index";
import { get, startCase } from "lodash/fp";

type IdeaPage = {
  creator: {
    key: { userkey: string };
    name: string;
    lowerName: string;
  };
  indexData: Array<string>;
  title: string;
  indexSchema: { index: Array<string> };
  url: string;
};

const AJS = window.AJS ? window.AJS : undefined;

// rows to be shown on each page of the paginated table
const rowsPerPage: number = 10;
// the default rendered page for paginated table
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
  // gets context path from atlassian
  // if not found, set to confluence as default
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "/confluence";

  const [formData, setFormData] = useState<FormDataType>();

  // populate form data with schema from the database
  useEffect(() => {
    axios.get(`${contextPath}/rest/idea/${version}/schema`).then((response) =>
      setFormData({
        indexSchema: JSON.parse(response.data.indexSchema),
      })
    );
  }, []);

  const initSearch = formData?.indexSchema?.index?.reduce(
    (pre, cur) => ({ ...pre, [cur]: "" }),
    {}
  );
  // search term will be empty fields on initial render
  const [searchTerm, setSearchTerm] = useState(initSearch);

  const handleChange = (term: string, value: string) => {
    console.log(term, value);
    setSearchTerm({
      ...searchTerm,
      [term.toLowerCase()]: value,
    });
  };

  const [justPages, setJustPages] = useState<Array<IdeaPage>>();

  useEffect(() => {
    axios
      .get(
        `${contextPath}/rest/idea/${version}/ideapages?${queryString.stringify(
          searchTerm ? searchTerm : {}
        )}`
      )
      .then((response) => setJustPages(response.data));
  }, [searchTerm, contextPath]);

  const row = (page: IdeaPage) =>
    formData?.indexSchema?.index
      ? formData.indexSchema.index.map((item, index) => ({
          key: `cell-${item}`,
          content: get(index)(page.indexData),
        }))
      : [];

  const rows = justPages?.map((page: IdeaPage) => ({
    key: `row-${page.title}`,
    cells: [
      {
        key: `cell-${page.title}`,
        content: <a href={page.url}>{page.title}</a>,
      },
      ...row(page),
    ],
  }));

  console.log(formData?.indexSchema?.index);

  if (!formData?.indexSchema?.index) {
    return <>loading...</>;
  }
  const headersList = ["Title", ...formData.indexSchema.index];

  const head = (headers: Array<string>) => ({
    cells: headers.map((header) => ({
      key: header,
      content: (
        <div>
          <div className={classes.heading}>{startCase(header)}</div>
          <Textfield
            id={header}
            placeholder={header}
            // specifying className to use useStyle() for css
            className={classes.root}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
              console.log(e);
              handleChange(header, e.target.value);
            }}
          />
        </div>
      ),
    })),
  });

  return (
    <div>
      <DynamicTable
        head={head(headersList)}
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
