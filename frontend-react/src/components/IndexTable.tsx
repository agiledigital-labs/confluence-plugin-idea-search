import DynamicTable from "@atlaskit/dynamic-table";
import Textfield from "@atlaskit/textfield";
import { makeStyles } from "@material-ui/core";
import axios from "axios";
import queryString from "query-string";
import React, { useEffect, useState } from "react";
import { FormDataType, version } from "./index";
import { startCase, flow, set, isNil, omitBy } from "lodash/fp";

type IdeaPage = {
  creator: {
    key: { userkey: string };
    name: string;
    lowerName: string;
  };
  indexData: {
    index: number;
    type: "string" | "number";
    value: string | number;
  }[];
  title: string;
  indexSchema: { stringIndex: Array<string>; numberIndex: Array<number> };
  url: string;
};

const AJS = window.AJS ? window.AJS : undefined;

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
  const contextPath = AJS?.contextPath() ? AJS.contextPath() : "";

  // rows to be shown on each page of the paginated table
  const rowsPerPage = 10;

  const [formData, setFormData] = useState<FormDataType>();

  const [isLoading, setIsLoading] = useState(true);

  // populate form data with schema from the database
  useEffect(() => {
    axios.get(`${contextPath}/rest/idea/${version}/schema`).then((response) => {
      setFormData({
        indexSchema: JSON.parse(response.data.indexSchema),
      });
      setIsLoading(false);
    });
  }, []);

  // search term will be empty fields on initial render
  const [searchTerm, setSearchTerm] = useState<{
    [key: string]: string | number;
  }>();

  const handleChange = (term: string, value: string | number) =>
    flow(
      set(term.toLowerCase(), value),
      omitBy(!isNil),
      setSearchTerm
    )(searchTerm);

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
    formData?.indexSchema?.stringIndex && formData?.indexSchema?.numberIndex
      ? [
          ...formData.indexSchema.stringIndex.map((item, index) => ({
            key: `cell-${item}`,
            content: page.indexData.find(
              (v) => v.index === index && v.type === "string"
            )?.value,
          })),
          ...formData.indexSchema.numberIndex.map((item, index) => ({
            key: `cell-${item}`,
            content: page.indexData.find(
              (v) => v.index === index && v.type === "number"
            )?.value,
          })),
        ]
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

  if (
    !formData?.indexSchema?.stringIndex ||
    !formData.indexSchema.numberIndex
  ) {
    return <>loading...</>;
  }
  const headersList = [
    { key: "Title", source: "title" },
    ...formData.indexSchema.stringIndex.map((key, index) => ({
      key,
      source: `string${index}`,
    })),
    ...formData.indexSchema.numberIndex.map((key, index) => ({
      key,
      source: `number${index}`,
    })),
  ];

  const head = (headers: { key: string; source: string }[]) => ({
    cells: headers.map(({ key: header, source }) => ({
      key: header,
      content: (
        <div>
          <div className={classes.heading}>{startCase(header)}</div>
          <Textfield
            id={header}
            placeholder={header}
            // specifying className to use useStyle() for css
            className={classes.root}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              handleChange(source, e.target.value)
            }
          />
        </div>
      ),
    })),
  });

  return (
    <div>
      <DynamicTable
        isLoading={isLoading}
        head={head(headersList)}
        rows={rows}
        emptyView={<>loading...</>}
        rowsPerPage={rowsPerPage}
        defaultPage={defaultPage}
        loadingSpinnerSize="large"
        isFixedSize
        defaultSortKey="Title"
        defaultSortOrder="ASC"
      />
    </div>
  );
};

export default OuterTable;
