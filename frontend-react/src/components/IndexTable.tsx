import DynamicTable from "@atlaskit/dynamic-table";
import Textfield from "@atlaskit/textfield";
import { makeStyles } from "@material-ui/core";
import axios from "axios";
import queryString from "query-string";
import React, { useEffect, useState } from "react";
import { FormDataType, IndexItem, version } from "./index";
import { startCase, flow, set, isNil, omitBy } from "lodash/fp";
import { lowerCase } from "lodash";

type IdeaPage = {
  creator: {
    key: { userkey: string };
    name: string;
    lowerName: string;
  };
  indexData: Array<IndexItem>;
  title: string;
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

  const order: string[] | undefined = formData?.indexSchema?.index?.map(
    (item) => item.key
  );

  const content = (page: IdeaPage) =>
    order
      ? order
          .map((itemKey) => page.indexData.find((inx) => inx.key === itemKey))
          .map((row) => ({
            key: `cell-${row?.key}`,
            content: row?.value,
          }))
      : [];

  const rows = justPages?.map((page: IdeaPage) => ({
    key: `row-${page.title}`,
    cells: content(page),
  }));

  if (!formData?.indexSchema?.index) {
    return <>loading...</>;
  }
  const headersList = formData.indexSchema.index.map(
    (indexSchemaItem, index) => ({
      key: indexSchemaItem.key,
      source: `${
        lowerCase(indexSchemaItem.type) !== "static"
          ? indexSchemaItem.type
          : indexSchemaItem.key
      }${
        lowerCase(indexSchemaItem.type) !== "static"
          ? indexSchemaItem.index
          : ""
      }`,
    })
  );

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
